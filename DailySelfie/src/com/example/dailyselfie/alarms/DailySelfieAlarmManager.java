package com.example.dailyselfie.alarms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

public class DailySelfieAlarmManager {

	private Activity mActivity;
	private AlarmManager mAlarmManager;
	private Intent mIntent;
	private PendingIntent mPendingIntent;

	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;

	public DailySelfieAlarmManager(Activity activity) {
		this.mActivity = activity;
		mAlarmManager = (AlarmManager) mActivity.getSystemService(Activity.ALARM_SERVICE);
		mIntent = new Intent(mActivity, DailySelfieAlarmReceiver.class);
		mPendingIntent = PendingIntent.getBroadcast(mActivity, 0, mIntent, 0);
	}

	public void setUpAlarm(long hours, long minutes, long seconds) {
		setUpAlarm(hours * HOUR + minutes * MINUTE + seconds * SECOND);
	}

	public void setUpAlarm(long delayInMillis) {
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + delayInMillis, delayInMillis,
				mPendingIntent);
	}

	public void removeAlarm() {
		mAlarmManager.cancel(mPendingIntent);
	}

}
