package com.nsdb.timebar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;


/**
 * 모든 타임 바의 부모 클래스입니다. 이 객체 자체는 아무것도 화면에 표시하지 않으므로, 상속하여 사용하여야 합니다.
 * @author NSDB
 * @see Synchronizer
 *
 */
public class TimeBarBase extends LinearLayout {

	private static final String TAG = TimeBarBase.class.getSimpleName();
	
	// time
	private long maxTime;
	private long currentPosition;
	private long limitPosition;
	// play
	private boolean started;
	private long startFMS;
	// sync
	private Synchronizer sync;
	// update
	private Runnable updater;
	private boolean updaterDismissed;
	
	
	public TimeBarBase(Context context) {
		super(context);
		init();
	}
	public TimeBarBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		setWillNotDraw(false);
		updater=new Runnable() {
			@Override
			public void run() {
				requestSync();
				updateTime();
				if(!updaterDismissed)
					postDelayed(this,30);
			}
		};
		post(updater);
	}
	
	
	
	// set
	/**
	 * 타임 바의 최대 시간을 정합니다. millisecond 단위입니다.
	 * @param value 최대 시간 (ms)
	 */
	public void setMaxTime(long value) {
		if(maxTime==value) return;
		Log.i(TAG,"Set max time : "+value);
		stop();
		maxTime=value;
		currentPosition=0;
		limitPosition=value;
		invalidate();
		updateView();
	}
	/**
	 * 타임 바의 커서의 위치를 바꿉니다. millisecond 단위입니다.
	 * @param position 커서의 위치 (ms)
	 */
	public void setPosition(long position) {
		if(position<0) position=0;
		if(position>limitPosition) position=limitPosition;
		if(position==currentPosition) return;
		Log.i(TAG,"Set position : "+position);
		currentPosition=position;
		invalidate();
		updateView();
	}
	/**
	 * 타임 바의 커서가 최대로 도달할 수 있는 값을 정합니다. millisecond 단위입니다.
	 * @param position 최대로 도달할 수 있는 값 (ms)
	 */
	public void setLimitPosition(long position) {
		if(position<0) position=0;
		else if(position>maxTime) position=maxTime;
		if(position==limitPosition) return;
		Log.i(TAG,"Set limit position : "+position);
		limitPosition=position;
		setPosition(currentPosition);
	}
	
	
	// play
	/**
	 * 타임바가 움직이게 합니다. 무조건 변화한 시간(ms)만큼 값이 증가합니다.<br>
	 * (1초에서 1200이었다면 2초에서 2200)
	 * @see #stop()
	 */
	public void start() {
		if(started) return;
		Log.i(TAG,"Start");
		started=true;
		startFMS=System.currentTimeMillis();
		post(updater);
	}
	/**
	 * 타임바를 멈춥니다.
	 * @see #start()
	 */
	public void stop() {
		if(!started) return;
		Log.i(TAG,"Stop");
		updateTime();
		started=false;
	}
	
	// update
	/**
	 * 타임바의 커서를 업데이트합니다. 매번 updater가 호출하므로 일부러 호출하지 않아도 됩니다.
	 */
	protected void updateTime() {
		if(started) {
			long newPosition=currentPosition+(System.currentTimeMillis()-startFMS);
			startFMS=System.currentTimeMillis();
			setPosition(newPosition);
		}
	}
	/**
	 * 타임바의 커서가 변할때마다 호출되는 메서드입니다. 커서와 관련이 있는 뷰를 업데이트해주십시요.
	 * requestLayout()을 잊지 마십시요.
	 */
	protected void updateView() {
		//requestLayout();
	}
	
	
	// updater
	@Override
	protected void onAttachedToWindow() {
		Log.i(TAG,"onAttachedToWindow");
		super.onAttachedToWindow();
		updaterDismissed=false;
		post(updater);
	}	
	@Override
	protected void onDetachedFromWindow() {
		Log.i(TAG,"onDetachedToWindow");
		super.onDetachedFromWindow();
		updaterDismissed=true;
	}
	
	
	// sync
	public void setSynchronizer(Synchronizer s) {
		sync=s;
		requestSync();
	}
	private void requestSync() {
		if(sync instanceof Synchronizer)
			sync.synchronize(this);
	}
	/**
	 * 타임바와 상태를 공유하여 타임바의 값을 수시로 받거나, 타임바의 값을 수시로 조절하는 인터페이스입니다.<br>
	 * setSynchronizer로 해당 객체를 붙이면, 주기적으로 synchronize 메서드가 호출되어 서로 동기화할 기회를 갖게 됩니다.
	 * @author NSDB
	 *
	 */
	public interface Synchronizer {
		public void synchronize(TimeBarBase bar);
	}
	
	
	// get
	public long getCurrentPosition() { return currentPosition; }
	public long getMaxTime() { return maxTime; }
	public boolean isStarted() { return started; }
	public Synchronizer getSynchronizer() { return sync; }
	
	
	
}
