package com.loftechs.sample.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.component.VerticalRecyclerView;
import com.loftechs.sdk.im.channels.LTChannelResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_CREATE_TYPE;

public class ChatListFragment extends BaseFragment implements ChatListContract.View<ChatListContract.Presenter>{

    private VerticalRecyclerView mRecyclerView;
    private ChatListAdapter mChatListAdapter;
    private ChatListContract.Presenter mPresenter;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
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
        return inflater.inflate(R.layout.fragment_chatlist, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @javax.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        mPresenter.create();
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.listview);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.string_chat_list);
        mChatListAdapter = null;
        mPresenter.resume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user:{
                ChatCreateFragment chatCreateFragment = ChatCreateFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CREATE_TYPE, ChatCreateFragment.ChatCreateType.One_to_One);
                changeFragment(chatCreateFragment, bundle);
                ChatCreatePresenter chatListPresenter = new ChatCreatePresenter(chatCreateFragment, mPresenter.getReceiverID(),mPresenter.getAccountID());
                chatCreateFragment.setPresenter(chatListPresenter);
            }
            break;
            case R.id.group: {
                ChatCreateFragment chatCreateFragment = ChatCreateFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CREATE_TYPE, ChatCreateFragment.ChatCreateType.Group);
                changeFragment(chatCreateFragment, bundle);
                ChatCreatePresenter chatListPresenter = new ChatCreatePresenter(chatCreateFragment, mPresenter.getReceiverID(),mPresenter.getAccountID());
                chatCreateFragment.setPresenter(chatListPresenter);
            }
            break;
        }
        ;
        return true;
    }

    @Override
    public void setPresenter(ChatListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void refreshChatList(List<LTChannelResponse> items) {
        if (mChatListAdapter == null) {
            mChatListAdapter = new ChatListAdapter(getActivity(), mPresenter);
            mChatListAdapter.setData(items);
            mRecyclerView.setAdapter(mChatListAdapter);
        } else {
            mChatListAdapter.setData(items);
            mChatListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {

    }
}
