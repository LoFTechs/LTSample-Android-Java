package com.loftechs.sample.profile;

import com.loftechs.sample.base.BaseContract;

public interface ProfileSettingsContract {
    interface View<T> extends BaseContract.View<T> {
        void toastUpdateResult(String returnMsg);

        void setNickname(String nickname);

        void setMuteStatus(boolean mute);

        void setEnableDisplay(boolean enable);

        void setEnableContent(boolean enable);
    }

    interface Presenter extends BaseContract.Presenter {
        void initData();

        void updateNickname(String nickname);

        void updateNotifyMute(boolean mute);

        void updateNotifyPreview(boolean enableDisplay, boolean enableContent);

    }
}
