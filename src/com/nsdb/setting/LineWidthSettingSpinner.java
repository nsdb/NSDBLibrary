package com.nsdb.setting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.nsdb.popup.ListPopupWindow;
import com.nsdb.popup.ListPopupWindow.OnPopupItemClickListener;

/**
 * 선의 굵기를 선택할 수 있는 Spinner 형식의 뷰
 * @author NSDB
 *
 */
public class LineWidthSettingSpinner extends LinearLayout implements OnClickListener, OnPopupItemClickListener {
	
	private final static int[] LINE_WIDTH_LIST = { 2, 3, 5, 10 };
	private int lineWidth;
	private LineView lineView;
	private ListPopupWindow linePopupWindow;
	private OnLineWidthChangeListener listener;

	public LineWidthSettingSpinner(Context context) {
		super(context);
		init();
	}
	public LineWidthSettingSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		int dp5 = Math.round(5*getResources().getDisplayMetrics().density);
		setPadding(dp5,0,dp5,0);
		setBackgroundColor(0xFFFFFFFF);
		
		lineWidth=LINE_WIDTH_LIST[0];
		lineView=new LineView(getContext());
		lineView.setLineWidth(lineWidth);
		LayoutParams lineViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(lineView,lineViewParams);
		
		linePopupWindow = new ListPopupWindow(getContext());
		LineView tempView = null;
		for(int i=0;i<LINE_WIDTH_LIST.length;i++) {
			tempView = new LineView(getContext());
			tempView.setLineWidth(LINE_WIDTH_LIST[i]);
			linePopupWindow.addView(tempView);
		}
		linePopupWindow.setOnPopupItemClickListener(this);
		
		setOnClickListener(this);
	}
	
	public void setLineWidth(int width) {
		this.lineWidth=width;
		lineView.setLineWidth(width);
	}
	public int getLineWidth() {
		return lineWidth;
	}

	@Override
	public void onClick(View v) {
		linePopupWindow.setItemSize(lineView.getWidth(),lineView.getHeight());
		linePopupWindow.setOffset((getWidth()-lineView.getWidth())/2, 0);
		linePopupWindow.show(this);
	}
	
	@Override
	public void onPopupItemClick(ListPopupWindow window, int position) {
		if(lineWidth != LINE_WIDTH_LIST[position]) {
			setLineWidth(LINE_WIDTH_LIST[position]);
			if(listener != null) listener.onLineWidthChange(this, lineWidth);
		}
	}
	
	
	
	public void setOnColorChangeListener(OnLineWidthChangeListener listener) {
		this.listener=listener;
	}
	
	public interface OnLineWidthChangeListener {
		public void onLineWidthChange(LineWidthSettingSpinner spinner, int lineWidth);
	}
	
	private class LineView extends View {
		
		private Paint p;
		private int lineWidth;
		private int dp5;

		public LineView(Context context) {
			super(context);
			p = new Paint();
			p.setColor(0xFF000000);
			dp5 = Math.round(5*getResources().getDisplayMetrics().density);
		}
		
		public void setLineWidth(int width) {
			lineWidth=width;
			invalidate();
		}
		//public int getLineWidth() { return lineWidth; }
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			p.setStrokeWidth(lineWidth);
			canvas.drawLine(dp5, getHeight()/2, getWidth()-dp5, getHeight()/2, p);
		}
		
	}


}
