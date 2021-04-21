package com.loftechs.sample.chat.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.common.event.ChannelChangeEvent;
import com.loftechs.sample.member.list.MemberListFragment;
import com.loftechs.sample.member.list.MemberListPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatSettingsFragment extends BaseFragment implements ChatSettingsContract.View<ChatSettingsContract.Presenter>, View.OnClickListener {
    ChatSettingsContract.Presenter mPresenter;
    EditText mSubjectEdit;
    SwitchMaterial mMuteSwitch;
    Button mSubjectSetButton;
    Button mMembersButton;
    Button mDismissButton;
    Button mLeaveButton;
    String mUserID;
    String mChID;

    public static ChatSettingsFragment newInstance() {
        return new ChatSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chatsettings, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserID = getArguments().getString(EXTRA_RECEIVER_ID);
        mChID = getArguments().getString(EXTRA_CHANNEL_ID);
        mPresenter.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
        getActivity().setTitle(R.string.chat_settings);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void setPresenter(ChatSettingsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public void initView(View root) {
        mSubjectEdit = root.findViewById(R.id.subject_edittext);
        mMuteSwitch = root.findViewById(R.id.mute_switch);
        mSubjectSetButton = root.findViewById(R.id.subject_setting_button);
        mMembersButton = root.findViewById(R.id.members_button);
        mDismissButton = root.findViewById(R.id.dismiss_button);
        mLeaveButton = root.findViewById(R.id.leave_button);
        mSubjectSetButton.setOnClickListener(this);
        mMuteSwitch.setOnClickListener(this);
        mMembersButton.setOnClickListener(this);
        mDismissButton.setOnClickListener(this);
        mLeaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.subject_setting_button:
                mPresenter.updateChannelSubject(mSubjectEdit.getText().toString());
                break;
//            case R.id.nickname_setting_button:
//                break;
            case R.id.mute_switch:
                mPresenter.updateNotifyMute(mMuteSwitch.isChecked());
                break;
            case R.id.members_button:
                MemberListFragment fragment = MemberListFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_RECEIVER_ID, mUserID);
                bundle.putString(EXTRA_CHANNEL_ID, mChID);
                MemberListPresenter memberPresenter = new MemberListPresenter(fragment, mUserID, mChID);
                fragment.setPresenter(memberPresenter);
                changeFragment(fragment, bundle);
                break;
            case R.id.dismiss_button:
                mPresenter.dismissChannel();
                break;
            case R.id.leave_button:
                mPresenter.leaveChannel();
                break;
        }
    }

    @Override
    public void toastUpdateResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void setSubject(String subject) {
        mSubjectEdit.setText(subject);
    }

    @Override
    public void enableGroupUI(boolean enable) {
        int visibility = enable ? View.VISIBLE : View.INVISIBLE;
        mMembersButton.setVisibility(visibility);
        mDismissButton.setVisibility(visibility);
        mLeaveButton.setVisibility(visibility);
    }

    @Override
    public void setNickname(String nickname) {
//        mNickmameEdit.setText(nickname);
    }

    @Override
    public void setMemberCount(int count) {
        mMembersButton.setText(getResources().getString(R.string.chat_settings_member_list)
                .concat("(" + String.valueOf(count) + ")"));
    }

    @Override
    public void setMuteStatus(boolean mute) {
        mMuteSwitch.setChecked(mute);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChannelChangeEvent event) {
        if (mUserID.equals(event.getReceiverID()) && mChID.equals(event.getChID())) {
            mPresenter.initData();
        }
    }
}
