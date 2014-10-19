package com.nsdb.util;

import java.util.ArrayList;

import android.content.Context;

public abstract class BaseThreadingProcessor<T> extends Thread {

	private Context applicationContext;
	private ArrayList<T> requestList = new ArrayList<T>();
	private boolean released;
	
	public BaseThreadingProcessor(Context applicationContext) {
		super();
		this.applicationContext = applicationContext;
	}

	public void addRequest(T request) {
		
		synchronized(requestList) {
			for(int i=0;i<requestList.size();i++) {
				if(onSwitchCheck(request, requestList.get(i))) {
					requestList.remove(i--);
				}
			}
			requestList.add(request);
			requestList.notify();
		}
	}
	
	public void clearRequest() {
		synchronized(requestList) {
			requestList.clear();
		}
	}
	
	public void release() {
		released = true;
		synchronized(requestList) {
			requestList.notify();
		}
	}
	
	@Override
	public void run() {
		super.run();
		T request;
		boolean available;
		while(!released) {
			try {
				
				// get data
				synchronized(requestList) {
					if(requestList.size() == 0) requestList.wait();
					if(released) return;
					request = requestList.get(0);
				}
				
				// do process
				onProcessInput(request);
				
				// remove at list
				synchronized(requestList) {
					available = requestList.size() > 0 && requestList.get(0) == request;
					if(available) requestList.remove(0);
				}
				
			} catch (InterruptedException e) { continue; }
		}
		
	}
	
	
	public boolean onSwitchCheck(T input, T target) { return false; }
	
	/** Another Thread Process */
	public abstract void onProcessInput(T request);
	
	public Context getApplicationContext() { return applicationContext; }
	
	protected boolean isAvailable(T request) {
		synchronized(requestList) {
			return (requestList.size() > 0 && requestList.get(0) == request);
		}
	}
	
}
