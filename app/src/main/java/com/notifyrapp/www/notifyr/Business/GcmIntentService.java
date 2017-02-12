package com.notifyrapp.www.notifyr.Business;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GcmListenerService;
import com.notifyrapp.www.notifyr.MainActivity;
import com.notifyrapp.www.notifyr.R;

import java.util.Date;

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
        int color = (0x00aaFF);
     ////   int color = getResources().getColor(R.color.my_notif_color);
     //   int color = ContextCompat.getColor(getApplicationContext(), R.color.colorNotifyrLightBlue);
        Log.d("NOTIFICATION", from);
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
          //      .setSmallIcon(R.mipmap.ic_launcher) // notification icon
          //      .setContentTitle("This is a long test to see how long it will go in terms of length") // title for notification
          //      .setContentText("This is a long test to see how long it will go in terms of length") // message for notification
                .setColor(color)
                .setTicker(articleTitle)
                .setVisibility(View.VISIBLE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true); // clear notification after click
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_whitelogo);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(articleTitle));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_whitelogo);
            mBuilder.setContentTitle(articleTitle); // title for notification
            mBuilder.setContentText("Read More...");
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.large_icon);
     //   mBuilder.setLargeIcon(largeIcon);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isIncomingNotification", true);
        intent.putExtra("articleUrl", articleUrl);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int random_id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        int aId = Integer.parseInt(articleId);
        mNotificationManager.notify(random_id, mBuilder.build());
    }
}

