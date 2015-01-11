package com.nsdb.cm.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtils {
	
	public static String getUrlFromImageId(Context applicationContext, long imageId) {
		
		// ready to query
		String[] projection = new String[] {
				MediaStore.Images.Media.DATA,
				};
		String selection = MediaStore.Images.Media._ID + "=" + imageId;
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		// Make the query.
		Cursor cur = applicationContext.getContentResolver().query(images, projection, selection, null, null);

		// get file location
		if (!cur.moveToFirst()) return null;
		else return cur.getString(0);
		
	}

	public static Bitmap getBitmapFromUrl(String url) {
		return getBitmapFromUrl(url, 4096);
	}
	
	/** maxSize limit : 2~4096 (4096 is Bitmap size limit) */
	public static Bitmap getBitmapFromUrl(String url, int maxSize) {

		// value check
		maxSize = Math.max(2, maxSize);
		maxSize = Math.min(4096, maxSize);
		
		// get size
		BitmapFactory.Options sizeCheckOption = new BitmapFactory.Options();
		sizeCheckOption.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, sizeCheckOption);
		
		// compress
		BitmapFactory.Options imageOption = new BitmapFactory.Options();
		if(sizeCheckOption.outWidth > maxSize || sizeCheckOption.outHeight > maxSize) {
			imageOption.inSampleSize = 2;
			while(Math.max(sizeCheckOption.outWidth, sizeCheckOption.outHeight) / imageOption.inSampleSize > maxSize) {
				imageOption.inSampleSize *= 2;
			}
		}
		
		return BitmapFactory.decodeFile(url, imageOption);
	}
}
