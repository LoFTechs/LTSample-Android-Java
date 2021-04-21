package com.loftechs.sample.chat;

import com.loftechs.sample.base.BaseContract;
import com.loftechs.sdk.im.message.LTMessageResponse;

import java.util.List;

interface ChatMessageContract {
    interface View<T> extends BaseContract.View<T> {
        void refreshMessageList(List<LTMessageResponse> items);

        void showShortToast(String message);

        void backStack();
    }

    interface Presenter extends BaseContract.Presenter {
        void onItemClick(LTMessageResponse response);

        String getReceiverID();

        String getSender(LTMessageResponse response);

        String getType(LTMessageResponse response);

        String getContent(LTMessageResponse response);

        boolean getRecall(LTMessageResponse response);

        boolean getDelete(LTMessageResponse response);

        void recallMessage(String msgID);

        void deleteMessage(String msgID);
    }
}
