package com.loftechs.sample.chat;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.model.MessageFlowManager;
import com.loftechs.sdk.im.channels.LTChannelProfileResponse;
import com.loftechs.sdk.im.channels.LTChannelRole;
import com.loftechs.sdk.im.channels.LTCreateChannelResponse;
import com.loftechs.sdk.im.channels.LTInviteMemberResponse;
import com.loftechs.sdk.im.channels.LTJoinChannelResponse;
import com.loftechs.sdk.im.channels.LTKickMemberResponse;
import com.loftechs.sdk.im.channels.LTLeaveChannelResponse;
import com.loftechs.sdk.im.channels.LTMemberProfile;
import com.loftechs.sdk.im.channels.LTMemberRoleResponse;
import com.loftechs.sdk.im.message.LTDeleteMessagesResponse;
import com.loftechs.sdk.im.message.LTFileMessage;
import com.loftechs.sdk.im.message.LTMessage;
import com.loftechs.sdk.im.message.LTMessageResponse;
import com.loftechs.sdk.im.message.LTRecallMessagesResponse;
import com.loftechs.sdk.im.message.LTSendMessageResponse;
import com.loftechs.sdk.im.message.LTTextMessage;
import com.loftechs.sdk.im.queries.LTQueryMessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatMessagePresenter implements ChatMessageContract.Presenter {

    private ChatMessageContract.View mView;
    private String mReceiverID;
    private String mChID;

    public ChatMessagePresenter(ChatMessageContract.View view, String receiverID, String chID) {
        this.mView = view;
        this.mReceiverID = receiverID;
        this.mChID = chID;
    }

    @Override
    public void create() {
    }


    @Override
    public void resume() {
        prepareData();
    }

    private void prepareData() {
        MessageFlowManager.getInstance()
                .sendQueryMessage(mReceiverID, mChID, System.currentTimeMillis(), -20)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTQueryMessageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTQueryMessageResponse ltQueryMessageResponse) {
                        Log.i("TAG", ltQueryMessageResponse.toString());
                        List<LTMessageResponse> messages = ltQueryMessageResponse.getMessages();
                        mView.refreshMessageList(messages);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onItemClick(LTMessageResponse response) {

    }

    @Override
    public String getReceiverID() {
        return null;
    }

    @Override
    public String getSender(LTMessageResponse response) {
        return Strings.isNullOrEmpty(response.getSenderNickname()) ? response.getSenderID() : response.getSenderNickname();
    }

    @Override
    public String getType(LTMessageResponse response) {
        if (response.getRecallStatus() != null) {
            if (response instanceof LTRecallMessagesResponse) {
                return "Recall a message.";
            } else {
                return "This message is recalled.";
            }

        } else if (response instanceof LTJoinChannelResponse) {
            return "Joined this chat.";
        } else if (response instanceof LTCreateChannelResponse) {
            return "Create this chat.";
        } else if (response instanceof LTInviteMemberResponse) {
            return "Invite";
        } else if (response instanceof LTLeaveChannelResponse) {
            return "Left.";
        } else if (response instanceof LTKickMemberResponse) {
            return "Kick";
        } else if (response instanceof LTMemberRoleResponse) {
            return "Set member role.";
        } else if (response instanceof LTChannelProfileResponse) {
            return "Set this chat profile.";
        } else if (response instanceof LTSendMessageResponse) {
            LTSendMessageResponse sendMessageResponse = (LTSendMessageResponse) response;
            LTMessage message = sendMessageResponse.getMessage();
            return String.format("Sent a %s.", message.getClass().getName().replace("com.loftechs.sdk.im.message.", ""));
        } else if (response instanceof LTDeleteMessagesResponse) {
            return "Delete message.";
        } else {
            return String.format("%s.", response.getClass().getName().replace("com.loftechs.sdk.im.message.", ""));
        }
    }

    public String detailWithMembers(Set<LTMemberProfile> ltMemberProfiles) {
        if (ltMemberProfiles == null) {
            return "";
        }
        List<String> members = new ArrayList<>();
        for (LTMemberProfile ltMemberProfile : ltMemberProfiles) {
            if (!Strings.isNullOrEmpty(ltMemberProfile.getNickname())) {
                members.add(ltMemberProfile.getNickname());
            } else {
                members.add(ltMemberProfile.getUserID());
            }
        }
        return TextUtils.join(",", members);
    }

    @Override
    public String getContent(LTMessageResponse response) {
        if (response.getRecallStatus() != null) {
            if (response instanceof LTRecallMessagesResponse) {
                LTRecallMessagesResponse recallMessagesResponse = (LTRecallMessagesResponse) response;
                return String.format("RecallMsgID is %s", recallMessagesResponse.getRecallMsgID());
            } else {
                return String.format("By %s.", response.getRecallStatus().getRecallBy());
            }

        } else if (response instanceof LTCreateChannelResponse) {
            LTCreateChannelResponse createChannelResponse = (LTCreateChannelResponse) response;
            return detailWithMembers(createChannelResponse.getMembers());
        } else if (response instanceof LTInviteMemberResponse) {
            LTInviteMemberResponse inviteMemberResponse = (LTInviteMemberResponse) response;
            return detailWithMembers(inviteMemberResponse.getMembers());
        } else if (response instanceof LTKickMemberResponse) {
            LTKickMemberResponse kickMemberResponse = (LTKickMemberResponse) response;
            return detailWithMembers(kickMemberResponse.getMembers());
        } else if (response instanceof LTMemberRoleResponse) {
            LTMemberRoleResponse memberRoleResponse = (LTMemberRoleResponse) response;
            LTChannelRole roleID = memberRoleResponse.getRoleID();
            String roleString = "";
            if (roleID == LTChannelRole.OUTCAST) {
                roleString = "Outcast";
            } else if (roleID == LTChannelRole.INVITED) {
                roleString = "Invieted";
            } else if (roleID == LTChannelRole.PARTICIPANT) {
                roleString = "Participant";
            } else if (roleID == LTChannelRole.MODERATOR) {
                roleString = "Moderator";
            } else if (roleID == LTChannelRole.ADMIN) {
                roleString = "Admin";
            }
            return String.format("Set %s  role is %s", memberRoleResponse.getMemberUserID(), roleString);
        } else if (response instanceof LTChannelProfileResponse) {
            LTChannelProfileResponse channelProfileResponse = (LTChannelProfileResponse) response;
            Set<String> keys = channelProfileResponse.getChannelProfile().keySet();
            if (keys != null && !keys.isEmpty()) {
                return String.format("Change %s.", TextUtils.join(",", keys));
            }
        } else if (response instanceof LTSendMessageResponse) {
            LTSendMessageResponse sendMessageResponse = (LTSendMessageResponse) response;
            LTMessage message = sendMessageResponse.getMessage();
            if (message instanceof LTTextMessage) {
                LTTextMessage textMessage = (LTTextMessage) message;
                return textMessage.getMsgContent();
            } else if (message instanceof LTFileMessage) {
                LTFileMessage fileMessage = (LTFileMessage) message;
                return fileMessage.getDisplayFileName();
            }

        } else if (response instanceof LTDeleteMessagesResponse) {
            LTDeleteMessagesResponse deleteMessagesResponse = (LTDeleteMessagesResponse) response;
            return deleteMessagesResponse.getDeleteMsgID();
        }
        return "";
    }

    @Override
    public boolean getRecall(LTMessageResponse response) {
        if (response instanceof LTDeleteMessagesResponse || response.getRecallStatus() != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean getDelete(LTMessageResponse response) {
        if (response instanceof LTDeleteMessagesResponse || response.getRecallStatus() != null) {
            return false;
        }
        return true;
    }

    @Override
    public void recallMessage(String msgID) {
        MessageFlowManager.getInstance()
                .recallMessage(mReceiverID, msgID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTRecallMessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTRecallMessagesResponse ltRecallMessagesResponse) {
                        Log.i("TAG", ltRecallMessagesResponse.toString());
                        prepareData();
                        mView.showShortToast("Recall success: "+ltRecallMessagesResponse.getMsgContent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showShortToast("Recall fail: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void deleteMessage(String msgID) {
        MessageFlowManager.getInstance()
                .deleteMessage(mReceiverID, msgID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTDeleteMessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTDeleteMessagesResponse ltDeleteMessagesResponse) {
                        Log.i("TAG", ltDeleteMessagesResponse.toString());
                        prepareData();
                        mView.showShortToast("Delete success: "+ltDeleteMessagesResponse.getMsgContent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showShortToast("Delete fail: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
