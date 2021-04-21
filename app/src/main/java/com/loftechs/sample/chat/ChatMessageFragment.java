package com.loftechs.sample.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.chat.settings.ChatSettingsFragment;
import com.loftechs.sample.chat.settings.ChatSettingsPresenter;
import com.loftechs.sample.common.event.IncomingMessageEvent;
import com.loftechs.sample.common.event.UpdateMessageEvent;
import com.loftechs.sample.component.VerticalRecyclerView;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.message.LTMessageResponse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_TYPE;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatMessageFragment extends BaseFragment implements ChatMessageContract.View<ChatMessageContract.Presenter>, View.OnClickListener {

    private VerticalRecyclerView mRecyclerView;
    private Button mSendMessageView;
    private ChatMessageAdapter mChatMessageAdapter;
    private ChatMessageContract.Presenter mPresenter;
    private String mChID;
    private LTChannelType mChType;
    private String mReceiver;

    public static ChatMessageFragment newInstance() {
        return new ChatMessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatmessage, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @javax.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mReceiver = getArguments().getString(EXTRA_RECEIVER_ID);
        mChID = getArguments().getString(EXTRA_CHANNEL_ID);
        mChType = (LTChannelType) getArguments().getSerializable(EXTRA_CHANNEL_TYPE);
        initView(view);
        mPresenter.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.string_chat);
        mChatMessageAdapter = null;
        mPresenter.resume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_message, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting: {
                ChatSettingsFragment fragment = ChatSettingsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_RECEIVER_ID, mReceiver);
                bundle.putString(EXTRA_CHANNEL_ID, mChID);
                ChatSettingsPresenter mPresenter = new ChatSettingsPresenter(fragment, mReceiver, mChID);
                fragment.setPresenter(mPresenter);
                changeFragment(fragment, bundle);
            }
        }
        return true;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.listview);
        mSendMessageView = view.findViewById(R.id.button_Send);
        mSendMessageView.setOnClickListener(this);
    }

    @Override
    public void setPresenter(ChatMessageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void refreshMessageList(List<LTMessageResponse> items) {
        if (mChatMessageAdapter == null) {
            mChatMessageAdapter = new ChatMessageAdapter(getActivity(), mPresenter);
            mChatMessageAdapter.setData(items);
            mRecyclerView.setAdapter(mChatMessageAdapter);
        } else {
            mChatMessageAdapter.setData(items);
            mChatMessageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void backStack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Send: {
                ChatSendMessageFragment fragment = ChatSendMessageFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_RECEIVER_ID, mReceiver);
                bundle.putString(EXTRA_CHANNEL_ID, mChID);
                bundle.putSerializable(EXTRA_CHANNEL_TYPE, mChType);
                ChatSendMessagePresenter mPresenter = new ChatSendMessagePresenter(fragment, mReceiver, mChID, mChType);
                fragment.setPresenter(mPresenter);
                changeFragment(fragment, bundle);
            }
            break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(IncomingMessageEvent event) {
        LTMessageResponse response = event.getResponse();
        if (response != null && mReceiver.equals(event.getReceiverID()) && mChID.equals(response.getChID())) {
            if (mChatMessageAdapter != null) {
                mChatMessageAdapter.addDate(response);
                mChatMessageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateMessageEvent event) {
        if (mReceiver.equals(event.getReceiverID()) && mChID.equals(event.getChID())) {
            mPresenter.resume();
        }
    }
}
