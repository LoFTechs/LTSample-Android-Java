package com.loftechs.sample.profile;

import com.loftechs.sample.model.PreferencesSetting;
import com.loftechs.sample.model.api.UserProfileManager;
import com.loftechs.sdk.im.queries.LTQueryUserProfileResponse;
import com.loftechs.sdk.im.users.LTSetUserProfileResponse;
import com.loftechs.sdk.im.users.LTUserNotifyData;
import com.loftechs.sdk.im.users.LTUserProfile;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileSettingsPresenter implements ProfileSettingsContract.Presenter {
    final ProfileSettingsContract.View<?> mView;
    String mUserID;

    public ProfileSettingsPresenter(@NonNull ProfileSettingsContract.View view, String userID) {
        mView = checkNotNull(view, "view cannot be null!");
        mUserID = userID;
    }

    @Override
    public void create() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void initData() {
        getNickname();
        getNotificationStatus();
    }

    private void getNickname() {
        UserProfileManager.getInstance().getUserProfile(mUserID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTQueryUserProfileResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTQueryUserProfileResponse ltQueryUserProfileResponse) {
                        List<LTUserProfile> ltUserProfiles = ltQueryUserProfileResponse.getResult();
                        mView.setNickname((null != ltUserProfiles && !ltUserProfiles.isEmpty()) ? ltUserProfiles.get(0).getNickname() : mUserID);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("getNickname error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getNotificationStatus() {
        UserProfileManager.getInstance().getDeviceNotify(mUserID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTUserNotifyData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTUserNotifyData ltUserNotifyData) {
                        mView.setMuteStatus(ltUserNotifyData.isMute());
                        mView.setEnableDisplay(!ltUserNotifyData.isHidingCaller());
                        mView.setEnableContent(!ltUserNotifyData.isHidingContent());
                        PreferencesSetting.getInstance().setNotificationMute(ltUserNotifyData.isMute());
                        PreferencesSetting.getInstance().setNotificationContent(!ltUserNotifyData.isHidingContent());
                        PreferencesSetting.getInstance().setNotificationDisplay(!ltUserNotifyData.isHidingCaller());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("getNotificationStatus error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateNickname(String nickname) {
        UserProfileManager.getInstance().setUserProfile(mUserID, nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTSetUserProfileResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTSetUserProfileResponse ltSetUserProfileResponse) {
                        mView.toastUpdateResult("updateNickname Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("updateNickname error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateNotifyMute(boolean mute) {
        UserProfileManager.getInstance().setNotifyMute(mUserID, mute)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        PreferencesSetting.getInstance().setNotificationMute(mute);
                        mView.toastUpdateResult("updateMuteStatus Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("updateMuteStatus error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateNotifyPreview(boolean enableDisplay, boolean enableContent) {
        UserProfileManager.getInstance().setNotifyPreview(mUserID, !enableDisplay, !enableContent)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        PreferencesSetting.getInstance().setNotificationContent(enableContent);
                        PreferencesSetting.getInstance().setNotificationDisplay(enableDisplay);
                        mView.toastUpdateResult("updateNotifyPreview Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("updateNotifyPreview error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
