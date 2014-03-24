package com.nsdb.timebar;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 타임바를 여러개 이어서 사용할 수 있는 타임바의 부모입니다. 마찬가지로 아무것도 화면에 표시하지 않으므로 상속하여 사용하여야 합니다.
 * @author NSDB
 * @see TimeBarBase
 *
 */
public abstract class ConsecutiveTimeBarBase extends TimeBarBase {

	private static final String TAG = ConsecutiveTimeBarBase.class.getSimpleName();
	private ArrayList<Integer> timeList;

	public ConsecutiveTimeBarBase(Context context) {
		super(context);
		init();
	}
	public ConsecutiveTimeBarBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		timeList=new ArrayList<Integer>();
	}

	// time setting
	/**
	 * 타임 리스트를 순서대로 일렬로 늘어서 타임바를 구성합니다.
	 * @param list 타임 리스트
	 */
	public void setTimeList(ArrayList<Integer> list) {
		timeList=list;
		int maxTime=0;
		for(Integer i : list)
			maxTime+=i;
		setMaxTime(maxTime);
	}
	/**
	 * X번 타임바에 현재 커서를 놓습니다.
	 * @param index 타임바 배열 번호 [0, 타임바 갯수-1]
	 * @see #setPosition(long)
	 */
	public void setPositionIndex(int index) {
		if(index<0) index=0;
		else if(index>timeList.size()) index=timeList.size();
		int accTime=0;
		for(int i=0;i<index;i++)
			accTime+=timeList.get(i);
		setPosition(accTime);
	}
	/**
	 * X번 타임바의 맨 처음 위치를 타임바가 진행할 수 있는 최대값으로 정합니다.
	 * @param index 타임바 배열 번호 [0, 타임바 갯수]
	 * @see #setLimitPosition(long)
	 */
	public void setLimitPositionIndex(int index) {
		if(index<0) index=0;
		else if(index>=timeList.size()) index=timeList.size();
		int accTime=0;
		for(int i=0;i<index;i++)
			accTime+=timeList.get(i);
		setLimitPosition(accTime);
	}
	/**
	 * 해당 포지션의 타임바를 지웁니다.
	 * @param position 타임바 배열 번호 [0, 타임바 갯수-1]
	 */
	public void remove(int position) {
		Log.i(TAG,"TimeBar is stopped and current position is set to 0 by remove(int)");
		timeList.remove(position);
		setTimeList(timeList);
	}
	/**
	 * 두 인덱스의 타임바의 위치를 바꿉니다. 커서는 0으로 되돌아갑니다.
	 */
	public void move(int src, int dst) {
		if(src==dst) return;
		Log.i(TAG,"TimeBar is stopped and current position is set to 0 by move(int,int)");
		int time=timeList.get(src);
		timeList.remove(src);
		timeList.add(dst,time);
		setTimeList(timeList);
	}
	/**
	 * 타임바를 현재 진행된 위치를 기준으로 나눕니다. 이미 경계선일 경우 나누지 않습니다. 커서 위치는 유지됩니다.
	 */
	public void divideBar() {
		long currentPosition=getCurrentPosition();
		if(currentPosition==0) return;
		int accTime=0;
		ArrayList<Integer> newTimeList=new ArrayList<Integer>();
		for(Integer i : timeList) {
			if(accTime+i<currentPosition || accTime>currentPosition) {
				newTimeList.add(i);
			} else if(accTime<currentPosition && accTime+i>currentPosition) {
				newTimeList.add((int)(currentPosition-accTime));
				newTimeList.add((int)(accTime+i-currentPosition));
			} else return;
			accTime+=i;
		}
		setTimeList(newTimeList);
		setPosition(currentPosition);
	}
	/**
	 * 해당 포지션과 그 다음 번호의 타임바를 합칩니다. 커서 위치는 유지됩니다.
	 */
	public void combineToNext(int position) {
		if(position>=timeList.size()-1) return;
		long currentPosition=getCurrentPosition();
		int time1=timeList.get(position);
		int time2=timeList.get(position+1);
		timeList.remove(position);
		timeList.remove(position);
		timeList.add(time1+time2);
		setTimeList(timeList);
		setPosition(currentPosition);
	}
		
	// getter
	public ArrayList<Integer> getTimeList() { return timeList; }
	public int getCurrentPositionInCurrentIndex() {
		if(timeList.size()==0) return 0;
		long accTime=0;
		long currentPosition=getCurrentPosition();
		for(Integer i : timeList) {
			if(accTime<=currentPosition && accTime+i>currentPosition)
				return (int)(currentPosition-accTime);
			accTime+=i;
		}
		return timeList.get(timeList.size()-1);
	}
	
}
