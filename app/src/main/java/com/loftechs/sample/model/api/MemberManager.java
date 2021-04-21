package com.loftechs.sample.model.api;

import android.util.Log;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sample.model.data.MemberEntity;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.im.channels.LTInviteMemberResponse;
import com.loftechs.sdk.im.channels.LTJoinMethod;
import com.loftechs.sdk.im.channels.LTKickMemberResponse;
import com.loftechs.sdk.im.channels.LTMemberPrivilege;
import com.loftechs.sdk.im.message.LTMemberModel;
import com.loftechs.sdk.im.queries.LTQueryChannelMembersResponse;
import com.loftechs.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MemberManager {
    static final String TAG = MemberManager.class.getSimpleName();
    static final int BATCH_COUNT = 20;

    private static class LazyHolder {
        private static final MemberManager instance = new MemberManager();
    }

    public static MemberManager getInstance() {
        return LazyHolder.instance;
    }

    /**
     * Loftechs SDK LTChannelHelper queryChannelMembersByChID
     */
    public Observable<LTQueryChannelMembersResponse> queryChannelMembersByChID(String userID, String chID, String lastUserID, int count) {
        return LTSDKManager.getIMManager(userID)
                .flatMap((Function<LTIMManager, ObservableSource<LTQueryChannelMembersResponse>>) ltimManager
                        -> ltimManager.getChannelHelper().queryChannelMembersByChID(Utils.createTransId(), chID, lastUserID, count));
    }


    /**
     * query all members by batch
     */
    public Observable<ArrayList<MemberEntity>> queryAllChannelMembers(String userID, String chID) {
        final String[] lastUserID = {""};

        return Observable
                .defer((Callable<ObservableSource<LTQueryChannelMembersResponse>>) ()
                        -> queryChannelMembersByChID(userID, chID, lastUserID[0], BATCH_COUNT))
                .filter(queryChannelMembersResult -> queryChannelMembersResult.getMembers() != null
                        && queryChannelMembersResult.getMembers().size() > 0)
                .flatMapIterable((Function<LTQueryChannelMembersResponse, Iterable<LTMemberPrivilege>>)
                        LTQueryChannelMembersResponse::getMembers)
                .map(memberResponse -> {
                    MemberEntity memberEntity = new MemberEntity(memberResponse.getUserID(), memberResponse.getNickname(), memberResponse.getPhoneNumber());
                    return memberEntity;
                })
                .collect(ArrayList::new, (BiConsumer<ArrayList<MemberEntity>, MemberEntity>) ArrayList::add)
                .toObservable()
                .doOnNext(new Consumer<ArrayList<MemberEntity>>() {
                    @Override
                    public void accept(ArrayList<MemberEntity> channelMemberEntities) throws Exception {
                        // 若是最後一批，就把lastUserID 壓為 null
                        if (channelMemberEntities.isEmpty()) {
                            lastUserID[0] = null;
                            return;
                        }
                        MemberEntity lastMemberEntity = channelMemberEntities.get(channelMemberEntities.size() - 1);
                        lastUserID[0] = (channelMemberEntities.size() == BATCH_COUNT) ? lastMemberEntity.getUserID() : null;
                    }
                })
                .repeatUntil(() -> lastUserID[0] == null)
                .subscribeOn(Schedulers.newThread())
                .doOnError(throwable -> Log.e(TAG, userID + " queryAllChannelMembers onError ++ " + throwable.toString()))
                .doOnComplete(() -> {
                    Log.d(TAG, userID + " queryAllChannelMembers onComplete");
                });
    }

    public Observable<LTKickMemberResponse> kickMembers(String receiverID, String chID, Set<String> kickMembersUserIDs) {
        return Observable.fromIterable(kickMembersUserIDs)
                .concatMap((Function<String, ObservableSource<LTMemberModel>>) userID -> {
                    LTMemberModel memberModel = new LTMemberModel(userID);
                    return Observable.just(memberModel);
                })
                .collect(HashSet::new, (BiConsumer<HashSet<LTMemberModel>, LTMemberModel>) HashSet::add)
                .toObservable()
                .concatMap((Function<HashSet<LTMemberModel>, ObservableSource<LTKickMemberResponse>>) ltMemberModels ->
                        LTSDKManager.getIMManager(receiverID)
                                .concatMap((Function<LTIMManager, ObservableSource<LTKickMemberResponse>>) ltimManager ->
                                        ltimManager.getChannelHelper().kickMembers(Utils.createTransId(), chID, ltMemberModels)))
                .doOnError(throwable -> Log.e(TAG, receiverID + " kickMembers onError ++ " + throwable.toString()));
    }


    public Observable<LTInviteMemberResponse> inviteMember(String receiverID, String chID, Set<LTMemberModel> ltMemberModels) {
        return LTSDKManager.getIMManager(receiverID)
                .flatMap((Function<LTIMManager, ObservableSource<LTInviteMemberResponse>>) imManager
                        -> imManager.getChannelHelper().inviteMembers(Utils.createTransId(), chID, ltMemberModels, LTJoinMethod.NORMAL))
                .doOnError(throwable -> Log.e(TAG, receiverID + " inviteMember onError ++ " + throwable.toString()));
    }
}
