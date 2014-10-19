package com.nsdb.popup;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast를 쉽게 사용하기 위한 객체입니다.
 * @author DS
 *
 */
public class SimpleToast {
	
	
	private static Context ctx;
	
	
	/**
	 * 이 메서드에 액티비티의 Context를 넣지 말고, 대신 ApplicationContext를 넣으십시요. 메모리 릭 문제가 있습니다.
	 * @param context 컨텍스트
	 */
	public static void init(Context context) {
		ctx = context;
	}
	
	public static void showLong(int rid) {
		Toast.makeText(ctx, rid, Toast.LENGTH_LONG).show();
	}
	
	public static void show(int rid) {
		Toast.makeText(ctx, rid, Toast.LENGTH_SHORT).show();
	}
	
	public static void showLong(String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
	
	public static void show(String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}

}
