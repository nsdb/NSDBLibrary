package com.nsdb.cm.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 키보드의 표시 여부를 체크할 수 있는 LinearLayout입니다. 사용하고자 하는 액티비티의 매니패스트 설정에
 * android:windowSoftInputMode="adjustResize"가 추가되어있어야 합니다.<br>
 * <br>
 * @see http://illusionsandroid.blogspot.kr/2011/09/android-soft-virtual-keyboard-listener.html
 * @see OnSoftKeyboardListener
 * @author NSDB
 */
public class KeyboardDetectingLinearLayout extends LinearLayout {

	private int originalHeight;
	private int lastHeight;
	private OnSoftKeyboardListener onSoftKeyboardListener;

	public KeyboardDetectingLinearLayout(Context context) {
		super(context);
	}
	public KeyboardDetectingLinearLayout(Context context,AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		if (onSoftKeyboardListener != null) {
			int newHeight = MeasureSpec.getSize(heightMeasureSpec);
			originalHeight=Math.max(originalHeight,MeasureSpec.getSize(heightMeasureSpec));
			
			// Moto Zoom(MZ601) 3.x에서 view height가 변경되는 버그를 위한 예외 코드
			if(Math.abs(newHeight - originalHeight) < 60) {
				originalHeight = newHeight;
			}
			
			if(lastHeight!=newHeight) {
				if (originalHeight > newHeight)
					onSoftKeyboardListener.onShown();
				else
					onSoftKeyboardListener.onHidden();
				lastHeight=newHeight;
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public final void setOnSoftKeyboardListener(
			final OnSoftKeyboardListener listener) {
		this.onSoftKeyboardListener = listener;
	}

	// Simplest possible listener :)
	/**
	 * 키보드의 상태를 전달받기 위한 리스너입니다.
	 * @author NSDB
	 *
	 */
	public interface OnSoftKeyboardListener {
		public void onShown();
		public void onHidden();
	}
}
