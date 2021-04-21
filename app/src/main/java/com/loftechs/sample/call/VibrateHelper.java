package com.loftechs.sample.call;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;

public class VibrateHelper {
    private static final long PRESS_VIBRATE_PERIOD = 40; // ms
    public static final long[] PATTERN_MESSAGE_NOTIF = new long[]{0, 300, 100, 200};
    public static final long[] PATTERN_OFF_NOTIF = new long[]{0, 0};

    public static void onPressed(Context context) {
        int hapticFeedback = 0;
        try {
            hapticFeedback = Settings.System.getInt(context.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (hapticFeedback == 1) {
            vibrate(context, PRESS_VIBRATE_PERIOD);
        }
    }

    public static boolean shouldVibrate(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            AudioManager audioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
            int setting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
            return (setting == AudioManager.VIBRATE_SETTING_ON);
        }
        return true;
    }

    public static void vibrate(Context context, long ms) {
        Vibrator vib = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
        vib.vibrate(ms);
    }
}
