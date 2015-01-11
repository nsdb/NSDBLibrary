package com.nsdb.cm.media;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TableDrawer {
	
//	private final static String TAG = TableDrawer.class.getSimpleName();
	
	public enum TableOrientation { HORIZONTAL, VERTICAL }
	public enum TableScaleType { FIT_CENTER, CROP_CENTER, FIX_XY, RATIO_4_3 }

	// private
	private ParentTableItem mainItem = null;
	
	// common setting (for apply to mainItem, need to call reset())
	public float defaultLineWidth = 2;
	public int defaultColor = Color.BLACK;
	public float defaultTextSize = 20;
	public Alignment defaultTextAlign = Alignment.ALIGN_CENTER;
	public TableOrientation defaultOrientation = TableOrientation.VERTICAL;
	public float defaultPadding = 0;
	public TableScaleType defaultScaleType = TableScaleType.FIT_CENTER;
	public int tableBackgroundColor = Color.WHITE;

	
	// cons
	
	public TableDrawer() {
		// none
	}
	
	
	// public method
	
	public void start() {
		mainItem = new ParentTableItem(this);
	}
	
	public ParentTableItem getMainItem() {
		return mainItem;
	}
	
	public void drawTable(Canvas c, RectF area) {
		// same as getMainItem().drawContent(c,area);
		mainItem.drawContent(c, area);
	}
	
	
	// item class list
	
	public static abstract class TableItem {
		protected final TableDrawer parent;
		protected final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		public final RectF lastDrawArea = new RectF();
		public float itemSize;
		public float padding;
		
		public TableItem(TableDrawer parent) {
			this.parent = parent;
			padding = parent.defaultPadding;
		}
		public void drawContent(Canvas c, RectF area) {
			lastDrawArea.set(area.left + padding, area.top + padding, area.right - padding, area.bottom - padding);
		}
	}
	
	public static class EmptyTableItem extends TableItem {
		public EmptyTableItem(TableDrawer parent) {
			super(parent);
		}
	}
	
	public static class TextTableItem extends TableItem {
		
		public float textSize;
		public Alignment textAlign;
		public int textColor;
		public String text;
		
		public TextTableItem(TableDrawer parent) {
			super(parent);
			textSize = parent.defaultTextSize;
			textAlign = parent.defaultTextAlign;
			textColor = parent.defaultColor;
		}
		public float measureWidth() {
			if(text == null) return padding * 2.2f;
			paint.reset();
			paint.setTextSize(textSize);
			return paint.measureText(text) + padding * 2.2f;  // 딱 맞게 해놓으면 안맞는 문제가 있어서 이렇게 해둠
		}
		public float measureMinHeight() {
			paint.reset();
			paint.setTextSize(textSize);
			return paint.descent() - paint.ascent() + padding * 2.2f;  // 딱 맞게 해놓으면 안맞는 문제가 있어서 이렇게 해둠
		}
		@Override
		public void drawContent(Canvas c, RectF area) {
			super.drawContent(c, area);
			if(text == null) return;
			paint.reset();
			paint.setTextSize(textSize);
			paint.setColor(textColor);

			c.save();
			c.translate(area.left + padding, area.top + padding);
			c.clipRect(0, 0, area.width() - padding*2, area.height() - padding*2);
			StaticLayout sl = new StaticLayout(text, paint, (int)(area.width() - padding*2), textAlign, 1, 0, false);
			c.translate(0, area.height()/2 - padding - sl.getHeight()/2);
			sl.draw(c);
			c.restore();
		}
	}
	
	public static class ImageTableItem extends TableItem {
		public Bitmap image;
		public TableScaleType scaleType;
		
		public ImageTableItem(TableDrawer parent) {
			super(parent);
			scaleType = parent.defaultScaleType;
		}
		@Override
		public void drawContent(Canvas c, RectF area) {
			super.drawContent(c, area);
			if(image == null) return;
			
			Rect srcRect = new Rect(0, 0, image.getWidth(), image.getHeight());
			RectF dstRect = new RectF(area.left + padding, area.top + padding, area.right - padding, area.bottom - padding);
			if(scaleType == TableScaleType.RATIO_4_3) {
				if(dstRect.width() / dstRect.height() > 4.0f / 3.0f ) {
					float width = dstRect.height() * 4/3;
					dstRect.left = dstRect.centerX() - width/2;
					dstRect.right = dstRect.left + width;
				} else {
					float height = dstRect.width() * 3/4;
					dstRect.top = dstRect.centerY() - height/2;
					dstRect.bottom = dstRect.top + height;
				}
			}

			c.save();
			c.clipRect(dstRect, Op.INTERSECT);
			
			if(scaleType != TableScaleType.FIX_XY) {
				
				boolean baseAtVertical = (float)dstRect.width() / dstRect.height() > (float)image.getWidth() / image.getHeight();
				if(scaleType != TableScaleType.FIT_CENTER) baseAtVertical = !baseAtVertical;
				if(baseAtVertical) {
					float width = image.getWidth() * dstRect.height() / image.getHeight();
					dstRect.left = dstRect.centerX() - width/2;
					dstRect.right = dstRect.left + width;
				} else {
					float height = image.getHeight() * dstRect.width() / image.getWidth();
					dstRect.top = dstRect.centerY() - height/2;
					dstRect.bottom = dstRect.top + height;
				}
			}
			
			c.drawBitmap(image, srcRect, dstRect, paint);
			c.restore();
		}
		
	}
	
	
	public static class ParentTableItem extends TableItem {
		public TableOrientation orientation;
		public int color;
		public float lineWidth;
		public ArrayList<TableItem> childItems = new ArrayList<TableItem>();
		
		public ParentTableItem(TableDrawer parent) {
			super(parent);
			orientation = parent.defaultOrientation;
			color = parent.defaultColor;
			lineWidth = parent.defaultLineWidth;
		}
		@Override
		public void drawContent(Canvas c, RectF area) {
			super.drawContent(c, area);
			paint.reset();
			
			// draw background
			if(parent.getMainItem() == this) {
				paint.setStyle(Style.FILL);
				paint.setColor(parent.tableBackgroundColor);
				c.drawRect(area, paint);
			}
			
			// draw border
			paint.setStyle(Style.STROKE);
			paint.setColor(color);
			paint.setStrokeWidth(lineWidth);
			if( (padding > 0 || parent.getMainItem() == this) && lineWidth > 0 ) {
				c.drawRect(area.left + padding, area.top + padding,
						area.right - padding, area.bottom - padding, paint);
			}

			// draw child
			if(childItems.size() == 0) return;
			
			// calc child size
			float[] itemSize = new float[childItems.size()];
			for(int i=0;i<itemSize.length;i++) itemSize[i] = childItems.get(i).itemSize;
			float zeroItemSize = (orientation == TableOrientation.HORIZONTAL)? (area.width() - padding*2) : (area.height() - padding*2);
			float zeroItemCount = itemSize.length;
			for(float size : itemSize) {
				if(size > 0) {
					zeroItemSize -= size;
					zeroItemCount -= 1;
				}
			}
			if(zeroItemCount > 0) {
				zeroItemSize /= zeroItemCount;
				for(int i=0;i<itemSize.length;i++) {
					if(itemSize[i] <= 0) itemSize[i] = zeroItemSize;
				}
			}
			
//			// debug
//			Log.i(TAG, "Area size : "+area.width()+", "+area.height());
//			Log.i(TAG, "Orientation : "+( (orientation == TableOrientation.HORIZONTAL)? "Horizontal" : "Vertical" ));
//			String debugString = "{ ";
//			for(float size : itemSize) debugString += size + ", ";
//			debugString += "}";
//			Log.i(TAG, "Item size : "+debugString);
			
			// draw lines and create item area
			RectF[] itemArea = new RectF[itemSize.length];
			if(orientation == TableOrientation.HORIZONTAL) {
				itemArea[0] = new RectF(
						area.left + padding, area.top + padding,
						area.left + padding + itemSize[0], area.bottom - padding);
				for(int i=1;i<itemArea.length;i++) {
					itemArea[i] = new RectF(
							itemArea[i-1].right, itemArea[0].top,
							itemArea[i-1].right + itemSize[i], itemArea[0].bottom);
					if(lineWidth > 0) c.drawLine(itemArea[i].left, itemArea[i].top, itemArea[i].left, itemArea[i].bottom, paint);
				}
			} else {
				itemArea[0] = new RectF(
						area.left + padding, area.top + padding,
						area.right - padding, area.top + padding + itemSize[0]);
				for(int i=1;i<itemArea.length;i++) {
					itemArea[i] = new RectF(
							itemArea[0].left, itemArea[i-1].bottom,
							itemArea[0].right, itemArea[i-1].bottom + itemSize[i]);
					if(lineWidth > 0) c.drawLine(itemArea[i].left, itemArea[i].top, itemArea[i].right, itemArea[i].top, paint);
				}
			}
			
			// draw child items
			for(int i=0;i<itemArea.length;i++) {
				childItems.get(i).drawContent(c, itemArea[i]);
			}
			
		}
	}
	
}
