package com.loftechs.sample.call;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;

import com.loftechs.sample.model.api.CallManager;

import androidx.fragment.app.FragmentActivity;

public class SoftVolumeActivity  extends FragmentActivity {

    private static boolean preventVolumeBarToDisplay = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyVolumeAdjust(keyCode)) return true;
        if (onKeyBackGoHome(this, keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }


    private boolean onKeyVolumeAdjust(int keyCode) {
        if (!((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || Build.VERSION.SDK_INT >= 15)) {
            return false; // continue
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            CallManager.getInstance().adjustVolume(1);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            CallManager.getInstance().adjustVolume(-1);
        }
        return true;
    }

    private boolean onKeyBackGoHome(Activity activity, int keyCode, KeyEvent event) {
        if (!(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            return false; // continue
        }

        activity.startActivity(new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME));
        return true;
    }
}
