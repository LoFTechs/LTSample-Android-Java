package com.loftechs.sample.fcm;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.loftechs.sample.R;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.main.MainActivity;
import com.loftechs.sample.model.PreferencesSetting;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MyNotificationManager {
    static final String TAG = MyNotificationManager.class.getSimpleName();
    static final String CHANNEL_ID = "LT_EXAMPLE_PUSH";
    static final int NOTIFY_ID = 2021;


    private static class LazyHolder {
        private static final MyNotificationManager instance = new MyNotificationManager();
    }

    public static MyNotificationManager getInstance() {
        return LazyHolder.instance;
    }


    public void pushNotify(String content) {
        boolean mute = PreferencesSetting.getInstance().getNotificationMute();
        if (mute) {
            Log.d(TAG, "pushNotify is mute!");
            return;
        }
        Intent notificationIntent = new Intent(SampleApp.context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            JSONObject jsonObject = new JSONObject(content);
            boolean enableContent = PreferencesSetting.getInstance().getNotificationContent();
            boolean enableDisplay = PreferencesSetting.getInstance().getNotificationDisplay();
            String contentString = jsonObject.getString("content");
            String display = jsonObject.getString("dispName");
            if (!enableDisplay) { // 內容不顯示
                contentString = "You have a new message";
            } else if (!enableContent) {
                contentString = display + " : Sent a message ";
            } else {
                contentString = display + " : " + contentString;
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SampleApp.context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(ContextCompat.getColor(SampleApp.context, R.color.colorPrimaryDark))
                    .setContentTitle(SampleApp.context.getString(R.string.app_name))
                    .setContentText(contentString)
                    .setContentIntent(PendingIntent.getActivity(SampleApp.context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SampleApp.context);
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = CHANNEL_ID;
                int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setShowBadge(true);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            } else {
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
            }
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NOTIFY_ID, builder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
