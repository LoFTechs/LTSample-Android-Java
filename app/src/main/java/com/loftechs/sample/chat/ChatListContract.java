package com.loftechs.sample.chat;

import android.os.Bundle;

import com.loftechs.sample.base.BaseContract;
import com.loftechs.sdk.im.channels.LTChannelResponse;

import java.util.List;

import androidx.fragment.app.Fragment;

public class ChatListContract {

    interface View<T> extends BaseContract.View<T> {
        void refreshChatList(List<LTChannelResponse> items);
        void changeFragment(Fragment f, Bundle intentBundle) ;
        void finish();
    }

    interface Presenter extends BaseContract.Presenter {
        void onItemClick(LTChannelResponse response);
        String getReceiverID();
        String getAccountID();
    }
}
