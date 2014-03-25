package com.nsdb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 단순한 문자열 변환에 사용되는 메서드들을 모아둔 객체입니다.
 * @author NSDB
 *
 */
public class StringUtils {
	
	/**
	 * Unix time을 문자열로 변환합니다.
	 * @param time 시각
	 * @return 시각을 문자열로 변환한것
	 */
	public static String getTimeString(long time) {
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		String date=""+c.get(Calendar.YEAR)+"-";
		if(c.get(Calendar.MONTH)+1 >= 10) date+=(c.get(Calendar.MONTH)+1);
		else date+="0"+(c.get(Calendar.MONTH)+1);
		date+="-";
		if(c.get(Calendar.DAY_OF_MONTH) >= 10) date+=c.get(Calendar.DAY_OF_MONTH);
		else date+="0"+c.get(Calendar.DAY_OF_MONTH);
		return date;
	}
	public static String getTimeStringSec(long time) {
		return getTimeString(time*1000);
	}
	
	/**
	 * 안드로이드 미디어 인텐트로부터 얻어온 URI를 변환해주는 메서드입니다.
	 * @see http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
	 * @param context 컨텍스트
	 * @param contentUri URI
	 * @return 실제 경로
	 */
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		catch(Exception e) {
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public static String getStringFromStream(InputStreamReader isr) throws Exception {

		BufferedReader br=new BufferedReader(isr);
		String result="";
		String temp=br.readLine();
		while(temp!=null) {
			result+=temp;
			temp="\n"+br.readLine();
		}
		
		return result;
		
	}
	
	
}
