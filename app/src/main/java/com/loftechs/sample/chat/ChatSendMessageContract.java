package com.loftechs.sample.chat;

import android.net.Uri;

import com.loftechs.sample.base.BaseContract;

interface ChatSendMessageContract {
    interface View<T> extends BaseContract.View<T> {
        void showShortToast(String message);

        void backStack();

        void setProgress(int value);
    }

    interface Presenter extends BaseContract.Presenter {
        void sendTextMessage(String message);

        void sendImageMessage(Uri uri, Uri thUri);

        void sendDocumentMessage(Uri uri);
    }
}
