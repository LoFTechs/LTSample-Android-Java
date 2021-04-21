package com.loftechs.sample.chat.settings;

import com.loftechs.sample.common.event.ChannelCloseEvent;
import com.loftechs.sample.model.api.ChatSettingsManager;
import com.loftechs.sdk.im.channels.LTChannelPreferenceResponse;
import com.loftechs.sdk.im.channels.LTChannelProfileResponse;
import com.loftechs.sdk.im.channels.LTChannelResponse;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.channels.LTDismissChannelResponse;
import com.loftechs.sdk.im.channels.LTLeaveChannelResponse;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChatSettingsPresenter implements ChatSettingsContract.Presenter {
    final ChatSettingsContract.View<?> mView;
    @Getter
    @Setter
    @Accessors(prefix = "m")
    public String mUserID;
    @Getter
    @Setter
    @Accessors(prefix = "m")
    public String mChID;

    public ChatSettingsPresenter(@NonNull ChatSettingsContract.View view, String userID, String chID) {
        mView = checkNotNull(view, "view cannot be null!");
        mUserID = userID;
        mChID = chID;
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
        getChannelInfo();
    }

    @Override
    public void updateChannelSubject(String subject) {
        ChatSettingsManager.getInstance().setChannelSubject(mUserID, mChID, subject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTChannelProfileResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTChannelProfileResponse ltChannelProfileResponse) {
                        mView.setSubject(subject);
                        mView.toastUpdateResult("updateChannelSubject Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("updateChannelSubject error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void getChannelInfo() {
        ChatSettingsManager.getInstance().getChannelInfo(mUserID, mChID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTChannelResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTChannelResponse ltChannelResponse) {
                        mView.setSubject(ltChannelResponse.getSubject());
                        mView.setMuteStatus(ltChannelResponse.isMute());
                        mView.enableGroupUI(LTChannelType.SINGLE == ltChannelResponse.getChType() ? false : true);
//                        mView.setMemberCount(ltChannelResponse.getMemberCount());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("getChannelInfo error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateNickname(String nickname) {
//        ProfileManager.getInstance().setUserProfile(mUserID, nickname)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<LTSetUserProfileResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(LTSetUserProfileResponse ltSetUserProfileResponse) {
//                        mView.toastUpdateResult("updateNickname Success.");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mView.toastUpdateResult("updateNickname error.");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    @Override
    public void updateNotifyMute(boolean mute) {
        ChatSettingsManager.getInstance().setChannelMute(mUserID, mChID, mute)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTChannelPreferenceResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTChannelPreferenceResponse ltChannelPreferenceResponse) {
                        mView.toastUpdateResult("updateNotifyMute Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("updateNotifyMute error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void dismissChannel() {
        ChatSettingsManager.getInstance().dismissChannel(mUserID, mChID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTDismissChannelResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTDismissChannelResponse ltDismissChannelResponse) {
                        mView.toastUpdateResult("dismissChannel Success.");
                        EventBus.getDefault().post(new ChannelCloseEvent(mUserID, mChID));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("dismissChannel error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void leaveChannel() {
        ChatSettingsManager.getInstance().leaveChannel(mUserID, mChID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTLeaveChannelResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTLeaveChannelResponse ltLeaveChannelResponse) {
                        mView.toastUpdateResult("leaveChannel Success.");
                        EventBus.getDefault().post(new ChannelCloseEvent(mUserID, mChID));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("leaveChannel error.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
