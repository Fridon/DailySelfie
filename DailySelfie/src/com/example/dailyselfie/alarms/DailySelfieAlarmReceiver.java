package com.example.dailyselfie.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dailyselfie.R;
import com.example.dailyselfie.activities.MainActivity;

public class DailySelfieAlarmReceiver extends BroadcastReceiver {

	private static final int DAILY_SELFIE_NOTIFICATION_ID = 1;
	
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// The Intent to be used when the user clicks on the Notification View
		mNotificationIntent = new Intent(context, MainActivity.class);
		// The PendingIntent that wraps the underlying Intent
		mContentIntent = PendingIntent.getActivity(context, 0,
				mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Build the Notification
		Notification.Builder notificationBuilder = new Notification.Builder(context);
		notificationBuilder.setTicker(context.getText(R.string.alarm_ticker_text));
		notificationBuilder.setSmallIcon(android.R.drawable.ic_menu_camera);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setContentTitle(context.getText(R.string.content_title));
		notificationBuilder.setContentText(context.getText(R.string.content_text));
		notificationBuilder.setContentIntent(mContentIntent);
				
		// Get the NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(DAILY_SELFIE_NOTIFICATION_ID, notificationBuilder.build());

	}

}
