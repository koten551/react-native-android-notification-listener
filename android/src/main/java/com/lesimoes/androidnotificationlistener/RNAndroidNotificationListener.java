 
package com.lesimoes.androidnotificationlistener;

import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.app.Notification;
import com.google.gson.Gson;

import com.facebook.react.HeadlessJsTaskService;

public class RNAndroidNotificationListener extends NotificationListenerService {
    private static final String TAG = "RNAndroidNotificationListener";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {       
        Notification statusBarNotification = sbn.getNotification();

        if (statusBarNotification == null || statusBarNotification.extras == null) {
            Log.d(TAG, "The notification received has no data");
            return;
        }

        Context context = getApplicationContext();

        Intent serviceIntent = new Intent(context, RNAndroidNotificationListenerHeadlessJsTaskService.class);

        RNNotification notification = new RNNotification(context, sbn);
        Icon iconInstance = sbn.getNotification().getLargeIcon();
        Drawable iconDrawable = iconInstance.loadDrawable(context);
        Bitmap iconBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
        Resources res = context.getResources();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(iconBitmap, 120, 120, true);
        Bitmap rs = BitmapFactory.decodeResource(res, R.drawable.depart);
        int[] pixels1 = new int[resizedBitmap.getWidth() * resizedBitmap.getHeight()];
        int[] pixels2 = new int[rs.getWidth() * rs.getHeight()];
        resizedBitmap.getPixels(pixels1, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
        rs.getPixels(pixels2, 0, rs.getWidth(), 0, 0, rs.getWidth(), rs.getHeight());
        int threshold = 0;
        for (int i = 0; i < pixels1.length; i++) {
            if(pixels1[i] == pixels2[i]) {
                threshold++;
            }
        }
        Log.d(TAG, "threshold: " + threshold + "/" + pixels1.length);
        Gson gson = new Gson();
        String serializedNotification = gson.toJson(notification);

        serviceIntent.putExtra("notification", serializedNotification);

        HeadlessJsTaskService.acquireWakeLockNow(context);

        context.startService(serviceIntent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}
}