package com.nsdb.cm.timebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * 가장 일반적인 TimeBar입니다.
 * @author NSDB
 * @see TimeBarBase
 */
public class TimeBarLinear extends TimeBarBase {
	
	private Paint p;
	private Rect bounds;
	private int colorEnable=0xFFFFFFFF;
	private int colorDisable=0x33FFFFFF;

	public TimeBarLinear(Context context) {
		super(context);
		init();
	}
	public TimeBarLinear(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		p=new Paint();
		bounds=new Rect();		
	}
	
	public void setColor(int enable, int disable) {
		colorEnable=enable;
		colorDisable=disable;
	}

	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		p.reset();
		// bg
		p.setColor(colorDisable);
		c.getClipBounds(bounds);
		c.drawRect(bounds, p);
		// bar
		if(getMaxTime()<=0) return;
		p.setColor(colorEnable);
		bounds.right=(int)(bounds.left+(bounds.right-bounds.left)*getCurrentPosition()/getMaxTime());
		c.drawRect(bounds, p);
	}
}
