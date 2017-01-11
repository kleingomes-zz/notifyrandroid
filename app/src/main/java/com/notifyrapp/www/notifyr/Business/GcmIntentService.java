package com.notifyrapp.www.notifyr.Business;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.notifyrapp.www.notifyr.MainActivity;
import com.notifyrapp.www.notifyr.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by K on 1/8/2017.
 */
public class GcmIntentService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String articleTitle = data.getString("articleTitle");
        String articleUrl = data.getString("articleUrl");
        String articleId = data.getString("articleId");
        String articleDescription = data.getString("articleDescription");

        Log.d("NOTIFICATION", from);
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(articleTitle) // title for notification
                .setContentText(articleDescription) // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}

