package com.hmkcode.android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmMessageHandler extends IntentService {
	public static final int MESSAGE_NOTIFICATION_ID = 435345;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Retrieve data extras from push notification
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		// Keys in the data are shown as extras
		String title = extras.getString("data");
		String body = extras.getString("m");
		// Create notification or otherwise manage incoming push
		createNotification(title, body);
		// Log receiving message
		Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("data"));
		// Notify receiver the intent is completed
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Creates notification based on title and body received
	private void createNotification(String title, String body) {
		Context context = getBaseContext();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
				.setContentText(body);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
	}

}