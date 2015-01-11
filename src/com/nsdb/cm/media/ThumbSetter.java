package com.nsdb.cm.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.nsdb.cm.util.BaseThreadingProcessor;

public class ThumbSetter extends BaseThreadingProcessor<ThumbSetter.RequestData> {

	public static class RequestData {
		public long pictureId;
		public ImageView targetView;
	}

	public ThumbSetter(Context applicationContext) {
		super(applicationContext);
	}
	
	
	// public
	
	public void addRequest(long pictureId, ImageView targetView) {
		RequestData data = new RequestData();
		data.pictureId = pictureId;
		data.targetView = targetView;
		super.addRequest(data);
	}
	
	
	// private
	

	@Override
	public boolean onSwitchCheck(RequestData input, RequestData target) {
		return input.targetView == target.targetView;
	}

	@Override
	public void onProcessInput(RequestData request) {
		
		Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
				getApplicationContext().getContentResolver(),
				request.pictureId,
				MediaStore.Images.Thumbnails.MINI_KIND, null);
		
		if(isAvailable(request)) {
			request.targetView.post( new ApplyAction(request.targetView, thumbnail) );
		}
	}
	
	private static class ApplyAction implements Runnable {
		
		private ImageView targetView;
		private Bitmap thumbnail;
		
		public ApplyAction(ImageView targetView, Bitmap thumbnail) {
			this.targetView = targetView;
			this.thumbnail = thumbnail;
		}

		@Override
		public void run() {
			targetView.setImageBitmap(thumbnail);
		}
	}
	
	
}
