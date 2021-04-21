package com.loftechs.sample.chat;

import com.loftechs.sample.base.BaseContract;
import com.loftechs.sdk.im.message.LTMemberModel;

import java.util.Set;

interface ChatCreateContract {
    interface View<T> extends BaseContract.View<T> {
        void toastUpdateResult(String returnMsg);

        void backStack();

        void setCheckBtnVisiable();
    }

    interface Presenter extends BaseContract.Presenter {
        void createGroupChannel(String subject);

        void createSingleChannel();

        void inviteMember(String userID, String chID);

        void checkAccount(String account);

        void removeAccount(String account);

        void setNickname(String account, String nickname);

        Set<LTMemberModel> getLTMemberModels();

        Boolean isVaildAccount(String account);
    }
}
