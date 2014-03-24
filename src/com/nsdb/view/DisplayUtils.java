package com.nsdb.view;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;

/**
 * 어플리케이션 화면 설정에 관련된 메서드를 모아둔 객체입니다.
 * @author NSDB
 *
 */
public class DisplayUtils {

	private final static int SDK_VERSION=Build.VERSION.SDK_INT;
	
	/**
	 * 현재 화면의 크기를 알려줍니다.
	 * @param display
	 * @return 크기값을 가진 Point
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static Point getDisplaySize(Display display) {
		Point p=new Point();
		if(SDK_VERSION<13) {
			p.x=display.getWidth();
			p.y=display.getHeight();
		} else {
			display.getSize(p);
		}
		return p;
	}
	
	/**
	 * 화면의 소프트키를 숨겨줍니다. 소프트키가 없을 경우 아무 동작을 하지 않습니다.
	 * @param window 현재 윈도우
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void hideSoftKey(Window window) {
		if(SDK_VERSION<14) return;
		else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
}
