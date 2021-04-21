package com.loftechs.sample.model.api;

import android.util.Log;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.im.queries.LTQueryUserDeviceNotifyResponse;
import com.loftechs.sdk.im.queries.LTQueryUserProfileResponse;
import com.loftechs.sdk.im.users.LTSetUserProfileResponse;
import com.loftechs.sdk.im.users.LTUserDeviceMuteResponse;
import com.loftechs.sdk.im.users.LTUserDeviceNotifyPreviewResponse;
import com.loftechs.sdk.im.users.LTUserNotifyData;
import com.loftechs.sdk.im.users.LTUserPushTokenResponse;
import com.loftechs.sdk.utils.Utils;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import lombok.NonNull;

/**
 * Created by liusibo on 2021/1/12.
 */
public class UserProfileManager {
    private String TAG = UserProfileManager.class.getSimpleName();

    private static class LazyHolder {
        private static final UserProfileManager instance = new UserProfileManager();
    }

    public static UserProfileManager getInstance() {
        return LazyHolder.instance;
    }

    /**
     * Set nickname
     *
     * @param userID
     * @param nickname
     * @return
     */
    public Observable<LTSetUserProfileResponse> setUserProfile(String userID, String nickname) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTSetUserProfileResponse>>) ltimManager -> {
                    Log.d(TAG, userID + " setUserProfile nickname : " + nickname);
                    return ltimManager.getUserHelper().setUserNickname(Utils.createTransId(), nickname);
                })
                .map(ltSetUserProfileResponse -> {
                    Log.i(TAG, userID + " setUserProfile : " + ltSetUserProfileResponse.toString());
                    return ltSetUserProfileResponse;
                })
                .doOnError(throwable -> {
                    Log.e(TAG, userID + " setUserProfile error : " + throwable.toString());
                });
    }

    /**
     * Get nickname
     *
     * @param userID
     * @return
     */
    public Observable<LTQueryUserProfileResponse> getUserProfile(String userID) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTQueryUserProfileResponse>>) ltimManager -> {
                    Log.d(TAG, userID + " getUserProfile");
                    Set<String> userIDs = new HashSet<>();
                    userIDs.add(userID);
                    return ltimManager.getUserHelper().queryUserProfile(Utils.createTransId(), userIDs, null, "");
                })
                .map(ltQueryUserProfileResponse -> {
                    Log.i(TAG, userID + " getUserProfile : " + ltQueryUserProfileResponse.toString());
                    return ltQueryUserProfileResponse;
                })
                .doOnError(throwable -> Log.e(TAG, userID + " getUserProfile error : " + throwable.toString()));
    }

    /**
     * Get notify
     *
     * @param userID
     * @return
     */
    public Observable<LTUserNotifyData> getDeviceNotify(String userID) {
        return Observable.just(userID)
                .concatMap((Function<String, ObservableSource<LTQueryUserDeviceNotifyResponse>>) receiverID1 -> {
                    Log.i(TAG, userID + " getDeviceNotify");
                    return LTSDKManager.getIMManager(receiverID1).flatMap((Function<LTIMManager, ObservableSource<LTQueryUserDeviceNotifyResponse>>)
                            imManager -> imManager.getUserHelper().queryDeviceNotify(Utils.createTransId()));
                })
                .map(ltQueryUserDeviceNotifyResponse -> {
                    Log.i(TAG, userID + " getDeviceNotify : " + ltQueryUserDeviceNotifyResponse.toString());
                    return ltQueryUserDeviceNotifyResponse.getNotifyData();
                })
                .doOnError(throwable -> Log.e(TAG, userID + " getDeviceNotify doOnError " + throwable.toString()));
    }

    /**
     * Set notify
     *
     * @param userID
     * @param muteAll
     * @return
     */
    public Observable<Boolean> setNotifyMute(String userID, boolean muteAll) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTUserDeviceMuteResponse>>)
                        ltimManager -> ltimManager.getUserHelper().setUserDeviceMute(Utils.createTransId(), muteAll, null))
                .map(ltUserDeviceMuteResponse -> {
                    Log.i(TAG, userID + " setNotifyMute : " + ltUserDeviceMuteResponse.toString());
                    return true;
                })
                .doOnError(throwable -> Log.e(TAG, userID + " setNotifyMute error : " + throwable.toString()));
    }

    /**
     * set Notify Preview
     *
     * @param userID
     * @param hidingSender
     * @param hidingContent
     * @return
     */
    public Observable<Boolean> setNotifyPreview(String userID, boolean hidingSender, boolean hidingContent) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTUserDeviceNotifyPreviewResponse>>)
                        ltimManager -> ltimManager.getUserHelper().SetUserDeviceNotifyPreview(Utils.createTransId(), hidingSender, hidingContent))
                .map(ltUserDeviceNotifyPreviewResponse -> {
                    Log.d(TAG, userID + " setNotifyPreview : " + ltUserDeviceNotifyPreviewResponse.toString());
                    return true;
                });
    }

    /**
     * Get user device notify data
     *
     * @key : FCM token
     */
    public Observable<LTUserPushTokenResponse> setUserPushToken(@NonNull String userID, @NonNull String key) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTUserPushTokenResponse>>)
                        imManager -> imManager.getUserHelper().setUserPushToken(Utils.createTransId(), key))
                .map(ltUserPushTokenResponse -> {
                    Log.i(TAG, userID + " setUserPushToken : " + ltUserPushTokenResponse.toString());
                    return ltUserPushTokenResponse;
                })
                .doOnError(throwable -> Log.e(TAG, userID + " setUserPushToken error : " + throwable.toString()));
    }
}
