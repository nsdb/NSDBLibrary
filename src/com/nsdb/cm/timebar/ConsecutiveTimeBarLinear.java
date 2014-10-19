package com.nsdb.cm.timebar;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * 가장 일반적인 ConsecutiveTimeBar입니다.
 * @author NSDB
 * @see ConsecutiveTimeBarBase
 */
public class ConsecutiveTimeBarLinear extends ConsecutiveTimeBarBase {

	private Paint p;
	private Rect bounds;
	private int[] colorEnable={ 0xFFDD0000, 0xFF00DD00, 0xFF0000DD };
	private int[] colorDisable={ 0x33DD0000, 0x3300DD00, 0x330000DD };

	public ConsecutiveTimeBarLinear(Context context) {
		super(context);
		init();
	}
	public ConsecutiveTimeBarLinear(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		p=new Paint();
		bounds=new Rect();
	}
	
	public void setColorTable(int[] enable, int[] disable) {
		colorEnable=enable;
		colorDisable=disable;
	}

	// draw
	@Override
	protected void onDraw(Canvas c) {
		if(getTimeList().size()<=0) return;
		p.reset();
		// parent value
		ArrayList<Integer> timeList=getTimeList();
		long currentPosition=getCurrentPosition();
		long maxTime=getMaxTime();
		bounds.top=0;
		bounds.bottom=getHeight();
		// draw
		int prevAccTime=0,nextAccTime=0;
		for(int i=0;i<timeList.size();i++) {
			// add time
			if(i>0) prevAccTime=nextAccTime;
			nextAccTime+=timeList.get(i);
			// set bounds
			bounds.left=Math.round((float)getWidth()*prevAccTime/maxTime);
			bounds.right=Math.round((float)getWidth()*nextAccTime/maxTime);
			// draw
			if(currentPosition<=prevAccTime) {
				p.setColor(colorDisable[i%colorDisable.length]);
				c.drawRect(bounds, p);
			} else if(currentPosition>=nextAccTime) {
				p.setColor(colorEnable[i%colorEnable.length]);
				c.drawRect(bounds, p);
			} else {
				p.setColor(colorEnable[i%colorEnable.length]);
				bounds.right=Math.round(bounds.left+(float)getWidth()*(currentPosition-prevAccTime)/maxTime);
				c.drawRect(bounds, p);
				p.setColor(colorDisable[i%colorDisable.length]);
				bounds.left=bounds.right;
				bounds.right=Math.round((float)getWidth()*nextAccTime/maxTime);
				c.drawRect(bounds, p);
			}
		}
	}
	
}
