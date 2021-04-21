package com.loftechs.sample.receiver;

import android.util.Log;

import com.loftechs.sample.common.event.ChannelChangeEvent;
import com.loftechs.sample.common.event.ChannelCloseEvent;
import com.loftechs.sample.common.event.IncomingMessageEvent;
import com.loftechs.sample.common.event.MemberChangedEvent;
import com.loftechs.sample.common.event.UpdateMessageEvent;
import com.loftechs.sample.common.event.UserProfileChangeEvent;
import com.loftechs.sdk.im.LTIMManagerListener;
import com.loftechs.sdk.im.channels.LTChannelPreferenceResponse;
import com.loftechs.sdk.im.channels.LTChannelProfileResponse;
import com.loftechs.sdk.im.channels.LTChannelUserProfileResponse;
import com.loftechs.sdk.im.channels.LTCreateChannelResponse;
import com.loftechs.sdk.im.channels.LTCreateNewsChannelResponse;
import com.loftechs.sdk.im.channels.LTDismissChannelResponse;
import com.loftechs.sdk.im.channels.LTInviteMemberResponse;
import com.loftechs.sdk.im.channels.LTJoinChannelResponse;
import com.loftechs.sdk.im.channels.LTKickMemberResponse;
import com.loftechs.sdk.im.channels.LTLeaveChannelResponse;
import com.loftechs.sdk.im.channels.LTMemberProfile;
import com.loftechs.sdk.im.channels.LTMemberRoleResponse;
import com.loftechs.sdk.im.channels.LTSetChannelMemberProfileResponse;
import com.loftechs.sdk.im.message.LTCastVoteResponse;
import com.loftechs.sdk.im.message.LTCreateVoteResponse;
import com.loftechs.sdk.im.message.LTDeleteAllMessagesResponse;
import com.loftechs.sdk.im.message.LTDeleteChannelMessageResponse;
import com.loftechs.sdk.im.message.LTDeleteMessagesResponse;
import com.loftechs.sdk.im.message.LTMarkReadNewsResponse;
import com.loftechs.sdk.im.message.LTMarkReadResponse;
import com.loftechs.sdk.im.message.LTMessageResponse;
import com.loftechs.sdk.im.message.LTNewsMessageResponse;
import com.loftechs.sdk.im.message.LTRecallMessagesResponse;
import com.loftechs.sdk.im.message.LTScheduledInDueTimeMessageResponse;
import com.loftechs.sdk.im.message.LTScheduledMessageResponse;
import com.loftechs.sdk.im.message.LTScheduledVoteResponse;
import com.loftechs.sdk.im.message.LTSendMessageResponse;
import com.loftechs.sdk.im.users.LTModifyUserProfileResponse;
import com.loftechs.sdk.im.users.LTSetUserProfileResponse;
import com.loftechs.sdk.listener.LTErrorInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

public class IMReceiver extends LTIMManagerListener {
    @Override
    public void onConnected(String userID) {
        Log.i("IMReceiver", "onConnected" + userID.toString());
    }

    @Override
    public void onDisconnected(String userID) {
        Log.i("IMReceiver", "onDisconnected" + userID.toString());
    }

    @Override
    public void onIncomingJoinChannel(String toUserID, LTJoinChannelResponse joinChannelResponse) {
        Log.i("IMReceiver", "onIncomingAnswerInvitation" + joinChannelResponse.toString());
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, joinChannelResponse));
    }

    @Override
    public void onIncomingCreateChannel(String toUserID, LTCreateChannelResponse createChannelResponse) {
        Log.i("IMReceiver", "onIncomingCreateChannel" + createChannelResponse.toString());
    }

    @Override
    public void onIncomingDismissChannel(String toUserID, LTDismissChannelResponse dismissChannelResponse) {
        Log.i("IMReceiver", "onIncomingDismissChannel" + dismissChannelResponse.toString());
        EventBus.getDefault().post(new ChannelCloseEvent(toUserID, dismissChannelResponse.getChID()));
    }

    @Override
    public void onIncomingInviteMember(String toUserID, LTInviteMemberResponse inviteMemberResponse) {
        Log.i("IMReceiver", "onIncomingInviteMember" + inviteMemberResponse.toString());
        EventBus.getDefault().post(new MemberChangedEvent(toUserID, inviteMemberResponse.getChID()));
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, inviteMemberResponse));
    }

    @Override
    public void onIncomingKickMember(String toUserID, LTKickMemberResponse kickMemberResponse) {
        Log.i("IMReceiver", "onIncomingKickMember" + kickMemberResponse.toString());
        Set<LTMemberProfile> members = kickMemberResponse.getMembers();
        for (LTMemberProfile member : members) {
            if (member.getUserID().equals(toUserID)) {
                EventBus.getDefault().post(new ChannelCloseEvent(toUserID, kickMemberResponse.getChID()));
                return;
            }
        }
        EventBus.getDefault().post(new MemberChangedEvent(toUserID, kickMemberResponse.getChID()));
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, kickMemberResponse));
    }

    @Override
    public void onIncomingLeaveChannel(String toUserID, LTLeaveChannelResponse leaveChannelResponse) {
        Log.i("IMReceiver", "onIncomingLeaveChannel" + leaveChannelResponse.toString());
        Set<LTMemberProfile> members = leaveChannelResponse.getMembers();
        for (LTMemberProfile member : members) {
            if (member.getUserID().equals(toUserID)) {
                EventBus.getDefault().post(new ChannelCloseEvent(toUserID, leaveChannelResponse.getChID()));
                return;
            }
        }
        EventBus.getDefault().post(new MemberChangedEvent(toUserID, leaveChannelResponse.getChID()));
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, leaveChannelResponse));
    }

    @Override
    public void onIncomingChannelPreference(String toUserID, LTChannelPreferenceResponse setChannelPreferenceResponse) {
        Log.i("IMReceiver", "onIncomingSetChannelPreference" + setChannelPreferenceResponse.toString());
        EventBus.getDefault().post(new ChannelChangeEvent(toUserID, setChannelPreferenceResponse.getChID()));
    }

    @Override
    public void onIncomingChannelProfile(String toUserID, LTChannelProfileResponse setChannelProfileResponse) {
        Log.i("IMReceiver", "onIncomingSetChannelProfile" + setChannelProfileResponse.toString());
        EventBus.getDefault().post(new ChannelChangeEvent(toUserID, setChannelProfileResponse.getChID()));
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, setChannelProfileResponse));
//        Observable.just(setChannelProfileResponse)
//                .flatMap((Function<LTChannelProfileResponse, ObservableSource<String>>) ltChannelProfileResponse -> ChatSettingsManager.getInstance().getSubject(ltChannelProfileResponse))
//                .subscribe(subject -> {
//                    ChannelProfileEvent event = new ChannelProfileEvent(toUserID, setChannelProfileResponse.getChID());
//                    event.setSubject(subject);
//                    EventBus.getDefault().post(event);
//                });
    }

    @Override
    public void onIncomingChannelUserProfile(String toUserID, LTChannelUserProfileResponse setChannelProfileResponse) {
        Log.i("IMReceiver", "onIncomingSetChannelUserProfile" + setChannelProfileResponse.toString());
    }

    @Override
    public void onIncomingMemberRole(String toUserID, LTMemberRoleResponse memberRoleResponse) {
        Log.i("IMReceiver", "onIncomingSetMemberRole" + memberRoleResponse.toString());
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, memberRoleResponse));
    }

    @Override
    public void onIncomingSetChannelMemberProfile(String toUserID, LTSetChannelMemberProfileResponse setChannelMemberProfileResponse) {
        Log.i("IMReceiver", "onIncomingSetChannelMemberProfile" + setChannelMemberProfileResponse.toString());
    }

    @Override
    public void onIncomingCreatePublicNewsChannel(String toUserID, LTCreateNewsChannelResponse createPublicNewsChannelResponse) {
        Log.i("IMReceiver", "onIncomingCreatePublicNewsChannel" + createPublicNewsChannelResponse.toString());
    }

    @Override
    public void onIncomingCreateCorpNewsChannel(String toUserID, LTCreateNewsChannelResponse createCorpNewsChannelResponse) {
        Log.i("IMReceiver", "onIncomingCreateCorpNewsChannel" + createCorpNewsChannelResponse.toString());
    }

    @Override
    public void onIncomingMessage(String toUserID, LTMessageResponse messageResponse) {
        Log.i("IMReceiver", "onIncomingMessage" + messageResponse.toString());
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, messageResponse));
    }

    @Override
    public void onIncomingSendMessage(String toUserID, LTSendMessageResponse sendMessageResponse) {
        Log.i("IMReceiver", "onIncomingSendMessage" + sendMessageResponse.toString());
        EventBus.getDefault().post(new IncomingMessageEvent(toUserID, sendMessageResponse));
    }

    @Override
    public void onIncomingScheduledMessage(String toUserID, LTScheduledMessageResponse scheduledMessageResponse) {
        Log.i("IMReceiver", "onIncomingScheduledMessage" + scheduledMessageResponse.toString());
    }

    @Override
    public void onIncomingScheduledVoteResponse(String toUserID, LTScheduledVoteResponse scheduledVoteResponse) {
        Log.i("IMReceiver", "onIncomingScheduledVoteResponse" + scheduledVoteResponse.toString());
    }

    @Override
    public void onIncomingScheduledInDueTimeMessage(String toUserID, LTScheduledInDueTimeMessageResponse scheduledInDueTimeMessageResponse) {
        Log.i("IMReceiver", "onIncomingScheduledInDueTimeMessage" + scheduledInDueTimeMessageResponse.toString());
    }

    @Override
    public void onIncomingMarkRead(String toUserID, LTMarkReadResponse markReadResponse) {
        Log.i("IMReceiver", "onIncomingMarkRead" + markReadResponse.toString());
    }

    @Override
    public void onIncomingMarkReadNews(String toUserID, LTMarkReadNewsResponse markReadNewsResponse) {
        Log.i("IMReceiver", "onIncomingMarkReadNews" + markReadNewsResponse.toString());
    }

    @Override
    public void onIncomingCreateVoteMessage(String toUserID, LTCreateVoteResponse createVoteResponse) {
        Log.i("IMReceiver", "onIncomingCreateVoteMessage" + createVoteResponse.toString());
    }

    @Override
    public void onIncomingCastVoteMessage(String toUserID, LTCastVoteResponse castVoteResponse) {
        Log.i("IMReceiver", "onIncomingCastVoteMessage" + castVoteResponse.toString());
    }

    @Override
    public void onIncomingDeleteAllMessages(String toUserID, LTDeleteAllMessagesResponse deleteAllMessagesResponse) {
        Log.i("IMReceiver", "onIncomingDeleteAllMessages" + deleteAllMessagesResponse.toString());
    }

    @Override
    public void onIncomingDeleteChannelMessage(String toUserID, LTDeleteChannelMessageResponse deleteChannelMessageResponse) {
        Log.i("IMReceiver", "onIncomingDeleteChannelMessage" + deleteChannelMessageResponse.toString());
    }

    @Override
    public void onIncomingDeleteMessages(String toUserID, LTDeleteMessagesResponse deleteMessagesResponse) {
        Log.i("IMReceiver", "onIncomingDeleteMessages" + deleteMessagesResponse.toString());
        EventBus.getDefault().post(new UpdateMessageEvent(toUserID, deleteMessagesResponse.getChID()));
    }

    @Override
    public void onIncomingRecallMessage(String toUserID, LTRecallMessagesResponse recallMessagesResponse) {
        Log.i("IMReceiver", "onIncomingRecallMessage" + recallMessagesResponse.toString());
        EventBus.getDefault().post(new UpdateMessageEvent(toUserID, recallMessagesResponse.getChID()));
    }

    @Override
    public void onIncomingNewsMessage(String toUserID, LTNewsMessageResponse messageResponse) {
        Log.i("IMReceiver", "onIncomingNewsMessage" + messageResponse.toString());
    }

    @Override
    public void onIncomingSetUserProfile(String toUserID, LTSetUserProfileResponse setUserProfileResponse) {
        Log.i("IMReceiver", "onIncomingSetUserProfile" + setUserProfileResponse.toString());
        EventBus.getDefault().post(new UserProfileChangeEvent(toUserID, toUserID));
    }

    @Override
    public void onIncomingModifyUserProfile(String toUserID, LTModifyUserProfileResponse modifyUserProfileResponse) {
        Log.i("IMReceiver", "onIncomingModifyUserProfile" + modifyUserProfileResponse.toString());
        EventBus.getDefault().post(new UserProfileChangeEvent(toUserID, toUserID));
    }

    @Override
    public void onError(LTErrorInfo errorInfo) {
        Log.i("IMReceiver", "onError" + errorInfo.toString());
    }
}
