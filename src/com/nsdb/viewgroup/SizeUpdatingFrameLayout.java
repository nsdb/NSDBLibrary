package com.nsdb.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 자신의 크기가 변할 때마다 등록된 리스너에게 알려주는 FrameLayout입니다. 최초 실행시에 반드시 한번 호출됩니다.
 * @author NSDB
 * @see OnSizeUpdatedListener
 *
 */
public class SizeUpdatingFrameLayout extends FrameLayout {
	
	private OnSizeUpdatedListener listener;
	private int lastWidth,lastHeight;
	
	public SizeUpdatingFrameLayout(Context context) {
		super(context);
	}
	public SizeUpdatingFrameLayout(Context context,AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width=getMeasuredWidth();
		int height=getMeasuredHeight();
		if(width!=0 && height!=0 && listener!=null && !(width==lastWidth && height==lastHeight) )
			listener.onSizeUpdated(width, height);
		lastWidth=width;
		lastHeight=height;
	}

	public void setOnSizeUpdatedListener(OnSizeUpdatedListener listener) {
		this.listener=listener;
	}

	// Simplest possible listener :)
	/**
	 * 객체의 크기 변화를 전달받기 위한 리스너입니다.
	 * @author NSDB
	 *
	 */
	public interface OnSizeUpdatedListener {
		public void onSizeUpdated(int viewWidth,int viewHeight);
	}
}
