package com.nsdb.cm.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickMover implements OnClickListener
{
		
	Activity activity;
	Intent intent;
	int requestCode;
	
	public OnClickMover(Activity activity, Intent intent) {
		this.activity=activity;
		this.intent=intent;
		this.requestCode=-1;
	}
	public OnClickMover(Activity activity, Intent intent, int requestCode) {
		this.activity=activity;
		this.intent=intent;
		this.requestCode=requestCode;
	}

	public void onClick(View v) {
		if(requestCode==-1)
			activity.startActivity(intent);
		else
			activity.startActivityForResult(intent,requestCode);
	}		
	
}
