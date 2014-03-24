package com.nsdb.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 이 객체를 뷰의 setOnClickListener에 넣으면 그 버튼을 누르는 즉시 해당 액티비티를 종료합니다.
 * @author NSDB
 *
 */
public class OnClickFinisher implements OnClickListener {
	
	private Activity activity;

	public OnClickFinisher(Activity activity) {
		this.activity=activity;
	}

	@Override
	public void onClick(View arg0) {
		activity.finish();
	}
}