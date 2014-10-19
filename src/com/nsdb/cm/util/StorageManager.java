package com.nsdb.cm.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class StorageManager {
	
	private final static String TAG = StorageManager.class.getSimpleName();
	private static String appName = null;
	
	public static void init(String appName) {
		StorageManager.appName = appName;
	}

	public static boolean isAvailable() {
		if(appName == null) {
			Log.i(TAG, "Not inited");
			return false;
		}
		return Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() );
	}
	
	public static String getApplicationFolderPath(Context context) {
		if(isAvailable() == false) return null;

		// path
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName;
		Log.i(TAG,"Application folder path : "+path);

		// create folder
		File folder = new File(path);
		if(!folder.exists()) folder.mkdirs();
		
		return path;
	}
	
}
