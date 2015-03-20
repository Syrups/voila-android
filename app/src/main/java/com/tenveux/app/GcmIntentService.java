package com.tenveux.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tenveux.app.activities.MainActivity;
import com.tenveux.app.activities.PropositionsActivity;
import com.tenveux.app.activities.menu.friends.FriendSearchRequestActivity;
import com.tenveux.app.activities.menu.friends.NetworkActivity;

/**
 * Created by theGlenn on 13/03/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    private static final String TAG = "VoilaGCM";

    private static final int SERVED = 0, TAKEN = 1, FRIEND_WANNA = 2, FRIEND_ACCEPTED = 3;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        Log.i(TAG, "onHandleIntent" + messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle extras) {

        int type = Integer.parseInt(extras.getString("type"));
        String name = extras.getString("name");

        String[] messages = getResources().getStringArray(R.array.notification_messages);
        String msg = String.format(messages[type], name);


        Class activityClass = MainActivity.class;
        switch (type) {
            case SERVED:
                activityClass = PropositionsActivity.class;
                break;
            case TAKEN:
                activityClass = PropositionsActivity.class;
                break;
            case FRIEND_WANNA:
                activityClass = FriendSearchRequestActivity.class;
                break;
            case FRIEND_ACCEPTED:
                activityClass = MainActivity.class;
                break;
        }
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, activityClass), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)

                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentIntent(contentIntent)
                        .setContentText(msg);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}