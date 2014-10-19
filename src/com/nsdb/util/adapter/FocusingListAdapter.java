package com.nsdb.util.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 사용자가 지정한 위치에 리스트의 아이템이 올 경우, 그 아이템을 '포커스'시켜주는 리스트뷰입니다.
 * 포커스될 시에 onFocusView 메서드가 기존의 getView와 다른 절차를 거쳐서 호출되며, 아이템이 포커스를 떠날 경우에는 onDefocusView가 호출됩니다.
 * 단, 마지막으로 리스트뷰에서 제거된 아이템의 경우 onDefocusView가 불리지 않으니 처리에 주의해주십시요.<br>
 * Header, Footer View는 영향을 받지 않습니다.<br>
 * <br>
 * 생성시에 바로 listView.setAdapter를 통해 자기 자신을 등록하고, onScrollListener와 RecyclerListener도 함께 등록합니다.
 * (onScroll, onMovedToScrapHeap 등을 사용하려면 오버라이딩하면 됩니다)<br>
 * @author NSDB
 */
public abstract class FocusingListAdapter<T> extends ArrayAdapter<T> implements OnScrollListener, RecyclerListener {
	
	private static final String TAG = FocusingListAdapter.class.getSimpleName();
	private ListView listView;
	private int layoutID;
	private int scrollState;
	private float focusLinePosition;
	
	/**
	 * @param focusLinePosition [0,1]의 값을 가집니다. 0.5일 경우, 중앙에 위치한 아이템이 포커스됩니다.
	 */
	public FocusingListAdapter(Context context,ListView listView,int layoutID,float focusLinePosition) {
		super(context,layoutID);
		this.listView=listView;
		this.layoutID=layoutID;
		listView.setAdapter(this);
		listView.setOnScrollListener(this);
		listView.setRecyclerListener(this);
		this.focusLinePosition=focusLinePosition;	
	}
	
	
	
	// update view
	@Override
	public View getView(int position,View v,ViewGroup parent) {			
		// create view
		Log.i(TAG,"getView : "+position+", "+v);
		if(position<0 || position>=getCount()) return null;
		if(v==null) {
			v=((LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
			.inflate(layoutID,null);
			Log.i(TAG,"View Created : "+v);
		}
		// update
		onUpdateView(position,v);
		return v;
	}
	/**
	 * getView와 유사한 용도로 사용되는 메서드입니다.
	 * @param position 데이터의 배열 위치
	 * @param v 데이터의 정보를 적용할 뷰
	 * @see #getView(int,View,ViewGroup)
	 */
	protected abstract void onUpdateView(int position, View v);

	// focus update
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//Log.i(TAG,"onScroll : "+firstVisibleItem+", "+visibleItemCount+", "+totalItemCount);
		if(scrollState==0) focusCheck();
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.i(TAG,"ScrollState : "+scrollState);
		// if scroll is stopped
		this.scrollState=scrollState;
		if(scrollState==0)
			focusCheck();
	}
	@Override
	public void onMovedToScrapHeap(View view) {
		Log.i(TAG,"Defocused from recycler : "+view);
		onDefocusView(view);
	}
	/**
	 * 해당 리스트 아이템이 포커스되었을 때 호출되는 메서드입니다. 반복해서 호출될 수 있음에 주의하십시요.
	 * @param position 데이터의 배열 위치
	 * @param v 데이터의 정보를 적용할 뷰
	 */
	protected abstract void onFocusView(int position, View v);
	/**
	 * 해당 리스트 아이템이 디포커스되었을 때 호출되는 메서드입니다. 반복해서 호출될 수 있음에 주의하십시요.
	 * @param v 디포커스된 뷰
	 */
	protected abstract void onDefocusView(View v);
	
	
	
	// check what item is focused (Be careful to use it while data is being modified, such as clear(), remove(T))
	private void focusCheck() {
		Log.i(TAG,"Focus check");
		// find what item is in center
		if(listView.getChildCount()==0) return;
		else if(listView.getChildCount()==1) {
			if(listView.getFirstVisiblePosition()<0 || listView.getFirstVisiblePosition()>=getCount()) return;
			Log.i(TAG,"Focused : "+listView.getFirstVisiblePosition()+", "+listView.getChildAt(0));
			onFocusView(listView.getFirstVisiblePosition(),listView.getChildAt(0));
			return;
		}
		int focusLine=Math.round( listView.getHeight()*focusLinePosition );
		View child;
		// determine what view is focused
		for(int i=0;i<listView.getChildCount();i++) {
			if(listView.getFirstVisiblePosition()+i<0) continue;
			if(listView.getFirstVisiblePosition()+i>=getCount()) break;
			child=listView.getChildAt(i);
			if(child.getTop()<=focusLine && child.getBottom()>focusLine) {
				Log.i(TAG,"Focused : "+(listView.getFirstVisiblePosition()+i)+", "+child);
				onFocusView(listView.getFirstVisiblePosition()+i,child);
			} else {
				Log.i(TAG,"Defocused : "+(listView.getFirstVisiblePosition()+i)+", "+child);
				onDefocusView(child);
			}
		}		
	}
	
	
	// getter
	public ListView getListView() { return listView; }
	protected List<View> getChildren() {
		ArrayList<View> list=new ArrayList<View>();
		for(int i=0;i<getListView().getChildCount();i++)
			list.add(getListView().getChildAt(i));
		return list;
	}
}
