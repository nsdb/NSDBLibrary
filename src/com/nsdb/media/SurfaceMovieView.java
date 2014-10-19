package com.nsdb.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 영상을 간편하게 재생할 수 있는 뷰입니다. showThumbnail 메서드와 showMovie 메서드로 썸네일 상태와 영상 상태로 전환할 수 있습니다.
 * resumeMovie, pauseMovie 를 통해 영상의 일시정지와 재개 또한 가능합니다.
 * @author NSDB
 * @see MovieListener
 *
 */
public class SurfaceMovieView extends FrameLayout {

	private static final String TAG = SurfaceMovieView.class.getSimpleName();
	// layout
	private ProgressBar indicator;
	private SurfaceView surface;
	private ImageView thumbnail;
	private ImageButton button;
	private View leftCover;
	private View rightCover;
	// movie play
	private String path;
	private MediaPlayer mp;
	private boolean prepared;
	private boolean surfaceInited;
	// listener
	private MovieListener listener;
	
	
	public SurfaceMovieView(Context context) {
		super(context);
		init();
	}
	public SurfaceMovieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SurfaceMovieView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init() {
		// surface
		resetSurface();
		
		LinearLayout coverGroup = new LinearLayout(getContext());
		coverGroup.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams coverGroupParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		addView(coverGroup, coverGroupParams);
		
		// Left cover
		leftCover = new View(getContext());
		LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
		leftCover.setBackgroundColor(Color.BLACK);
		leftCover.setVisibility(View.GONE);
		coverGroup.addView(leftCover, leftParams);
		
		// thumbnail
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 6);
		thumbnail=new ImageView(getContext());
		thumbnail.setScaleType(ScaleType.CENTER_CROP);
		thumbnail.setVisibility(View.INVISIBLE);
		coverGroup.addView(thumbnail,params);
		
		// Right cover
		rightCover = new View(getContext());
		LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
		rightCover.setBackgroundColor(Color.BLACK);
		rightCover.setVisibility(View.GONE);
		coverGroup.addView(rightCover, rightParams);
		
		// button
		LayoutParams buttonParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		buttonParams.gravity=Gravity.CENTER;
		button=new ImageButton(getContext());
		button.setOnClickListener(clickListener);
		button.setBackgroundResource(0);
		addView(button,buttonParams);

		// indicator
		indicator=new ProgressBar(getContext(),null,android.R.attr.progressBarStyleLarge);
		indicator.setVisibility(View.GONE);
		LayoutParams indicatorParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		indicatorParams.gravity=Gravity.CENTER;
		addView(indicator,indicatorParams);
		
		
		
		// path
		path="";
	}
	/**
	 * thumbnail을 SurfaceView 위에서 치운 후에 영상이 실제로 시작되기 전까지, SurfaceView는 아무 동작도 하지 않는채로 사용자에게 노출되게 됩니다.
	 * SurfaceView를 처음 사용할 때는 검은 화면이라 비교적 자연스럽지만, 이전에 재생한 영상이 있다면 그 영상의 잔상이 보이게 됩니다.
	 * 이는 꽤 부자연스럽기 때문에 이러한 상황에 한정해서 SurfaceView를 새로 만드는 작업을 진행합니다. <br>
	 */
	@SuppressWarnings("deprecation")
	private void resetSurface() {
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		if(surface!=null) removeView(surface);
		surface=new SurfaceView(getContext());
		surface.setOnClickListener(clickListener);
		if(Build.VERSION.SDK_INT<11)
			surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surface.getHolder().addCallback(surfaceStateCallback);
		surface.getHolder().addCallback(playCallback);
		addView(surface, 0, params);
	}
	private SurfaceHolder.Callback surfaceStateCallback=new SurfaceHolder.Callback() {
		@Override public void surfaceCreated(SurfaceHolder holder) {}
		@Override public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG,"Surface Destroyed");
			surfaceInited=false;
		}		
		@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
			Log.i(TAG,"Surface Available : " + width + "x" + height);
			surfaceInited=true;
		}
	};
	private SurfaceHolder.Callback playCallback=new SurfaceHolder.Callback() {
		@Override public void surfaceCreated(SurfaceHolder holder) {}
		@Override public void surfaceDestroyed(SurfaceHolder holder) {}		
		@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
			if(!path.equals("")) preparePlayer();
			surface.getHolder().removeCallback(playCallback);
		}
	};
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.i(TAG,"onDetachedFromWindow");
		restoreMovie();
	};
	
	
	// thumbnail
	/**
	 * 썸네일을 보여줍니다. 재생되고 있던 영상은 중지됩니다.
	 * @param b 비트맵
	 */
	public void showThumbnail(Bitmap b) {
		Log.i(TAG,"Show thumbnail");
		restoreMovie();
		thumbnail.setImageBitmap(b);
		thumbnail.setVisibility(View.VISIBLE);
	}
	
	// Cover
	/**
	 * 640x480 해상도로 촬영된 영상을 480x480 해상도로 보여주기 위해서 사용하는 커버입니다.
	 * @param color 커버 색
	 */
	public void showCover(int color) {
		leftCover.setVisibility(View.VISIBLE);
		rightCover.setVisibility(View.VISIBLE);
		leftCover.setBackgroundColor(color);
		rightCover.setBackgroundColor(color);
	}
	
	// movie play
	/**
	 * 영상을 보여줍니다. 표시되어있던 썸네일은 사라지며, 어느정도 시간이 지난후에 (prepare) 영상이 재생됩니다.<br>
	 * 리스너의 onPrepared 메서드를 통해 영상이 시작될 시점을 어느정도 알아낼 수 있습니다.
	 * @param path 영상 경로
	 */
	public void showMovie(String path) {
		Log.i(TAG,"Show movie");
		// same path check
		if(path==null || path.equals("")) {
			Log.w(TAG,"Empty path");
			return;
		} else if(this.path.equals(path) && mp!=null) {
			if(prepared) resumeMovie();
			else Log.i(TAG,"Player is now preparing...");
			return;
		}
		// clear
		restoreMovie();
		this.path=path;
		if(thumbnail.getVisibility()==View.VISIBLE) {
			resetSurface();
		} else if(surfaceInited){
			preparePlayer();
		}
	}
	private void preparePlayer() {
		Log.i(TAG,"Prepare media player");
		// start
		try {
			// Note : setVisibility(View.INVISIBLE) not working at View on SurfaceView
			thumbnail.setVisibility(View.INVISIBLE);
			button.setVisibility(View.GONE);
			indicator.setVisibility(View.VISIBLE);
			mp=new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setOnPreparedListener(prepareListener);
			mp.setOnCompletionListener(completionListener);
			mp.setDisplay(surface.getHolder());
			mp.setDataSource(path);
			mp.prepareAsync();
		} catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG,"Failed to update movie");
			restoreMovie();
		}
	}
	private OnPreparedListener prepareListener=new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer arg0) {
			Log.i(TAG,"onPrepare");
			prepared=true;	
			mp.start();
			indicator.setVisibility(View.GONE);
			if(listener != null) {
				listener.onPrepared(SurfaceMovieView.this);
			}
		}
	};
	private OnCompletionListener completionListener=new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer m) {
			Log.i(TAG,"onCompletion");
			restoreMovie();
			if(listener instanceof MovieListener)
				listener.onCompletion(SurfaceMovieView.this);			
		}
	};
	private OnClickListener clickListener=new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Log.i(TAG,"onClick");
			if(listener instanceof MovieListener)
				listener.onClick(SurfaceMovieView.this);			
		}
	};
	
	
	/**
	 * 재생되고 있던 영상이 있다면 일시정지합니다.
	 */
	public void pauseMovie() {
		Log.i(TAG,"pause");
		if(mp!=null && prepared) {
			mp.pause();		
			button.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 재생되고 있던 영상이 있다면 다시 재생합니다.
	 */
	public void resumeMovie() {
		Log.i(TAG,"resume");
		if(mp!=null && prepared) {
			mp.start();		
			button.setVisibility(View.GONE);
		}
	}	
	/**
	 * 영상을 정리하고 이전에 지정한 썸네일을 보여줍니다. 영상에 관련된 설정은 모두 초기화됩니다.
	 */
	public void restoreMovie() {
		Log.i(TAG,"restore");
		if(mp!=null) {
			mp.release();
			mp=null;
		}
		prepared=false;
		button.setVisibility(View.VISIBLE);
		indicator.setVisibility(View.GONE);
		path="";
	}
	
	/**
	 * 재생버튼의 그림을 바꿉니다.
	 * @param resID 리소스ID
	 */
	public void setPlayButtonImage(int resID) {
		button.setImageResource(resID);
	}
	
	
	public void setListener(MovieListener listener) {
		this.listener=listener;
	}
	/**
	 * 이 객체의 상태 변화를 전달받기 위한 리스너입니다.
	 * @author NSDB
	 *
	 */
	public interface MovieListener {
		public void onPrepared(SurfaceMovieView v);
		public void onClick(SurfaceMovieView v);
		public void onCompletion(SurfaceMovieView v);
	}

	public boolean isPreparing() { return mp!=null && !prepared; }
	public boolean isPrepared() { return mp!=null && prepared; }
	public boolean isPlaying() { return mp!=null && prepared && mp.isPlaying(); }
	public int getCurrentPosition() {
		if(isPrepared()) return mp.getCurrentPosition();
		else return 0;
	}
	public int getDuration() {
		if(isPrepared()) return mp.getDuration();
		else return 0;
	}

}
