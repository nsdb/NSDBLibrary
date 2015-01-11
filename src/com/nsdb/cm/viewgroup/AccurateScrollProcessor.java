package com.nsdb.cm.viewgroup;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

/**
 * 여러 뷰에서 스크롤을 사용하여 충돌이 일어날 경우, 이 리스너를 독점하길 원하는 뷰에게 붙여주면 다른 뷰가 스크롤을 빼앗지 않습니다.
 * @author NSDB
 *
 */
public class AccurateScrollProcessor implements OnTouchListener {

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		ViewGroup group=(ViewGroup)view;
		if (event.getAction() == MotionEvent.ACTION_UP)
            group.requestDisallowInterceptTouchEvent(false);
        else 
            group.requestDisallowInterceptTouchEvent(true);
		return false;
	}
}
