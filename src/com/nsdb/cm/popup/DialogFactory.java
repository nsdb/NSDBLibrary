package com.nsdb.cm.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class DialogFactory {
	
	/** 특정한 뷰를 매개로 생성(없어도 됨), 취소 가능, 확인 리스너만 존재 */
	public static AlertDialog build(Context context, int titleId, View contentView,
			int posBtnId, DialogInterface.OnClickListener posBtnListener,
			int cancelBtnId) {
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(titleId);
    	if(contentView != null) builder.setView(contentView);
    	builder.setPositiveButton(posBtnId, posBtnListener);
    	builder.setNegativeButton(cancelBtnId, null);
    	
    	return builder.create();
		
	}
	
	/** 리스트를 매개로 생성, 취소 가능 */
	public static AlertDialog build(Context context, int titleId,
			String[] list, DialogInterface.OnClickListener listener) {
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(titleId);
		builder.setItems(list, listener);
		
		return builder.create();
		
	}
	
}
