package com.loftechs.sample.utils;


import android.Manifest;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by annliu on 2018/4/24.
 */

public class PermissionUtil {
    @Getter
    @Accessors(prefix = "m")
    public static String[] mMustPerms = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    @Getter
    @Accessors(prefix = "m")
    public static String[] mVoicePerms = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO //不能用 permission_group.MICROPHONE，有些手機會認不出來
            };
    @Getter
    @Accessors(prefix = "m")
    public static String mCameraPerm = Manifest.permission.CAMERA;
    @Getter
    @Accessors(prefix = "m")
    public static String mLocationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    @Getter
    @Accessors(prefix = "m")
    public static String mReadContactsPerm = Manifest.permission.READ_CONTACTS;
    @Getter
    @Accessors(prefix = "m")
    public static String mWriteContactsPerm = Manifest.permission.WRITE_CONTACTS;
    @Getter
    @Accessors(prefix = "m")
    public static String mPhonePerm = Manifest.permission.READ_PHONE_STATE;
    @Getter
    @Accessors(prefix = "m")
    public static String mStoragePerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    @Getter
    @Accessors(prefix = "m")
    public static String mMicrophonePerm = Manifest.permission.RECORD_AUDIO;
    @Getter
    @Accessors(prefix = "m")
    public static String mReadSMSPerm = Manifest.permission.READ_SMS;
    @Getter
    @Accessors(prefix = "m")
    public static String mReadCallLogPerm = Manifest.permission.READ_CALL_LOG;
}

