package com.bspif.app.mobilemechanic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ActionReceiver extends BroadcastReceiver {

	private static final String TAG = "Recver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "onReceive %s", action);
		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			startService(context);  
		}
		if (action.equals("alarm.action")) {
			startService(context);
		}
		if (action.equals(NotificationService.NOTIFICATION_ON_CLICK_INTENT)) {
			NotificationService.onNotificationClick(context);
		}
	}
	
	public static void startService(Context context) {
		Log.d(TAG, "startService");
		Intent i = new Intent();
		i.setClass(context, NotificationService.class);
		context.startService(i);
	}

}
