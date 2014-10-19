package com.nsdb.popup;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotiUtils {
	
	private final int SDK_VERSION = Build.VERSION.SDK_INT;

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void add(Context c, Intent i, String title, String comment, String ticker, int icon, int id) {
    	
		NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);		
        PendingIntent pi=PendingIntent.getActivity(c,0,i,0);
        
        Notification noti=null;
        if(SDK_VERSION >= 16) {
            noti=new Notification.Builder(c)
        	.setContentTitle( title )
        	.setContentText( comment )
        	.setTicker( ticker )
        	.setSmallIcon(icon)
        	.setContentIntent(pi)
        	.build();
        } else if(SDK_VERSION >= 11) {
            noti=new Notification.Builder(c)
        	.setContentTitle( title )
        	.setContentText( comment )
        	.setTicker( ticker )
        	.setSmallIcon(icon)
        	.setContentIntent(pi)
        	.getNotification();
        } else {
        	noti=new Notification(icon, title, System.currentTimeMillis());
        	noti.setLatestEventInfo(c, title, comment, pi);
        }
        noti.flags=noti.flags | Notification.FLAG_ONGOING_EVENT;
        nm.notify(id,noti);
    }
	
	public void cancel(Context c,int id) {
		
		NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);		
		nm.cancel(id);
	}
}
