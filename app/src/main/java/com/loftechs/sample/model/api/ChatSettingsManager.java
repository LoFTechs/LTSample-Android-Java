package com.loftechs.sample.model.api;


import android.util.Log;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.im.channels.LTChannelPreferenceResponse;
import com.loftechs.sdk.im.channels.LTChannelProfileResponse;
import com.loftechs.sdk.im.channels.LTChannelResponse;
import com.loftechs.sdk.im.channels.LTDismissChannelResponse;
import com.loftechs.sdk.im.channels.LTLeaveChannelResponse;
import com.loftechs.sdk.im.queries.LTQueryChannelsResponse;
import com.loftechs.sdk.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class ChatSettingsManager {
    static final String TAG = ChatSettingsManager.class.getSimpleName();

    private static class LazyHolder {
        private static final ChatSettingsManager instance = new ChatSettingsManager();
    }

    public static ChatSettingsManager getInstance() {
        return LazyHolder.instance;
    }

    /**
     * get channel information
     *
     * @param userID
     * @param channelID
     * @return
     */
    public Observable<LTChannelResponse> getChannelInfo(String userID, String channelID) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTQueryChannelsResponse>>) imManager ->
                        imManager.getChannelHelper().queryChannel(Utils.createTransId(), channelID, false))
                .filter(response -> response.getChannels() != null && !response.getChannels().isEmpty())
                .map(queryChannelsResponse -> {
                    Log.i(TAG, userID + " getChannelInfo : " + queryChannelsResponse.toString());
                    return queryChannelsResponse.getChannels().get(0);
                })
                .doOnError(throwable -> Log.e(TAG, userID + " getChannelInfo error : " + throwable.toString()));
    }

    /**
     * Set channel subject
     *
     * @param userID
     * @param channelID
     * @param subject
     * @return
     */
    public Observable<LTChannelProfileResponse> setChannelSubject(String userID, String channelID, String subject) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTChannelProfileResponse>>) imManager ->
                        imManager.getChannelHelper().setChannelSubject(Utils.createTransId(), channelID, subject))
                .map(setChannelSubjectResponse -> {
                    Log.i(TAG, userID + " setChannelSubject : " + setChannelSubjectResponse.toString());
                    return setChannelSubjectResponse;
                })
                .doOnError(throwable -> Log.e(TAG, userID + " setChannelSubject error : " + throwable.toString()));
    }

    /**
     * Set channel mute status
     *
     * @param userID
     * @param channelID
     * @param mute
     * @return
     */
    public Observable<LTChannelPreferenceResponse> setChannelMute(String userID, String channelID, boolean mute) {
        return LTSDKManager.getIMManager(userID)
                .concatMap((Function<LTIMManager, ObservableSource<LTChannelPreferenceResponse>>) ltimManager ->
                        ltimManager.getChannelHelper().setChannelMute(Utils.createTransId(), channelID, mute))
                .doOnError(throwable -> Log.d(TAG, userID + " setChannelMute doOnError " + throwable.toString()));
    }

    public Observable<LTDismissChannelResponse> dismissChannel(String userID, String channelID) {
        return LTSDKManager.getIMManager(userID)
                .concatMap((Function<LTIMManager, ObservableSource<LTDismissChannelResponse>>) ltimManager
                        -> ltimManager.getChannelHelper().dismissChannel(Utils.createTransId(), channelID))
                .doOnError(throwable -> Log.e(TAG, userID + " dismissChannel doOnError: " + throwable.toString()));
    }

    public Observable<LTLeaveChannelResponse> leaveChannel(String userID, String channelID) {
        return LTSDKManager.getIMManager(userID)
                .concatMap((Function<LTIMManager, ObservableSource<LTLeaveChannelResponse>>) ltimManager
                        -> ltimManager.getChannelHelper().leaveChannel(Utils.createTransId(), channelID))
                .doOnError(throwable -> Log.d(TAG, userID + " leaveChannel doOnError " + throwable.toString()));
    }

    public Observable<String> getSubject(LTChannelProfileResponse response) {
        return getValueInResponse(response, "subject", String.class)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String subject) throws Exception {
                        return subject;
                    }
                });
    }

    /**
     * 判斷 key 是否在 LTChannelProfileResponse 裡 且型態正確
     */
    private <T> Observable<T> getValueInResponse(LTChannelProfileResponse response, String key, Class<T> cls) {
        return Observable.create(emitter -> {
            Object value = response.getChannelProfile().get(key);
            if (value != null && cls.isAssignableFrom(value.getClass())) {
                emitter.onNext((T) value);
                emitter.onComplete();
                return;
            }
            emitter.onError(new Throwable(key.concat("is not exist in LTChannelProfileResponse")));
        });
    }

}
