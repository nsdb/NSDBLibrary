package com.nsdb.spinner;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.FrameLayout.LayoutParams;

public class ListPopupWindow {

	private ArrayList<View> viewList;
	private LinearLayout listLayout;
	private PopupWindow popup;
	private int itemWidth, itemHeight;
	private int offsetX, offsetY;
	private OnPopupItemClickListener listener;
	
	public ListPopupWindow(Context c) {
		viewList = new ArrayList<View>();
		listLayout = new LinearLayout(c);
		listLayout.setOrientation(LinearLayout.VERTICAL);
		listLayout.setGravity(Gravity.CENTER);
		listLayout.setBackgroundColor(0xFFFFFFFF);
		popup = new PopupWindow(listLayout);
	    popup.setBackgroundDrawable(new BitmapDrawable());
	    popup.setOutsideTouchable(true);
		
	}
	
	public void addView(View v) {
		v.setOnClickListener(viewClickListener);
		viewList.add(v);
		listLayout.addView(v);
	}
	
	public void setItemSize(int itemWidth, int itemHeight) {
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		for(View v : viewList) {
			v.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
		}
	}
	public void setOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
	}
	
	public void show(View anchor) {
		popup.setWidth(itemWidth);
		popup.setHeight(itemHeight*viewList.size());
		popup.showAsDropDown(anchor,offsetX,offsetY);
	}
	
	public void dismiss() {
		popup.dismiss();
	}
	
	private OnClickListener viewClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(listener != null) {
				for(int i=0;i<viewList.size();i++) {
					if(v==viewList.get(i)) listener.onPopupItemClick(ListPopupWindow.this, i);
				}
			}
			popup.dismiss();
		};
	};
	
	public void setOnPopupItemClickListener(OnPopupItemClickListener l) {
		listener = l;
	}
	public interface OnPopupItemClickListener {
		public void onPopupItemClick(ListPopupWindow window, int position);
	}

}
