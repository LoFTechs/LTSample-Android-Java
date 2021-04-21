package com.loftechs.sample.component;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.util.Log;

import com.loftechs.sample.SampleApp;

import java.util.HashMap;

public class ToneGeneratorUtils {

    private static String TAG = "【DialApp】";
    private static final int PLAY_TONE = 0x01;
    private static final int DTMF_DURATION_MS = 80;
    private Object mToneGeneratorLock = new Object();
    private ToneGenerator mToneGenerator;
    private static boolean mDTMFToneEnabled;
    // 存储DTMF Tones
    public static final HashMap<Character, Integer> mToneMap = new HashMap();

    private static class LazyHolder {
        private static final ToneGeneratorUtils instance = new ToneGeneratorUtils();
    }

    public static ToneGeneratorUtils getInstance() {
        return LazyHolder.instance;
    }

    static {
        mToneMap.put('1', ToneGenerator.TONE_DTMF_1);
        mToneMap.put('2', ToneGenerator.TONE_DTMF_2);
        mToneMap.put('3', ToneGenerator.TONE_DTMF_3);
        mToneMap.put('4', ToneGenerator.TONE_DTMF_4);
        mToneMap.put('5', ToneGenerator.TONE_DTMF_5);
        mToneMap.put('6', ToneGenerator.TONE_DTMF_6);
        mToneMap.put('7', ToneGenerator.TONE_DTMF_7);
        mToneMap.put('8', ToneGenerator.TONE_DTMF_8);
        mToneMap.put('9', ToneGenerator.TONE_DTMF_9);
        mToneMap.put('0', ToneGenerator.TONE_DTMF_0);
        mToneMap.put('#', ToneGenerator.TONE_DTMF_P);
        mToneMap.put('*', ToneGenerator.TONE_DTMF_S);
    }

    public ToneGeneratorUtils() {
        // TODO Auto-generated constructor stub
        try {

            mDTMFToneEnabled = Settings.System.getInt(SampleApp.context.getContentResolver(),
                    Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
            synchronized (mToneGeneratorLock) {
                if (mDTMFToneEnabled && mToneGenerator == null) {
                    mToneGenerator = new ToneGenerator(
                            AudioManager.STREAM_DTMF, 50);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            mDTMFToneEnabled = false;
            mToneGenerator = null;
        }
    }

    public void playTone(char tone) {
        playTone(mToneMap.get(tone));
    }

    /**
     * 播放按鍵聲音
     */
    private void playTone(int tone) {
        if (!mDTMFToneEnabled) {
            return;
        }
        AudioManager audioManager = (AudioManager) SampleApp.context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT
                || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // 靜音或者振動時不發出聲音
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            mToneGenerator.startTone(tone, DTMF_DURATION_MS);
        }
    }

    public void stopTone() {
        if (mToneGenerator != null) {
            mToneGenerator.stopTone();
            mToneGenerator.release();
            mDTMFToneEnabled = false;
            mToneGenerator = null;
        }
    }
}
