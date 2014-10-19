package com.nsdb.cm.view;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 뷰의 크기에 관련된 메서드를 쉽게 사용할 수 있도록 모아둔 객체입니다.
 * @author NSDB
 *
 */
public class ViewRegulator {

	public static void regulateBasedDisplayWidth(View view, ViewGroup.LayoutParams params, int width, int height) {
		regulateBasedDisplayWidth(view,params,width,height,0);
	}
	public static void regulateBasedDisplayWidth(View view, ViewGroup.LayoutParams params, int width, int height, int marginW) {
		Display display=((WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point p=DisplayUtils.getDisplaySize(display);
		regulateBasedWidth(view,params,p.x-marginW*2,width,height);
	}
	public static void regulateBasedDisplayHeight(View view, ViewGroup.LayoutParams params, int width, int height) {
		regulateBasedDisplayHeight(view,params,width,height,0);
	}
	public static void regulateBasedDisplayHeight(View view, ViewGroup.LayoutParams params, int width, int height, int marginH) {
		Display display=((WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point p=DisplayUtils.getDisplaySize(display);
		regulateBasedHeight(view,params,p.y-marginH*2,width,height);
	}
	
	
	public static void regulateBasedWidth(View view, ViewGroup.LayoutParams params, int baseWidth, int width, int height) {
		if(width == 0) return;
		params.width=baseWidth;
		params.height=baseWidth*height/width;
		view.setLayoutParams(params);
		view.post(new PostRunnable(view));
	}
	public static void regulateBasedHeight(View view, ViewGroup.LayoutParams params, int baseHeight, int width, int height) {
		if(height == 0) return;
		params.width=baseHeight*width/height;
		params.height=baseHeight;
		view.setLayoutParams(params);
		view.post(new PostRunnable(view));
	}
	public static void regulateInCase(View view, ViewGroup.LayoutParams params, int caseWidth, int caseHeight, int width, int height) {
		if(width == 0 || height == 0) return;
		float widthScaleRate=(float)caseWidth/width;
		float heightScaleRate=(float)caseHeight/height;
		if(widthScaleRate<heightScaleRate) {
			params.width=caseWidth;
			params.height=Math.round(height*widthScaleRate);
		} else {
			params.width=Math.round(width*heightScaleRate);
			params.height=caseHeight;
		}
		view.setLayoutParams(params);
		view.post(new PostRunnable(view));
	}
	
	
	private static class PostRunnable implements Runnable {
		private View view;
		public PostRunnable(View view) { this.view=view; }
		@Override public void run() { view.requestLayout(); }
	}
}
