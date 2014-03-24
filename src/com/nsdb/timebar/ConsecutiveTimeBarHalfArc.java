package com.nsdb.timebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 반원 형태의 ConsecutiveTimeBar입니다. 각 타임바는 공백으로 구분됩니다.
 * @author NSDB
 * @see ConsecutiveTimeBarBase
 *
 */
public class ConsecutiveTimeBarHalfArc extends ConsecutiveTimeBarBase {

	public final static int TYPE_LEFT=0;
	public final static int TYPE_TOP=1;
	// draw value
	private int type;
	private int colorEnable=0xFFDDDDDD;
	private int colorDisable=0x33DDDDDD;
	private float lineWidth=15;
	private float lineDashOff=35;
	// drawing
	private Paint p;
	private RectF rect;
	
	
	public ConsecutiveTimeBarHalfArc(Context context) {
		super(context);
		init();
	}
	public ConsecutiveTimeBarHalfArc(Context context, AttributeSet attrs) {
		super(context, attrs);
		// attribute set
		for(int i=0;i<attrs.getAttributeCount();i++) {
			if(attrs.getAttributeName(i).equals("type")) {
				if(attrs.getAttributeValue(i).equals("top"))
					type=TYPE_TOP;
				else
					type=TYPE_LEFT;
			}
		}
		init();
	}
	private void init() {
		p=new Paint();
		rect=new RectF(0,0,0,0);
	}	
	
	// draw value
	public void setType(int type) {
		this.type=type;
	}
	public void setColor(int enable, int disable) {
		colorEnable=enable;
		colorDisable=disable;
	}
	public void setLineWidth(int lineWidth, int lineInterval) {
		this.lineWidth=lineWidth;
		this.lineDashOff=lineWidth*2+lineInterval;
	}
	
	// drawing
	@Override
	protected void onDraw(Canvas c) {
		if(getTimeList().size()<1) return;
		p.reset();
		p.setAntiAlias(true);
		p.setStrokeCap(Cap.SQUARE);
		p.setStrokeWidth(lineWidth);
		p.setStyle(Paint.Style.STROKE);
		
		float startAngle=0,maxSweepAngle=0,arcLength;
		float dashPercent;
		switch(type) {
		case TYPE_LEFT:
			startAngle=270;
			maxSweepAngle=-180;
			rect.top=lineWidth/2;
			rect.left=lineWidth/2;
			rect.right=getWidth()*2-lineWidth/2;
			rect.bottom=getHeight()-lineWidth/2;
			break;
		case TYPE_TOP:
			startAngle=180;
			maxSweepAngle=180;
			rect.top=lineWidth/2;
			rect.left=lineWidth/2;
			rect.right=getWidth()-lineWidth/2;
			rect.bottom=getHeight()*2-lineWidth/2;
			break;
		}
		float minor=rect.bottom-rect.top;
		float major=rect.right-rect.left;
		arcLength=(float)((5*(minor+major)/4-minor*major/(minor+major))*Math.PI)/2;
		dashPercent=lineDashOff/arcLength;
		maxSweepAngle*=(1+dashPercent);
		
		float percent,accPercent=0;
		int accTime=0;
		for(int i=0;i<getTimeList().size();i++) {
			// draw background
			percent=(float)getTimeList().get(i)/getMaxTime();
			p.setColor(colorDisable);
			c.drawArc(rect, startAngle+accPercent*maxSweepAngle, Math.max(percent-dashPercent,0)*maxSweepAngle, false, p);
			// draw foreground
			p.setColor(colorEnable);
			if(accTime+getTimeList().get(i)<=getCurrentPosition()) {
				c.drawArc(rect, startAngle+accPercent*maxSweepAngle, Math.max(percent-dashPercent,0)*maxSweepAngle, false, p);
			} else if(accTime<=getCurrentPosition()) {
				percent=(float)(getCurrentPosition()-accTime)/getMaxTime();
				c.drawArc(rect, startAngle+accPercent*maxSweepAngle, Math.max(percent-dashPercent,0)*maxSweepAngle, false, p);
				percent=(float)getTimeList().get(i)/getMaxTime();
			}
			// add
			accTime+=getTimeList().get(i);
			accPercent+=percent;
		}
	}
}
