package com.loftechs.sample.chat;

import android.os.Bundle;
import android.util.Log;

import com.loftechs.sample.model.api.ChatFlowManager;
import com.loftechs.sdk.im.channels.LTChannelResponse;
import com.loftechs.sdk.im.queries.LTQueryChannelsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_TYPE;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatListPresenter implements ChatListContract.Presenter {

    private ChatListContract.View mView;
    private String mReceiverID;
    private String mAccountID;
    List<LTChannelResponse> channels;

    public ChatListPresenter(ChatListContract.View view, String receiverID, String accountID) {
        mView = view;
        mReceiverID = receiverID;
        mAccountID = accountID;
    }

    @Override
    public void create() {
    }

    private List<LTChannelResponse> getChannels() {
        if (channels == null) {
            channels = new ArrayList<>();
        }
        return channels;
    }

    private void prepareData() {
        ChatFlowManager.getInstance().queryChannelListByUserID(mReceiverID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTQueryChannelsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTQueryChannelsResponse ltQueryChannelsResponse) {
                        Log.i("ChatListPresenter", ltQueryChannelsResponse.toString());
                        getChannels().clear();
                        getChannels().addAll(ltQueryChannelsResponse.getChannels());
                        mView.refreshChatList(getChannels());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ChatListPresenter", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void resume() {
        prepareData();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onItemClick(LTChannelResponse response) {
        ChatMessageFragment fragment = ChatMessageFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_RECEIVER_ID, mReceiverID);
        bundle.putString(EXTRA_CHANNEL_ID, response.getChID());
        bundle.putSerializable(EXTRA_CHANNEL_TYPE, response.getChType());
        mView.changeFragment(fragment, bundle);
        ChatMessagePresenter chatListPresenter = new ChatMessagePresenter(fragment, mReceiverID, response.getChID());
        fragment.setPresenter(chatListPresenter);
    }

    @Override
    public String getReceiverID() {
        return mReceiverID;
    }

    @Override
    public String getAccountID() {
        return mAccountID;
    }
}
