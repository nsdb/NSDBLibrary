package com.nsdb.movie;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

/**
 * 영상을 간편하게 재생할 수 있는 뷰입니다. 진행 원리는 SurfaceMovieView와 거의 유사하지만,
 * MovieList의 영상들을 재생하기 위해 제작된 뷰로 일부 추가, 제거된 부분이 있습니다. (큰 신경은 안써도 됩니다)<br>
 * <br>
 * SurfaceView의 문제점인 뷰의 이동 불가능 문제를 해결하고자 하는 것이 아니라면 SurfaceMovieView를 사용하길 권장합니다.<br>
 * <br>
 * TextureView는 API 4.0 이상에서만 쓸 수 있기 때문에, 이 뷰도 API 4.0 이상에서만 사용 가능합니다.
 * @author NSDB
 * @see SurfaceMovieView
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureMovieView extends FrameLayout {

	private static final String TAG = TextureMovieView.class.getSimpleName();
	// layout
	private ProgressBar indicator;
	private ImageView thumbnail;
	private TextureView texture;
	// movie
	private String path;
	private MediaPlayer mp;
	private Surface surface;
	private boolean prepared;
	private boolean startOnPrepare;

	// init
	public TextureMovieView(Context context) {
		super(context);
		init();
	}
	public TextureMovieView(Context context, AttributeSet attrs) {
		super(context,attrs);
		init();
	}
	public TextureMovieView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		init();
	}	
	private void init() {
		// view
		LayoutParams baseParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		texture=new TextureView(getContext());
		texture.setSurfaceTextureListener(textureListener);
		addView(texture, baseParams);
		thumbnail=new ImageView(getContext());
		thumbnail.setScaleType(ScaleType.CENTER_CROP);
		thumbnail.setBackgroundColor(0xFF000000);
		addView(thumbnail,baseParams);
		indicator=new ProgressBar(getContext(),null,android.R.attr.progressBarStyleLarge);
		indicator.setVisibility(View.INVISIBLE);
		LayoutParams indicatorParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		indicatorParams.gravity=Gravity.CENTER;
		addView(indicator,indicatorParams);
		// media
		path="";
		startOnPrepare=true;
	}
	
	// thumbnail
	public void showThumbnail(Bitmap b) {
		Log.i(TAG,"Show thumbnail");
		thumbnail.setImageBitmap(b);
		restoreMovie();
	}
	
	// movie
	public void showMovie(String path) {
		Log.i(TAG,"Show movie");
		// same path check
		if(path==null || path.equals("")) {
			Log.w(TAG,"Empty path");
			return;
		} else if(this.path.equals(path) && mp!=null) {
			if(prepared) resumeMovie();
			else Log.i(TAG,"Player is now preparing...");
			return;
		}
		// clear
		restoreMovie();
		this.path=path;
		if(texture.isAvailable())
			preparePlayer();
			
	}
	
	@Override
	protected void onDetachedFromWindow() {
		Log.i(TAG, "TextureMovieView detached");
		super.onDetachedFromWindow();
	}
	
	
	@Override
	protected void onAttachedToWindow() {
		Log.i(TAG, "TextureMovieView attached");
		
		super.onAttachedToWindow();
	}
	
	private SurfaceTextureListener textureListener=new SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			Log.i(TAG, "Surface created.");
			if(!path.equals("")) preparePlayer();
		}
		@Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			Log.i(TAG, "Surface destroyed.");
			restoreMovie();
			return true;
		}
		@Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
		@Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			// if playing, remove thumbnail
			if(mp!=null && mp.getCurrentPosition()>0) {
				if(thumbnail.getVisibility() != View.INVISIBLE)
					thumbnail.setVisibility(View.INVISIBLE);
				if(indicator.getVisibility() != View.INVISIBLE)
					indicator.setVisibility(View.INVISIBLE);
			}
		}
	};
	// must after showMovie and onSurfaceTextureAvailable
	private void preparePlayer() {
		Log.i(TAG,"Prepare media player");
		// prepare
		try {
			indicator.setVisibility(View.VISIBLE);
			mp=new MediaPlayer();
			mp.setOnPreparedListener(mediaListener);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setLooping(true);
			surface=new Surface(texture.getSurfaceTexture());
			mp.setSurface(surface);
			mp.setDataSource(path);
			mp.prepareAsync();
		} catch(Exception e) {
			e.printStackTrace();
			restoreMovie();
		}
	}
	private OnPreparedListener mediaListener=new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.i(TAG,"prepared");
			prepared=true;
			if(startOnPrepare) mp.start();
		}		
	};
	
	public void resumeMovie() { 
		Log.i(TAG,"Resume");
		if(isPrepared())
			mp.start();
		startOnPrepare=true;
	}
	public void pauseMovie() {
		Log.i(TAG,"Pause");
		if(isPrepared())
			mp.pause();
		startOnPrepare=false;
	}
	
	public void seekMovie(int position) {
		Log.i(TAG, "Seek : "+position);
		if(isPrepared())
			mp.seekTo(position);
	}
	
	public void restoreMovie() {
		Log.i(TAG,"Restore movie");
		prepared=false;
		if(mp!=null) {
			Log.i(TAG,"Release media player");
			mp.release();
			mp=null;
			Log.i(TAG,"Release virtual surface");
			surface.release();
			surface=null;
		}
		thumbnail.setVisibility(View.VISIBLE);
		indicator.setVisibility(View.INVISIBLE);
		path="";
		startOnPrepare=true;
		Log.i(TAG,"Restore end");
	}
	
	public boolean isPreparing() { return mp!=null && !prepared; }
	public boolean isPrepared() { return mp!=null && prepared; }
	public boolean isPlaying() { return (mp!=null && prepared && mp.isPlaying()); }
	public int getCurrentPosition() {
		if(isPrepared()) return mp.getCurrentPosition();
		else return 0;
	}
	public int getDuration() {
		if(isPrepared()) return mp.getDuration();
		else return 0;
	}
	public ImageView getThumbnailView() { return thumbnail; }
	
}
