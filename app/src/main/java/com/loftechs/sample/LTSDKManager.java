package com.loftechs.sample;

import com.google.common.base.Strings;
import com.loftechs.sample.model.AccountPreferenceHelper;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sdk.LTSDKOptions;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.storage.LTStorageManager;
import com.loftechs.sdk.utils.LTLog;
import com.loftechs.sdk.call.LTCallCenterManager;

import io.reactivex.Observable;

public class LTSDKManager {
    private static String TAG = LTSDKManager.class.getSimpleName();
    private static LTSDK ltsdk;
    private static boolean userDataReady;
    public static Observable<LTSDK> getLTSDK() {
        if(ltsdk == null || !userDataReady) {
            AccountEntity accountEntity = AccountPreferenceHelper.getInstance().geFirstAccount();
            LTSDKOptions options = LTSDKOptions.builder()
                    .context(SampleApp.context)
                    .url(BuildConfig.Auth_API)
                    .licenseKey(BuildConfig.License_Key)
                    .userID(accountEntity.getUserID())
                    .uuid(accountEntity.getUuid())
                    .build();
            return  LTSDK.init(options)
                    .map(aBoolean -> {
                        LTLog.i(TAG, "getLTSDK init:"+aBoolean);
                        if(aBoolean && !Strings.isNullOrEmpty(accountEntity.getUuid())) {
                            userDataReady = true;
                        }
                        return ltsdk = LTSDK.getInstance();
                    });
        }
        return Observable.just(ltsdk);
    }

    public static Observable<LTIMManager> getIMManager(String receiverID) {
        return getLTSDK()
                .map(ltsdk -> ltsdk.getIMManager(receiverID));
    }

    public static Observable<LTStorageManager> getStorageManager(String receiverID) {
        return getLTSDK()
                .map(ltsdk -> ltsdk.getStorageManager(receiverID));
    }

    public static Observable<LTCallCenterManager> getCallCenterManager() {
        return getLTSDK()
                .map(ltsdk -> ltsdk.getCallCenterManager());
    }
}

