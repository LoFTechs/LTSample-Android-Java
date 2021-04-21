package com.loftechs.sample.chat;

import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.model.api.ChatFlowManager;
import com.loftechs.sample.model.api.MemberManager;
import com.loftechs.sample.model.api.UserManager;
import com.loftechs.sdk.im.channels.LTChannelPreferenceResponse;
import com.loftechs.sdk.im.channels.LTChannelResponse;
import com.loftechs.sdk.im.channels.LTCreateChannelResponse;
import com.loftechs.sdk.im.channels.LTInviteMemberResponse;
import com.loftechs.sdk.im.message.LTMemberModel;
import com.loftechs.sdk.listener.LTErrorInfo;
import com.loftechs.sdk.user.LTUserStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ChatCreatePresenter implements ChatCreateContract.Presenter {
    static final String TAG = ChatCreatePresenter.class.getSimpleName();
    private ChatCreateContract.View mView;
    private String mReceiverID;
    private String mAccountID;
    List<LTChannelResponse> channels;
    Map<String, LTMemberModel> mLTMemberModelMap;

    public ChatCreatePresenter(ChatCreateContract.View view, String receiverID, String accountID) {
        this.mView = view;
        this.mReceiverID = receiverID;
        mAccountID = accountID;
    }

    private Map<String, LTMemberModel> getLTMemberModelMap() {
        if (mLTMemberModelMap == null) {
            mLTMemberModelMap = new HashMap<>();
        }
        return mLTMemberModelMap;
    }

    @Override
    public void create() {
    }


    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void createGroupChannel(String subject) {
        if (getLTMemberModels() == null) {
            mView.toastUpdateResult("Account has error!");
            return;
        }
        ChatFlowManager.getInstance().createGroupChannel(mReceiverID, subject, getLTMemberModels())
                // set creater channel nickanme.
                .flatMap((Function<LTCreateChannelResponse, ObservableSource<LTChannelPreferenceResponse>>) ltCreateChannelResponse ->
                        ChatFlowManager.getInstance().setChannelNickname(mReceiverID, ltCreateChannelResponse.getChID(), mAccountID))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTChannelPreferenceResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTChannelPreferenceResponse ltChannelPreferenceResponse) {
                        mView.toastUpdateResult("Create Channel Success. (" + ltChannelPreferenceResponse.getChID() + ")");
                        mView.backStack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof LTErrorInfo) {
                            String message = ((LTErrorInfo) e).getErrorMessage();
                            mView.toastUpdateResult("Create Channel fail. " + message);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void createSingleChannel() {
        if (getLTMemberModels() == null) {
            mView.toastUpdateResult("Account has error!");
            return;
        }
        ChatFlowManager.getInstance().createSingleChannel(mReceiverID, new ArrayList<>(getLTMemberModels()).get(0))
                // set creater channel nickanme.
                .flatMap((Function<LTCreateChannelResponse, ObservableSource<LTChannelPreferenceResponse>>) ltCreateChannelResponse ->
                        ChatFlowManager.getInstance().setChannelNickname(mReceiverID, ltCreateChannelResponse.getChID(), mAccountID))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTChannelPreferenceResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTChannelPreferenceResponse ltChannelPreferenceResponse) {
                        mView.toastUpdateResult("Create Channel Success. (" + ltChannelPreferenceResponse.getChID() + ")");
                        mView.backStack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof LTErrorInfo) {
                            String message = ((LTErrorInfo) e).getErrorMessage();
                            mView.toastUpdateResult("Create Channel fail. " + message);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void inviteMember(String userID, String chID) {
        if (getLTMemberModels() == null) {
            mView.toastUpdateResult("Account has error!");
            return;
        }
        MemberManager.getInstance().inviteMember(userID, chID, getLTMemberModels())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTInviteMemberResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTInviteMemberResponse ltInviteMemberResponse) {
                        mView.toastUpdateResult("inviteMember Success.");
                        mView.backStack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("inviteMember error.");
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    @Override
    public void checkAccount(String account) {
        List<String> accountList = new ArrayList<>();
        accountList.add(account);
        UserManager.getInstance().getUserStatusWithSemiUIDs(accountList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LTUserStatus>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<LTUserStatus> ltUserStatuses) {
                        Log.d(TAG, "checkAccount : " + ltUserStatuses.toString());
                        if (ltUserStatuses.isEmpty()) {
                            mView.toastUpdateResult("Account has error");
                        } else {
                            for (LTUserStatus ltUserStatus : ltUserStatuses) {
                                LTMemberModel ltMemberModel = new LTMemberModel(ltUserStatus.getUserID());
                                ltMemberModel.setChNickname(ltUserStatus.getSemiUID());
                                getLTMemberModelMap().put(ltUserStatus.getSemiUID(), ltMemberModel);
                            }
                            mView.toastUpdateResult("Get account success");
                        }
                        mView.setCheckBtnVisiable();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "checkAccount e : " + e.toString());
                        mView.toastUpdateResult("Account has error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void removeAccount(String account) {
        getLTMemberModelMap().remove(account);
    }

    @Override
    public void setNickname(String account, String nickname) {
        if (!getLTMemberModelMap().containsKey(account)
                || Strings.isNullOrEmpty(nickname)) {
            return;
        }
        LTMemberModel ltMemberModel = getLTMemberModelMap().get(account);
        ltMemberModel.setChNickname(nickname);
        getLTMemberModelMap().put(account, ltMemberModel);
    }

    @Override
    public Set<LTMemberModel> getLTMemberModels() {
        if (getLTMemberModelMap().values().isEmpty()) {
            return null;
        }
        Set<LTMemberModel> memberModels = new HashSet<>();
        for (LTMemberModel ltMemberModel : getLTMemberModelMap().values()) {
            memberModels.add(ltMemberModel);
        }
        return memberModels;
    }

    @Override
    public Boolean isVaildAccount(String account) {
        return getLTMemberModelMap().containsKey(account);
    }
}
