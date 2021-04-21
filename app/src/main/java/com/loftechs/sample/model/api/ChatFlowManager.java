package com.loftechs.sample.model.api;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.im.channels.LTChannelPreferenceResponse;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.channels.LTCreateChannelResponse;
import com.loftechs.sdk.im.message.LTMemberModel;
import com.loftechs.sdk.im.queries.LTQueryChannelsResponse;
import com.loftechs.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class ChatFlowManager {
    private static class LazyHolder {
        private static final ChatFlowManager instance = new ChatFlowManager();
    }

    public static ChatFlowManager getInstance() {
        return LazyHolder.instance;
    }

    public Observable<LTQueryChannelsResponse> queryChannelListByUserID(String receiverID) {
        ArrayList<LTChannelType> channelTypeList = new ArrayList<>();
        channelTypeList.add(LTChannelType.GROUP);
        channelTypeList.add(LTChannelType.SINGLE);

        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTQueryChannelsResponse>>) imManager
                        -> imManager.getChannelHelper().queryChannelList(Utils.createTransId(),
                        channelTypeList, true, 30));
    }

    public Observable<LTCreateChannelResponse> createGroupChannel(String receiverID, String subject, Set<LTMemberModel> memberModels) {
        String chID = Utils.createTransId();
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTCreateChannelResponse>>) imManager
                        -> imManager.getChannelHelper().createGroupChannel(Utils.createTransId(),
                        chID, subject, memberModels));
    }

    public Observable<LTCreateChannelResponse> createSingleChannel(String receiverID, LTMemberModel memberModel) {
        String chID = Utils.createTransId();
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTCreateChannelResponse>>) imManager
                        -> imManager.getChannelHelper().createSingleChannel(Utils.createTransId(),
                        memberModel));
    }

    public Observable<LTChannelPreferenceResponse> setChannelNickname(String receiverID, String chID, String nickname) {
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTChannelPreferenceResponse>>) imManager
                        -> imManager.getChannelHelper().setChannelUserNickname(Utils.createTransId(), chID, nickname));
    }
}
