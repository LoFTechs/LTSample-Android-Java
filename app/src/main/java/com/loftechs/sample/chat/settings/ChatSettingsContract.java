package com.loftechs.sample.chat.settings;

import com.loftechs.sample.base.BaseContract;

public interface ChatSettingsContract {
    interface View<T> extends BaseContract.View<T> {
        void toastUpdateResult(String returnMsg);

        void setSubject(String subject);

        void enableGroupUI(boolean enable);

        void setNickname(String nickname);

        void setMemberCount(int count);

        void setMuteStatus(boolean mute);

    }

    interface Presenter extends BaseContract.Presenter {
        void initData();

        void updateChannelSubject(String subject);

        void updateNickname(String nickname);

        void updateNotifyMute(boolean mute);

        void dismissChannel();

        void leaveChannel();

    }
}
