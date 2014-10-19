package com.nsdb.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * 색을 선택할 수 있는 Spinner 형식의 뷰
 * @author NSDB
 *
 */
public class ColorSettingSpinner extends LinearLayout implements OnClickListener {
	
	private int color;
	private View colorView;
	private OnColorChangeListener listener;

	public ColorSettingSpinner(Context context) {
		super(context);
		init();
	}
	public ColorSettingSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		int dp5 = Math.round(5*getResources().getDisplayMetrics().density);
		setPadding(dp5,dp5,dp5,dp5);
		setBackgroundColor(0xFFFFFFFF);
		
		color=0xFF000000;
		colorView=new View(getContext());
		colorView.setBackgroundColor(color);
		LayoutParams colorViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(colorView,colorViewParams);
		
		setOnClickListener(this);
	}
	
	public void setColor(int color) {
		this.color=color;
		colorView.setBackgroundColor(color);
	}
	public int getColor() {
		return color;
	}

	@Override
	public void onClick(View v) {

		// Need AmbilWarnaDialog (android color picker) library
//		AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(getContext(), color, new OnAmbilWarnaListener() {
//
//			@Override public void onCancel(AmbilWarnaDialog dialog) {}
//
//			@Override
//			public void onOk(AmbilWarnaDialog dialog, int color) {
//				if(ColorSettingSpinner.this.color != color) {
//					setColor(color);
//					if(listener != null) listener.onColorChange(ColorSettingSpinner.this, color);
//				}
//			}
//			
//		});
//		
//		colorDialog.show();
	}
	
	public void setOnColorChangeListener(OnColorChangeListener listener) {
		this.listener=listener;
	}
	
	public interface OnColorChangeListener {
		public void onColorChange(ColorSettingSpinner spinner, int color);
	}

}
