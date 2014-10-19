package com.nsdb.cm.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * AlertDialog를 쉽게 사용하기 위해 만든 객체입니다. 버튼을 사용하는 대부분의 AlertDialog를 쉽게 생성할 수 있습니다.
 * @author NSDB
 *
 */
public class SimpleAlertDialog {

	/**
	 * 확인 버튼을 누르면 해당 액티비티를 종료하는 AlertDialog를 보여줍니다. 버튼을 누르면 자동으로 dismiss되며, 취소가 불가능합니다.
	 * @param activity 액티비티
	 * @param stringID 문자열 ID
	 * @see #showFinishDialog(Activity, String)
	 */
	public static void showFinishDialog(Activity activity,int stringID) {
		showFinishDialog(activity,activity.getResources().getString(stringID));
	}
	/**
	 * 확인 버튼을 누르면 해당 액티비티를 종료하는 AlertDialog를 보여줍니다. 버튼을 누르면 자동으로 dismiss되며, 취소가 불가능합니다.
	 * @param activity 액티비티
	 * @param msg 문자열
	 * @see #showFinishDialog(Activity, int)
	 */
	public static void showFinishDialog(Activity activity,String msg) {
		AlertDialog.Builder builder=new AlertDialog.Builder(activity);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new FinishListener(activity));
		builder.setCancelable(false);
		AlertDialog ad=builder.create();
		ad.show();
	}
	
	private static class FinishListener implements DialogInterface.OnClickListener {
		private Activity activity;
		public FinishListener(Activity activity) {
			this.activity=activity;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			activity.finish();
		}
	}
	
	/**
	 * 예, 아니오의 선택지를 넣을 수 있는 AlertDialog를 생성합니다.
	 * @param context 컨텍스트
	 * @param stringID 문자열 ID
	 * @param yes 예를 눌렀을 경우 사용되는 리스너
	 * @param no 아니오를 눌렀을 경우 사용되는 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 * @param cancel 캔슬했을 경우 사용되는 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 * @param cancelable 캔슬 가능 여부
	 * @see #showYesNoDialog(Context, String, android.content.DialogInterface.OnClickListener, android.content.DialogInterface.OnClickListener, android.content.DialogInterface.OnCancelListener, boolean)
	 */
	public static void showYesNoDialog(Context context,int stringID,
			DialogInterface.OnClickListener yes,DialogInterface.OnClickListener no,
			DialogInterface.OnCancelListener cancel,boolean cancelable) {
		showYesNoDialog(context,context.getResources().getString(stringID),yes,no,cancel,cancelable);
	}
	/**
	 * 예, 아니오의 선택지를 넣을 수 있는 AlertDialog를 생성합니다.
	 * @param context 컨텍스트
	 * @param msg 문자열
	 * @param yes 예를 눌렀을 경우 사용되는 리스너
	 * @param no 아니오를 눌렀을 경우 사용되는 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 * @param cancel 캔슬했을 경우 사용되는 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 * @param cancelable 캔슬 가능 여부
	 * @see #showYesNoDialog(Context, int, android.content.DialogInterface.OnClickListener, android.content.DialogInterface.OnClickListener, android.content.DialogInterface.OnCancelListener, boolean)
	 */
	public static void showYesNoDialog(Context context,String msg,
			DialogInterface.OnClickListener yes,DialogInterface.OnClickListener no,
			DialogInterface.OnCancelListener cancel,boolean cancelable) {
		if(no==null) no=getSimpleClickListener();
		if(cancel==null) cancel=getSimpleCancelListener();
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setPositiveButton("YES", yes);
		builder.setNegativeButton("NO", no);
		builder.setOnCancelListener(cancel);
		builder.setCancelable(cancelable);
		AlertDialog ad=builder.create();
		ad.show();		
	}
	private static DialogInterface.OnClickListener getSimpleClickListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
	}
	private static DialogInterface.OnCancelListener getSimpleCancelListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		};
	}
	
	
	/**
	 * 3개의 선택지가 주어지는 AlertDialog를 생성합니다. cancel이 불가능합니다.
	 * @param context 컨텍스트
	 * @param msg 문자열 ID
	 * @param poMsg 1번째 버튼 문자열 ID
	 * @param po 1번째 버튼 리스너
	 * @param neuMsg 2번째 버튼 문자열 ID
	 * @param neu 2번째 버튼 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 * @param neMsg 3번째 버튼 문자열 ID
	 * @param ne 3번째 버튼 리스너, null일 경우 누르면 dialog를 dismiss해주는 리스너가 등록됩니다.
	 */
	public static void show3ChoiceDialog(Context context,int msg,
			int poMsg,DialogInterface.OnClickListener po,
			int neuMsg,DialogInterface.OnClickListener neu,
			int neMsg,DialogInterface.OnClickListener ne) {
		if(neu==null) neu=getSimpleClickListener();
		if(ne==null) ne=getSimpleClickListener();
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setPositiveButton(poMsg, po);
		builder.setNeutralButton(neuMsg, neu);
		builder.setNegativeButton(neMsg, ne);
		builder.setCancelable(false);
		AlertDialog ad=builder.create();
		ad.show();		
	}
	
	
}
