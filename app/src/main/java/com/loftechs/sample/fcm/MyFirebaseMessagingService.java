package com.loftechs.sample.fcm;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loftechs.sample.LTSDKManager;
import com.loftechs.sample.model.api.CallManager;
import com.loftechs.sdk.LTSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(TAG, "onNewToken token:" + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        checkJsonKey(remoteMessage);
    }

    private void checkJsonKey(RemoteMessage notification) {
        Set<String> keys = notification.getData().keySet();
        for (String s : keys) {
            if (s.equals("json")) {
                handleFCMMessage(notification.getData().get(s));
                break;
            }
        }
    }

    public void handleFCMMessage(String messageJson) {
        Log.i(TAG, "FCM : " + messageJson);
        if (!isJson(messageJson)) {
            MyNotificationManager.getInstance().pushNotify(messageJson);
            return;
        }
        LTSDKManager.getLTSDK()
                .subscribe(new Observer<LTSDK>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTSDK ltsdk) {
                        CallManager.getInstance().parseFCMCallMessage(messageJson);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 非 call content json.
                        MyNotificationManager.getInstance().pushNotify(messageJson);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean isJson(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return false;
        }
        Object parse = null;
        try {
            JSONObject jsonObject = new JSONObject(str);
            String content = jsonObject.getString("content");
            parse = JsonParser.parseString(content);
        } catch (Exception e) {
            return false;
        }
        if (parse instanceof JsonObject
                || parse instanceof JSONArray) {
            return true;
        }
        return false;
    }
}