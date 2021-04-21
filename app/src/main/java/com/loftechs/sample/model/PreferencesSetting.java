package com.loftechs.sample.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loftechs.sample.SampleApp;


public class PreferencesSetting {

    private static final String PREF_TOKEN = "pref_token";
    private static final String PREF_NOTIFICATION_DISPLAY = "pref_notification_display";
    private static final String PREF_NOTIFICATION_CONTENT = "pref_notification_content";
    private static final String PREF_NOTIFICATION_MUTE = "pref_notification_mute";

    private static SharedPreferences mPref;
    private static SharedPreferences.Editor mEditor;

    private static class LazyHolder {
        private static final PreferencesSetting instance = new PreferencesSetting();
    }

    public static PreferencesSetting getInstance() {
        return LazyHolder.instance;
    }

    public SharedPreferences getPref() {
        if (null == mPref) {
            mPref = PreferenceManager.getDefaultSharedPreferences(SampleApp.context);
        }
        return mPref;
    }

    public SharedPreferences.Editor getEditor() {
        if (null == mEditor) {
            mEditor = getPref().edit();
        }
        return mEditor;
    }

    public void setToken(String token) {
        getEditor().putString(PREF_TOKEN, token).commit();
    }

    public String getToken() {
        return getPref().getString(PREF_TOKEN, "");
    }

    public void setNotificationDisplay(boolean enable) {
        getEditor().putBoolean(PREF_NOTIFICATION_DISPLAY, enable).commit();
    }

    public Boolean getNotificationDisplay() {
        return getPref().getBoolean(PREF_NOTIFICATION_DISPLAY, true);
    }

    public void setNotificationContent(boolean enable) {
        getEditor().putBoolean(PREF_NOTIFICATION_CONTENT, enable).commit();
    }

    public Boolean getNotificationContent() {
        return getPref().getBoolean(PREF_NOTIFICATION_CONTENT, true);
    }

    public void setNotificationMute(boolean enable) {
        getEditor().putBoolean(PREF_NOTIFICATION_MUTE, enable).commit();
    }

    public Boolean getNotificationMute() {
        return getPref().getBoolean(PREF_NOTIFICATION_MUTE, false);
    }
}
