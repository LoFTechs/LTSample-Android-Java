package com.loftechs.sample.fcm;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.utils.VersionUtil;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.LTSDKNoInitializationException;
import com.loftechs.sdk.http.response.LTResponse;

import java.util.Calendar;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FCMTokenHelper {
    public static final String TAG = FCMTokenHelper.class.getSimpleName();
    public static final long EXPIRY_TIME_MS = 1000 * 3600 * 24 * 3;

    private static class LazyHolder {
        private static final FCMTokenHelper instance = new FCMTokenHelper();
    }

    public static FCMTokenHelper getInstance() {
        return LazyHolder.instance;
    }

    /**
     * only check main account and corpBK account, without corpIM account.
     */
    public void checkFCMtokenByAccount(OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(listener);
    }

    public static void performUpdate() {
        if (!shouldUpdateGcmKey()) {
            Log.d(TAG, "performUpdate no need to update");
            return;
        }
        String registrationId = FirebaseInstanceId.getInstance().getToken();
        handleKey(registrationId);
    }

    public static void saveGcmKey(String registrationId) {
        long exired = Calendar.getInstance().getTimeInMillis() + EXPIRY_TIME_MS;
        FCMPreManager.setGCMExpire(exired);
        FCMPreManager.setGCMKey(registrationId);
    }

    private static boolean shouldUpdateGcmKey() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long expiryTime = FCMPreManager.getGCMExpire();
        boolean isTimeExpired = currentTime > expiryTime;
        int currentVersionCode = VersionUtil.getAppVersionCode(SampleApp.context);
        int savedVersionCode = FCMPreManager.getAppVersionCode();
        boolean isVersionUpdated = currentVersionCode > savedVersionCode;

        if (isVersionUpdated) {
            FCMPreManager.setAppVersionCode(currentVersionCode);
        }

        boolean isInvalidKey = Strings.isNullOrEmpty(FCMPreManager.getGCMKey());
        return isTimeExpired || isVersionUpdated || isInvalidKey;
    }

    public static void handleKey(String registrationId) {
        Log.d(TAG, "registrationId = " + registrationId);

        if (Strings.isNullOrEmpty(registrationId)) {
            Intent intent = new Intent(SampleApp.context, MyFirebaseInstanceIDService.class);
            SampleApp.context.startService(intent);
            return;
        }
        updateKeyWithServer(registrationId);
    }

    private static void updateKeyWithServer(final String registrationId) {
        Log.d(TAG, "Send key to server");
        try {
            LTSDK.getInstance().updateNotificationKey(registrationId)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<LTResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LTResponse LTResponse) {
                            Log.d(TAG, "sendTokenByAPI response receiverID :  returnCode : " + LTResponse.getReturnCode());
                            saveGcmKey(registrationId);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, " sendTokenByAPI error : " + e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (
                LTSDKNoInitializationException e) {
            e.printStackTrace();
        }
    }

}
