package com.kian.butba.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat.Builder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import com.kian.butba.MainActivity;
import com.kian.butba.R;

import java.util.Map;

/**
 * Created by Kian Mistry on 30/12/16.
 */

public class NotificationMessagingService extends FirebaseMessagingService {

	private Builder notificationBuilder;
	private NotificationManager notificationManager;
	private int notificationId;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		//Get data from the notification.
		Map<String, String> data = remoteMessage.getData();
		if(data != null) {
			//Obtain topic: Averages; Events; Rankings.
			String topic = data.get("topic");
			Notification notification = remoteMessage.getNotification();

			//Create notification.
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationBuilder = new Builder(this);
			notificationBuilder.setAutoCancel(true);
			notificationBuilder.setSmallIcon(R.mipmap.ic_logo_launcher);
			notificationBuilder.setWhen(remoteMessage.getSentTime());

			switch(topic) {
				case "Events":
					notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_event));
					notificationId = 1;
					break;
				case "Averages":
				case "Rankings":
					notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_chart));
					notificationId = 2;
					break;
				default:
					break;
			}

			notificationBuilder.setContentTitle(notification.getTitle());
			notificationBuilder.setContentText(notification.getBody());

			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

			notificationBuilder.setContentIntent(pendingIntent);
			notificationManager.notify(notificationId, notificationBuilder.build());
		}
	}
}
