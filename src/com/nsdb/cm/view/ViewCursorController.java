package com.nsdb.cm.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/** 뷰를 복사해서 커서로 띄울 수 있게 해주는 클래스. 단, 그 뷰가 반드시 화면에 표시된 후에 사용하여야 함 */
public class ViewCursorController {
	
//	private final static String TAG = ViewCursorController.class.getSimpleName();
	private final static int SDK_VERSION = Build.VERSION.SDK_INT;
	
	// manager
	private WindowManager windowManager;

	// draw base
	private Bitmap cursorImage;
	private CursorFieldView cursorFieldView;
	private Paint cursorPaint;
	
	// draw setting
	private float cursorPointX;
	private float cursorPointY;
	
	@SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
	public ViewCursorController(Context context, View cursorView, int alpha) {
		
		// make cursor image
		cursorImage = Bitmap.createBitmap(cursorView.getWidth(), cursorView.getHeight(), Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas(cursorImage);
	    cursorView.layout(0, 0, cursorView.getWidth(), cursorView.getHeight());
	    cursorView.draw(c);
	    cursorView.requestLayout();
	    
	    // get window info
	    windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = windowManager.getDefaultDisplay();
	    Point displaySize = new Point();
        if(SDK_VERSION >= 13) {
        	display.getSize(displaySize);
        } else {
        	displaySize.x = display.getWidth();
        	displaySize.y = display.getHeight();
        }
	    
	    // make and add cursor field view
	    cursorFieldView = new CursorFieldView(context);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        		WindowManager.LayoutParams.TYPE_APPLICATION,
        		WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        		PixelFormat.TRANSLUCENT);
        params.width = displaySize.x;
        params.height = displaySize.y;
        windowManager.addView(cursorFieldView, params);
        
        // cursor paint
        cursorPaint = new Paint();
        cursorPaint.setAlpha(alpha);
        cursorPaint.setStyle(Style.FILL);
        cursorPaint.setColor(0xFF000000);
	}
	
	public void setPoint(float x, float y) {
		cursorPointX = x;
		cursorPointY = y;
		cursorFieldView.invalidate();
	}
	
	public void release() {
		windowManager.removeView(cursorFieldView);
		cursorImage.recycle();
		cursorImage = null;
	}
	
	private class CursorFieldView extends View {
		
		public CursorFieldView(Context context) {
	        super(context);
	        setWillNotDraw(false);
        }
		
		@Override
		protected void onDraw(Canvas canvas) {
		    super.onDraw(canvas);
		    if(cursorImage == null) return;
			
			float drawX = cursorPointX - cursorImage.getWidth()/2;
			float drawY = cursorPointY - cursorImage.getHeight()/2;
			
		    canvas.drawBitmap(cursorImage, drawX, drawY, cursorPaint);
		}
	}
}
