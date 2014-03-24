package com.nsdb.view;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


/**
 * 로컬에 존재하는 이미지를 로드해서 원하는 뷰에 적용시켜주는 객체입니다.
 * 불러온 그림들은 requestRemoveImage 메서드 또는 clearImageList 메서드를 호출하지 않는한 계속해서 메모리에 남아있으므로 효율적인 관리가 필요합니다.<br>
 * 또한 스레드를 사용하므로 사용이 끝났을 때에 restore 메서드를 불러주는 것도 잊지 마십시요.
 * @author NSDB
 *
 */
public class LocalImageLoader extends Thread {

	private static final String TAG = LocalImageLoader.class.getSimpleName();
	private ArrayList<LoadSet> imageList;
	private LinkedList<LoadSet> addQueue;
	private boolean restored;
	private Handler handler = new Handler();
	
	public LocalImageLoader() {
		imageList=new ArrayList<LoadSet>();
		addQueue=new LinkedList<LoadSet>();
	}
	
	private class LoadSet {
		public ImageView view;
		public String path;
		public ProgressBar indicator;
		public Bitmap thumb;
		public LoadSet(ImageView view,String path,ProgressBar indicator) {
			this.view=view;
			this.path=path;
			this.indicator=indicator;
			this.thumb=null;
		}
	}	
	
	// add request
	/**
	 * 이미지 로드를 요청합니다.
	 * @param view 적용할 뷰
	 * @param path 이미지 경로
	 * @param indicator 진행상황을 표시할 프로그레스 바
	 */
	public void requestLoadImage(ImageView view,String path,ProgressBar indicator) {
		Log.i(TAG,"Request load : "+view);
		LoadSet data;
		synchronized(imageList) {
			// search
			int num=findDataIndexByView(view);
			if(num!=-1) {
				if(imageList.get(num).thumb!=null) view.setImageBitmap(imageList.get(num).thumb);
				return;
			}
			// add to list
			data=new LoadSet(view,path,indicator);
			imageList.add(data);
		}
		// add to thread
		if(indicator!=null)
			indicator.setVisibility(View.VISIBLE);
		synchronized(addQueue) {
			addQueue.add(data);
			addQueue.notify();
			Log.i(TAG,"Added to queue : "+view);
		}
	}
	/**
	 * 해당 뷰가 요청했던 이미지의 제거를 요청합니다.
	 * @param view 이미지를 요청했던 뷰
	 */
	public void requestRemoveImage(ImageView view) {
		Log.i(TAG,"Request remove : "+view);
		// search and remove
		synchronized(imageList) {
			int num=findDataIndexByView(view);
			if(num==-1) return;
			LoadSet data=imageList.get(num);
			if(data.thumb!=null) {
				data.thumb.recycle();
				data.thumb=null;
				data.view.setImageBitmap(null);
				Log.i(TAG,"Image removed : "+view);
			}
			if(data.indicator!=null)
				data.indicator.setVisibility(View.GONE);
			imageList.remove(data);
		}
	}
	/**
	 * 로드했던 모든 이미지를 제거합니다
	 */
	public void clearImageList() {
		Log.i(TAG,"Clear image list");
		// empty queue
		synchronized(addQueue) {
			while(addQueue.size()>0)
				addQueue.poll();
		}
		// empty list
		synchronized(imageList) {
			LoadSet data=null;
			for(int i=0;i<imageList.size();i++) {
				data=imageList.get(i);
				if(data.thumb!=null) {
					data.thumb.recycle();
					data.thumb=null;
//					data.view.setImageBitmap(null);
				}
//				if(data.indicator!=null) {
//					data.indicator.setVisibility(View.GONE);
//					data.indicator.clearAnimation();
//				}
				imageList.remove(i--);
			}
		}
		System.gc();
	}
	
	@Override
	public void run() {
		super.run();
		// load image
		LoadSet data=null;
		Bitmap thumb=null;
		Log.i(TAG,"Image loader thread started");
		while(!restored) {
			try {
				// get data
				synchronized(addQueue) {
					data=addQueue.poll();
				}

				if(data != null) {
					// load bitmap
					synchronized(imageList) {
						if(findDataIndexByView(data.view)==-1) continue;
					}
					Log.i(TAG,"Load bitmap : "+data.view);
					thumb=BitmapFactory.decodeFile(data.path);
					// apply to data
					synchronized(imageList) {
						if(findDataIndexByView(data.view)==-1) {
							if(thumb!=null)
								thumb.recycle();
							continue;
						}
						else {
							Log.i(TAG,"Set bitmap : "+data.view);
							data.thumb=thumb;
						}
					}
					// apply to view
					handler.post(new ImageRunnable(data));
				}
				
				try {
					synchronized (addQueue) {
						
						if(addQueue.size() > 0) {
							continue;
						}
						
						addQueue.wait();
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// restore image
		synchronized(imageList) {
			for(LoadSet d : imageList) {
				if(d.thumb!=null)
					d.thumb.recycle();
			}
		}
		Log.i(TAG,"Image loader thread end");
	}
	private class ImageRunnable implements Runnable {
		private LoadSet data;
		public ImageRunnable(LoadSet data) {
			this.data=data;
		}
		@Override
		public void run() {
			Log.i(TAG,"Apply to view : "+data.view);
			if(data.indicator!=null)
				data.indicator.setVisibility(View.GONE);
			try {
				data.view.setImageBitmap(data.thumb);
			} catch (Exception e) {
			}
		}
	}
	

	/**
	 * 이 객체를 정리합니다. 사용을 끝내면 호출해주십시요
	 */
	public void restore() { 
		Log.i(TAG,"Restore");
		restored=true;
		synchronized (addQueue) {
			addQueue.notify();
		}
	}
	
	
	private int findDataIndexByView(ImageView view) {
		for(int i=0;i<imageList.size();i++)
			if(imageList.get(i).view==view)
				return i;
		return -1;
	}
}
