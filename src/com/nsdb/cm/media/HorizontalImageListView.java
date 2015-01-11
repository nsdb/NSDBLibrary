package com.nsdb.cm.media;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nsdb.cm.view.ViewCursorController;

public class HorizontalImageListView extends HorizontalScrollView {
	
	private final static String TAG = HorizontalImageListView.class.getSimpleName();
	private final static int LINE_WIDTH = 1;
	private final static int SDK_VERSION = Build.VERSION.SDK_INT;
	private final static long LONG_CLICK_DELAY = 500;

	// components
	private ThumbSetter mThumbSetter;
	private ViewCursorController mCursorController;
	// layout
	private LinearLayout mContentView;
	private ArrayList<FrameLayout> mImageViewWrapperList = new ArrayList<FrameLayout>();
	// layout value
	private int mImagePadding; // = 5dp (in init())
	private int mLineColor = Color.rgb(222, 222, 222);
	private int mSelectedColor = Color.rgb(244, 244, 80);
	private int mItemWidth;
	private int mItemHeight;
	// bitmap, position
	private long[] mImageIdList;
	private int mSelectedPosition = -1;
	// listener
	private ItemStateChangeListener mListener;
	// touch action
	private Handler mMainHandler = new Handler(Looper.getMainLooper());
	private Timer mLongClickTimer;
	private int mDragPosition = -1;
	
	public interface ItemStateChangeListener {
    	public void onSelectedItemChanged(HorizontalImageListView view, int position);
    	public void onItemSwitched(HorizontalImageListView view, int from, int to);
	}
	
	

	public HorizontalImageListView(Context context) {
	    super(context);
	    init();
    }
	public HorizontalImageListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    for(int i=0;i<attrs.getAttributeCount();i++) {
	    	if(attrs.getAttributeName(i).equals("imagePadding")) {
	    		Log.i(TAG, "imagePadding : "+attrs.getAttributeIntValue(i, 0));
	    	} else if(attrs.getAttributeName(i).equals("lineColor")) {
	    		Log.i(TAG, "imagePadding : "+attrs.getAttributeResourceValue(i, 0));
	    	}
	    }
	    init();
    }
	
	private void init() {
		mContentView = new LinearLayout(getContext());
		mContentView.setOrientation(LinearLayout.HORIZONTAL);
		addView(mContentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		if(mImagePadding == 0) mImagePadding = Math.round(5 * getContext().getResources().getDisplayMetrics().density);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(mItemHeight != MeasureSpec.getSize(heightMeasureSpec)) {
			
			mItemHeight = MeasureSpec.getSize(heightMeasureSpec);
		    mItemWidth = mItemHeight * 4/3;

		    int prePosition = mSelectedPosition;
		    refresh(mImageIdList);
		    setSelectedPosition(prePosition);
		}
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();
	    if(mThumbSetter != null) {
	    	mThumbSetter.release();
	    	mThumbSetter = null;
	    }
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
	    super.onWindowFocusChanged(hasWindowFocus);
	    if(!hasWindowFocus) {
			clearLongClickState();
	    	disableDragMode();
	    }
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		// prevent scorlling on drag
		if(mDragPosition != -1) return false;
		
		// check is scrolling
		boolean intercept = super.onInterceptTouchEvent(ev);
		if(intercept) clearLongClickState();
		return intercept;
	}
	
	// public
	
	public void refresh(long[] imageIdList) {
		clear();

		// available check
		mImageIdList = imageIdList;
		if(mItemWidth == 0 || mImageIdList == null) return;

		// thumbnail setter check
		if(mThumbSetter == null) {
			mThumbSetter = new ThumbSetter(getContext().getApplicationContext());
			mThumbSetter.start();
		}
		
		// make and add imageviews and lines
		FrameLayout wrapperView;
		ImageView image;
		View line;
		for(long id : imageIdList) {
			
			wrapperView = new FrameLayout(getContext());
			wrapperView.setLayoutParams(new LayoutParams(mItemWidth, mItemHeight));
			wrapperView.setPadding(mImagePadding, mImagePadding, mImagePadding, mImagePadding);
			wrapperView.setOnTouchListener(mItemTouchListener);
			image = new ImageView(getContext());
			image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			image.setScaleType(ScaleType.CENTER_CROP);
			image.setBackgroundColor(mLineColor);
			mThumbSetter.addRequest(id, image);
			wrapperView.addView(image);
			mContentView.addView(wrapperView);
			mImageViewWrapperList.add(wrapperView);
			
			line = new View(getContext());
			line.setLayoutParams(new LayoutParams(LINE_WIDTH, mItemHeight));
			line.setBackgroundColor(mLineColor);
			mContentView.addView(line);
		}
		
	}
	
	public void clear() {
		
		mContentView.removeAllViews();
		mImageViewWrapperList.clear();
		mSelectedPosition = -1;
		clearLongClickState();
		disableDragMode();
	}
	
	public void setSelectedPosition(int position) {
		mSelectedPosition = position;
		for(int i=0;i<mImageViewWrapperList.size();i++) {
			if(i == mSelectedPosition) {
				mImageViewWrapperList.get(i).setBackgroundColor(mSelectedColor);
			} else {
				mImageViewWrapperList.get(i).setBackgroundColor(0);
			}
		}
	}
	
	public void setItemStateChangeListener(ItemStateChangeListener listener) {
		mListener = listener;
	}
	
	public int getSelectedPosition() { return mSelectedPosition; }
	
	
	
	// touch action
	
	private OnTouchListener mItemTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			
			// get touched position
			int touchedPosition = -1;
			for(int i=0;i<mImageViewWrapperList.size();i++) {
				if(mImageViewWrapperList.get(i) == v) {
					touchedPosition = i;
					break;
				}
			}
			if(touchedPosition == -1) return false;
			////
			
			// process touch event
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_DOWN:
				// start long click timer
				for(int i=0;i<mImageViewWrapperList.size();i++) {
					if(mImageViewWrapperList.get(i) == v) {
						mLongClickTimer = new Timer();
						mLongClickTimer.schedule(new LongClickTask(i, event.getRawX(), event.getRawY()), LONG_CLICK_DELAY);
						return true;
					}
				}
				return false;
			
			case MotionEvent.ACTION_MOVE:
				// if drag mode, send point
				if(mDragPosition != -1) {
					mCursorController.setPoint(event.getRawX(), event.getRawY());
				}
				return true;
			
			case MotionEvent.ACTION_UP:
				
				// switch image
				if(mDragPosition != -1) {
					// y point check
					if(event.getY() < -getHeight() || event.getY() > getHeight()*2) {
						disableDragMode();
						return true;
					}
					// get switch position
					// switchPosition * (mItemWidth + LINE_WIDTH) < switch point x < (switchPosition + 1) * (itemWidth + LINE_WIDTH)
					int switchPosition = (int)(mDragPosition * mItemWidth + event.getX())/(int)(mItemWidth + LINE_WIDTH);
					Log.i(TAG, "Test : "+(int)(mDragPosition * mItemWidth + event.getX())+", "+(int)(mItemWidth + LINE_WIDTH));
					if(switchPosition >= mImageViewWrapperList.size()) {
						switchPosition = mImageViewWrapperList.size() - 1;
					} else if(switchPosition < 0) {
						switchPosition = 0;
					}
					// same position check
					if(switchPosition != mDragPosition) {
						// switch view
						ImageView dragImage = (ImageView)mImageViewWrapperList.get(mDragPosition).getChildAt(0);
						ImageView switchImage = (ImageView)mImageViewWrapperList.get(switchPosition).getChildAt(0);
						mThumbSetter.addRequest(mImageIdList[switchPosition], dragImage);
						mThumbSetter.addRequest(mImageIdList[mDragPosition], switchImage);
						long swapId = mImageIdList[switchPosition];
						mImageIdList[switchPosition] = mImageIdList[mDragPosition];
						mImageIdList[mDragPosition] = swapId;
						touchedPosition = switchPosition;
						// send to listener
						if(mListener != null) mListener.onItemSwitched(HorizontalImageListView.this, mDragPosition, switchPosition);
					}
					
				}

				// view check
				for(int i=0;i<mImageViewWrapperList.size();i++) {
					if(i == touchedPosition) {
						// select image
						mImageViewWrapperList.get(i).setBackgroundColor(mSelectedColor);
						mSelectedPosition = i;
						// send to listener
						if(mListener != null) mListener.onSelectedItemChanged(HorizontalImageListView.this, i);
					} else {
						// deselect
						mImageViewWrapperList.get(i).setBackgroundColor(0);
					}
				}
				
				// clear state
				clearLongClickState();
				disableDragMode();
				
				return true;
				
			
			default: return false;
			}
			
		}
	};
			
	
	private class LongClickTask extends TimerTask {
		
		private int mPosition;
		private float mCursorX, mCursorY;
		
		public LongClickTask(int position, float x, float y) {
			mPosition = position;
			mCursorX = x;
			mCursorY = y;
		}

		@Override
        public void run() {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					activateDragMode(mPosition, mCursorX, mCursorY);
				}
			});
        }
		
	}
	
	@SuppressLint("NewApi")
	private void activateDragMode(int position, float x, float y) {
		
		// Activate Drag Mode
		if(SDK_VERSION >= 11) {
			mImageViewWrapperList.get(position).setAlpha(0.5f);
		}
		mDragPosition = position;
		Log.i(TAG, "Activate Drag Mode : "+position);
		ImageView item = (ImageView)mImageViewWrapperList.get(position).getChildAt(0);
		mCursorController = new ViewCursorController(getContext(), item, 128);
		mCursorController.setPoint(x, y);
        
		// clear long click timer
		clearLongClickState();
		
	}
			
	@SuppressLint("NewApi")
	private void disableDragMode() {
		if(mDragPosition != -1) {
			if(SDK_VERSION >= 11) {
				mImageViewWrapperList.get(mDragPosition).setAlpha(1.0f);
			}
			mCursorController.release();
			mCursorController = null;
			mDragPosition = -1;
		}
	}
	
	private void clearLongClickState() {
		if(mLongClickTimer != null) {
			mLongClickTimer.cancel();
			mLongClickTimer = null;
		}
	}
	
	
}
