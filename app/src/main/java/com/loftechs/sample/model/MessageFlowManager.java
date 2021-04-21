package com.loftechs.sample.model;

import android.net.Uri;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.message.LTDeleteMessagesResponse;
import com.loftechs.sdk.im.message.LTDocumentMessage;
import com.loftechs.sdk.im.message.LTFileMessageResponse;
import com.loftechs.sdk.im.message.LTImageMessage;
import com.loftechs.sdk.im.message.LTMessage;
import com.loftechs.sdk.im.message.LTRecallMessagesResponse;
import com.loftechs.sdk.im.message.LTSendMessageResponse;
import com.loftechs.sdk.im.message.LTTextMessage;
import com.loftechs.sdk.im.queries.LTQueryMessageResponse;
import com.loftechs.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class MessageFlowManager {
    private static class LazyHolder {
        private static final MessageFlowManager instance = new MessageFlowManager();
    }
    
    public static MessageFlowManager getInstance() {
        return LazyHolder.instance;
    }

    public Observable<LTQueryMessageResponse> sendQueryMessage(String receiverID, String chID,
                                                               long startTS, int afterN) {
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTQueryMessageResponse>>) imManager
                        -> imManager.getMessageHelper().queryMessage(Utils.createTransId(),
                        chID, startTS, afterN));
    }

    public Observable<LTSendMessageResponse> sendTextMessage(String receiverID, String chID, LTChannelType chType, String message) {
        LTTextMessage textMessage = LTTextMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .msgContent(message)
                .build();
        return sendMessage(receiverID, textMessage);
    }

    public Observable<LTFileMessageResponse> sendImageMessage(String receiverID, String chID, LTChannelType chType, Uri uri, Uri thUri, String displayFileName) {
        LTImageMessage imageMessage = LTImageMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .imageUri(uri)
                .thumbnailUri(thUri)
                .displayFileName(displayFileName)
                .build();
        return sendMessage(receiverID, imageMessage);
    }

    public Observable<LTFileMessageResponse> sendDocumentMessage(String receiverID, String chID, LTChannelType chType, Uri uri, String displayFileName) {
        LTDocumentMessage documentMessage = LTDocumentMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .fileUri(uri)
                .displayFileName(displayFileName)
                .build();
        return sendMessage(receiverID, documentMessage);
    }

    private <T extends LTSendMessageResponse> Observable<T> sendMessage(String receiverID, LTMessage message) {
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<T>>) imManager
                        -> imManager.getMessageHelper().sendMessage(message));
    }

    public Observable<LTDeleteMessagesResponse> deleteMessage(String receiverID, String msgID) {
        List<String> msgIDs = new ArrayList<>();
        msgIDs.add(msgID);
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTDeleteMessagesResponse>>) imManager
                        -> imManager.getMessageHelper().deleteMessages(Utils.createTransId(),
                        msgIDs));
    }

    public Observable<LTRecallMessagesResponse> recallMessage(String receiverID, String msgID) {
        List<String> msgIDs = new ArrayList<>();
        msgIDs.add(msgID);
        boolean silentMode = false;
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTRecallMessagesResponse>>) imManager
                        -> imManager.getMessageHelper().recallMessages(Utils.createTransId(),
                        msgIDs, silentMode));
    }
}
