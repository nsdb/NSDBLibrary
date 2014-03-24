package com.nsdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;

/**
 * @author DS
 */
public class BitmapUtils {
	
    public static final int changeRGB4444ToRGB8888(short color) {
		return ((color & 0xF000) << 16 | 0x0F000000) + (((color & 0x0F00) << 12) | ((color & 0x0F00) << 8)) +
					(((color & 0x00F0) << 8) | ((color & 0x00F0) << 4)) + ((color & 0x000F) << 4 | (color & 0x000F));
	}
	
	public static Bitmap resizeBitmap(Bitmap orig, int width, int height) {
		
		Bitmap b = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas c = new Canvas(b);
		
		Paint p = new Paint();
	    p.setAntiAlias(true);
	    p.setDither(true);
	    p.setFilterBitmap(true);
	    
		c.drawBitmap(orig, new Rect(0, 0, orig.getWidth(), orig.getHeight()), new Rect(0, 0, width, height), p);
		
		return b;
	}
	
	
	public static Bitmap rotateBitmap(Bitmap orig, int rotate, boolean highQuality) {
		
		Matrix matrix = new Matrix();
		
		Bitmap b;
		
		Config config;
		
		if(highQuality == true) {
			config = Config.RGB_565;
		}
		else {
			if(orig.hasAlpha() == true) {
				config = Config.ARGB_8888;
			}
			else {
				config = Config.ARGB_4444;
			}
		}
		
		
		if(rotate == 0 || rotate == 180) {
			b = Bitmap.createBitmap(orig.getWidth(), orig.getHeight(), config);
			matrix.postTranslate(0, 0);
		}
		else if(rotate == 90) {
			b = Bitmap.createBitmap(orig.getHeight(), orig.getWidth(), config);
			matrix.postTranslate((orig.getWidth() - orig.getHeight()) / 2, -(orig.getHeight() - orig.getWidth()) / 2);
		}
		else {
			b = Bitmap.createBitmap(orig.getHeight(), orig.getWidth(), config);
			matrix.postTranslate(-(orig.getWidth() - orig.getHeight()) / 2, (orig.getHeight() - orig.getWidth()) / 2);
		}
		
		matrix.postRotate(rotate, orig.getWidth() / 2, orig.getHeight() / 2);
		
		Canvas c = new Canvas(b);
		
		Paint p = new Paint();
	    p.setAntiAlias(true);
	    p.setDither(true);
	    p.setFilterBitmap(true);
	    
		c.drawRect(new Rect(0, 0, b.getWidth(), b.getHeight()), new Paint());
		c.drawBitmap(orig, matrix, p);
		return b;
	}
	
	
	public static Bitmap flipVerticalBitmap(Bitmap orig) {
		
		Matrix matrix = new Matrix();
		
		Bitmap b;
		
		b = Bitmap.createBitmap(orig.getWidth(), orig.getHeight(), Config.ARGB_8888);
		matrix.postTranslate(orig.getWidth(), 0);
		matrix.preScale(-1, 1);
		
		Canvas c = new Canvas(b);
		
		Paint p = new Paint();
	    p.setAntiAlias(true);
	    p.setDither(true);
	    p.setFilterBitmap(true);
		
		c.drawRect(new Rect(0, 0, b.getWidth(), b.getHeight()), new Paint());
		c.drawBitmap(orig, matrix, p);
		return b;
	}
	
	public static Bitmap decodingImage(String filename, int width, int height, boolean highQuality) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, opts);
		
		if (opts.mCancel || opts.outWidth == -1 || opts.outHeight == -1) {
			return null;
		}
		
		int sampleSize;
		
		if(opts.outWidth / (float)width < opts.outHeight / (float)height) {
			sampleSize = (int)(opts.outWidth / (float)width);
		}
		else {
			sampleSize = (int)(opts.outHeight / (float)height);
		}
		
		if(sampleSize < 1) {
			sampleSize = 1;
		}
		
		int scaleWidth;
        int scaleHeight;
		
		if(opts.outWidth > width || opts.outHeight > height) {
	        if(opts.outWidth / (float)width > opts.outHeight / (float)height) {
	        	scaleWidth = (int)width;
	        	scaleHeight = (int)(width * ((float)opts.outHeight / opts.outWidth));
	        }
	        else {
	        	scaleWidth = (int)(height * ((float)opts.outWidth / opts.outHeight));
	        	scaleHeight = (int)height;
	        }
        }
        else {
        	scaleWidth = opts.outWidth;
        	scaleHeight = opts.outHeight;
        }
		
		opts.inJustDecodeBounds = false;
		opts.inDither = true;
		if(highQuality == true) {
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		}
		else {
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
		}
        opts.inSampleSize = sampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(filename, opts);
        
        if(bitmap != null) {
        
        	Bitmap result =  Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
        	bitmap.recycle();
        	bitmap = null;
        	
        	if(scaleWidth > 200 || scaleHeight > 200) {
        		System.gc();
        	}
        	
        	File ori = new File(filename);
    		int rotate = 0;
    		
    		ExifInterface exif;
    		 
    		if(ori.exists() == false) {
    			return null;
    		}
    		
    		try {
    			exif = new ExifInterface(filename);
    			int value = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
    			
    			switch(value) {
    			case 1:
    				rotate = 0;
    				break;
    			case 6:
    				rotate = 90;
    				break;
    			case 8:
    				rotate = -90;
    				break;
    			case 3:
    				rotate = 180;
    				break;
    			}
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		} catch(OutOfMemoryError e) {
    		}
        	
    		if(rotate != 0) {
    			Bitmap rot = rotateBitmap(result, rotate, highQuality);
    			
    			result.recycle();
    			result = rot;
    			
    			if(scaleWidth > 200 || scaleHeight > 200) {
    				System.gc();
    			}
    		}
        	
        	return result;
        }
        else {
        	return null;
        }
	}
    
    public static Bitmap renderToGrayBitmap(byte[] yuv, int width, int height) {
        int inputOffset = 0;

        int[] out = new int[width * height];
        
        for (int y = 0; y < height; y++) {
          int outputOffset = y * width;
          for (int x = 0; x < width; x++) {
            int grey = yuv[inputOffset + x] & 0xff;
            out[outputOffset + x] = (0xff << 24) | (grey << 16) | (grey << 8) | grey;
          }
          inputOffset += width;
        }
        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(out, 0, width, 0, 0, width, height);
                
        return bitmap;
    }
    
    
    public static Bitmap renderToBitmap(byte[] data, int width, int height) { 
    	final int sz = width * height; 
    	int i, j; 
    	int Y, Cr = 0, Cb = 0;
    	
    	int[] out = new int[width * height];
    	
    	for(j = 0; j < height; j++) { 
    		int pixPtr = j * width; 
    		final int jDiv2 = j >> 1; 
	    	for(i = 0; i < width; i++) { 
	    		Y = data[pixPtr]; if(Y < 0) Y += 255; 
	    		if((i & 0x1) != 1) { 
	    			final int cOff = sz + jDiv2 * width + (i >> 1) * 2; 
	    			Cb = data[cOff]; 
	    			if(Cb < 0) Cb += 127; else Cb -= 128; 
	    			Cr = data[cOff + 1]; 
	    			if(Cr < 0) Cr += 127; else Cr -= 128; 
	    		} 
	    		int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5); 
	    		if(R < 0) R = 0; else if(R > 255) R = 255; 
	    		int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 
	    		3) + (Cr >> 4) + (Cr >> 5); 
	    		if(G < 0) G = 0; else if(G > 255) G = 255; 
	    		int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6); 
	    		if(B < 0) B = 0; else if(B > 255) B = 255; 
	    		out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R; 
	    	} 
    	} 
    	
    	Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(out, 0, width, 0, 0, width, height);
         
        return bitmap;
    }
    
    
    private static  float cleanValue(float p_val, float p_limit) { 
        return Math.min(p_limit,Math.max(-p_limit,p_val)); 
    } 
    
    public static ColorFilter makeHueColorFilter(float value) {
    	value = cleanValue(value,180f)/180f*(float)Math.PI; 
    	
    	ColorMatrix cm = new ColorMatrix();
    	
    	if (value == 0 ) { return null; } 
    	float cosVal = (float)Math.cos(value); 
    	float sinVal = (float)Math.sin(value); 
    	float lumR = 0.213f; 
    	float lumG = 0.715f; 
    	float lumB = 0.072f; 
    	float[] mat = new float[] { 
    			lumR+cosVal*(1-lumR)+sinVal*(-lumR),lumG+cosVal*(-lumG)+sinVal*(- 
    					lumG),lumB+cosVal*(-lumB)+sinVal*(1-lumB),0,0, 
    					lumR+cosVal*(-lumR)+sinVal*(0.143f),lumG+cosVal*(1-lumG) 
    					+sinVal*(0.140f),lumB+cosVal*(-lumB)+sinVal*(-0.283f),0,0, 
    					lumR+cosVal*(-lumR)+sinVal*(-(1-lumR)),lumG+cosVal*(-lumG) 
    					+sinVal*(lumG),lumB+cosVal*(1-lumB)+sinVal*(lumB),0,0, 
    					0f,0f,0f,1f,0f, 
    					0f,0f,0f,0f,1f 
    	}; 
    	cm.postConcat(new ColorMatrix(mat)); 
    	
    	return new ColorMatrixColorFilter(cm);
    }
    
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    
    
    public static ColorFilter getInvertColorMatrix() {
    	float mx [] = {
                -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
                0.0f,  -1.0f,  0.0f,  1.0f,  0.0f,
                0.0f,  0.0f,  -1.0f,  1.0f,  0.0f,
                0.0f,  0.0f,  0.0f,  1.0f,  0.0f 
       };
    	
    	ColorMatrix cm = new ColorMatrix(mx);
    	
    	ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
    	
    	return cf;
    }
    
    
    public static Bitmap createMaskImage(Context ctx, int maskId, Bitmap ori) {
		int dstWidth;
    	int dstHeight;
    	
    	Bitmap mask1 = null;
		    	
    	mask1 = BitmapFactory.decodeResource(ctx.getResources(), maskId);
    	
    	dstWidth = mask1.getWidth();
   		dstHeight = mask1.getHeight();
   	
    	Bitmap roundImage = Bitmap.createBitmap(dstWidth, dstHeight, Config.ARGB_8888);
    	
    	Canvas canvas = new Canvas(roundImage);
    	Rect src = new Rect(0, 0, ori.getWidth(), ori.getHeight());
    	Rect dst = new Rect(0, 0, dstWidth, dstHeight);
    	
    	Paint paint = new Paint();
    	paint.setAntiAlias(false);
    	paint.setDither(false);
    	paint.setFilterBitmap(true);
    	canvas.drawBitmap(ori, src, dst, paint);
    	
    	paint.setFilterBitmap(false);
    	
		if(mask1 != null) {
			
	    	BitmapShader shader = new BitmapShader(mask1, TileMode.CLAMP, TileMode.CLAMP);
	    	paint.setShader(shader);
	    	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	    	canvas.drawRect(new Rect(0, 0, dstWidth, dstHeight), paint);
	    	paint.setShader(null);
	    	paint.setXfermode(null);
	    	
	    	mask1.recycle();
		}
    	
    	return roundImage;
	}
    

    public static Bitmap[] splitBitmap(Bitmap src, int row, int column) {
    	
    	try {
	    	int w = src.getWidth();
	    	int h = src.getHeight();
	    	
	    	int splitWidth = w / column;
	    	int splitHeight = h / row;
	    	
	    	Bitmap images[] = new Bitmap[row * column];
	    	
	    	for(int i = 0; i < row; i++) {
	    		for(int j = 0; j < column; j++) {
	    			images[i * column + j] = Bitmap.createBitmap(src, j * splitWidth, i * splitHeight, splitWidth, splitHeight);
	    		}
	    	}
	    	
	    	return images;
    	}
    	catch (Throwable t) {
    		return null;
    	}
    }
    
    
    public static Bitmap cropSquareCenter(Bitmap src) {
    	int srcWidth = src.getWidth();
    	int srcHeight = src.getHeight();
    	
    	int size = Math.min(srcWidth, srcHeight);
    	
    	return Bitmap.createBitmap(src, (srcWidth - size) / 2, (srcHeight - size) / 2, size, size);
    	
    }
    
    
    public static byte[] bitmapToByteArray(Bitmap src) {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();  
        src.compress( CompressFormat.JPEG, 100, stream);  
        byte[] byteArray = stream.toByteArray();
        try {
        	stream.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        return byteArray;  
    }
}
