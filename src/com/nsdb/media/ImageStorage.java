package com.nsdb.media;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class ImageStorage {

	private final static String TAG = ImageStorage.class.getSimpleName();
	private Context applicationContext;
	
	/** 이미지 정보 기록용 데이터 */
	public static class ImageStorageData {
		
		// 첫 요청으로 받아오는 데이터들
		
		/** 그림의 ID (사용자가 이 그림을 선택했는지를 알기 위해) */
		public long imageId;
		
		/** 그림의 이름 (디버그용) */
		public String pictureName;
		
		/** 그룹의 ID (폴더별로 분류하여 사용자가 선택하기 쉽게) */
		public long bucketId;
		
		/** 그룹의 이름 (폴더 구분을 쉽게) */
		public String bucketName;
	}
	
	/** 데이터 리스트<br>
	 * 폴더별로 분류하며, 각 폴더에는 최소 1개 이상의 파일이 존재합니다. */
	private ArrayList<ArrayList<ImageStorageData>> dataList = new ArrayList<ArrayList<ImageStorageData>>();

	// public method
	
	public ImageStorage(Context applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void updateImageList() {
		
		Log.i(TAG, "updateImageList");
		long startTime = System.currentTimeMillis();
		
		// clear last data
		dataList.clear();

		// ready to query
		String[] projection = new String[] {
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.BUCKET_ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				};
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String sortOrder = MediaStore.Images.Media.BUCKET_ID + " ASC, " + MediaStore.Images.Media.DATE_TAKEN+" DESC";

		// Make the query.
		Cursor cur = applicationContext.getContentResolver().query(images, projection, null, null, sortOrder);
		Log.i(TAG, "query count = " + cur.getCount());

		// read cursor and add data
		ImageStorageData output;
		long currentBucketId = 0;
		int currentBucketIndex = -1;
		if (cur.moveToFirst()) {
			do {
				
				output = new ImageStorageData();
				output.imageId = cur.getLong(0);
				output.pictureName = cur.getString(1);
				output.bucketId = cur.getLong(2);
				output.bucketName = cur.getString(3);
				
				// new folder
				if(currentBucketId != output.bucketId) {
					currentBucketId = output.bucketId;
					currentBucketIndex += 1;
					dataList.add(new ArrayList<ImageStorageData>());
				}
				// new file
				dataList.get(currentBucketIndex).add(output);
				
			} while (cur.moveToNext());
		}
		cur.close();
		
		// check list (debug)
//		for(int i=0;i<dataList.size();i++) {
//			for(int j=0;j<dataList.get(i).size();j++) {
//				output = dataList.get(i).get(j);
//				Log.i(TAG,"Result ("+i+", "+j+") : "+output.pictureId+", "+output.bucketId);
//			}
//		}
		
		Log.i(TAG, "Time comsumed : "+(System.currentTimeMillis()-startTime));
	}
	
	public int getBucketCount() { return dataList.size(); }
	
	public int getImageCountInBucket(int bucketIndex) { return dataList.get(bucketIndex).size(); }
	
	public ImageStorageData getData(int bucketIndex, int pictureIndex) { return dataList.get(bucketIndex).get(pictureIndex); }
	
	////
	
}
