package com.loftechs.sample.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loftechs.sample.SampleApp;

/**
 * Created by annliu on 2017/12/28.
 */

public class FCMPreManager {

    private static SharedPreferences mPref = null;
    private static Context mContext = null;

    private static String  PRE_VERSION_CODE_KEY= "pref_version_code_key";
    private static String  PRE_GCMTOKEN_KEY= "pref_gcmtoken_key";
    private static String  PRE_GCMTOKEN_EXPIRE_KEY= "pref_gcmtoken_expire_key";

    private static SharedPreferences getPref() {
        if (mPref != null)
            return mPref;
        mContext = SampleApp.context;
        mPref = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return mPref;
    }

    public static int getAppVersionCode() {
        String key = PRE_VERSION_CODE_KEY;
        return getPref().getInt(key, 0);
    }

    public static void setAppVersionCode(int versionCode) {
        String key = PRE_VERSION_CODE_KEY;
        getPref().edit().putInt(key, versionCode).commit();
    }

    public static long getGCMExpire() {
        String key = PRE_GCMTOKEN_EXPIRE_KEY;
        return getPref().getLong(key, 0);
    }

    public static void setGCMExpire(long gcmExpire) {
        String key = PRE_GCMTOKEN_EXPIRE_KEY;
        getPref().edit().putLong(key, gcmExpire).commit();
    }

    public static String getGCMKey() {
        String key = PRE_GCMTOKEN_KEY;
        return getPref().getString(key, "");
    }

    public static void setGCMKey(String gcmKey) {
        String key = PRE_GCMTOKEN_KEY;
        getPref().edit().putString(key, gcmKey).commit();
    }
}
