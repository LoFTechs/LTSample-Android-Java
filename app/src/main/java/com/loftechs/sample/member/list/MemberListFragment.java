package com.loftechs.sample.member.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.chat.ChatCreateFragment;
import com.loftechs.sample.chat.ChatCreatePresenter;
import com.loftechs.sample.common.event.MemberChangedEvent;
import com.loftechs.sample.component.VerticalRecyclerView;
import com.loftechs.sample.member.MemberListAdapter;
import com.loftechs.sample.model.data.MemberEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_CREATE_TYPE;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class MemberListFragment extends BaseFragment implements MemberListContract.View<MemberListContract.Presenter> {

    private VerticalRecyclerView mRecyclerView;
    private MemberListAdapter mMemberListAdapter;
    private MemberListContract.Presenter mPresenter;
    private String mUserID;
    private String mChID;

    public static MemberListFragment newInstance() {
        return new MemberListFragment();
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
        return inflater.inflate(R.layout.fragment_member_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @javax.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserID = getArguments().getString(EXTRA_RECEIVER_ID);
        mChID = getArguments().getString(EXTRA_CHANNEL_ID);
        initView(view);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.listview);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.string_member_list);
        mMemberListAdapter = null;
        mPresenter.resume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_invite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite:
                ChatCreateFragment chatCreateFragment = ChatCreateFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CREATE_TYPE, ChatCreateFragment.ChatCreateType.Invite);
                bundle.putString(EXTRA_RECEIVER_ID, mUserID);
                bundle.putString(EXTRA_CHANNEL_ID, mChID);
                changeFragment(chatCreateFragment, bundle);
                ChatCreatePresenter chatListPresenter = new ChatCreatePresenter(chatCreateFragment, mUserID, "");
                chatCreateFragment.setPresenter(chatListPresenter);
                break;
        }
        return true;
    }

    @Override
    public void toastUpdateResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void refreshList(List<MemberEntity> members) {
        if (mMemberListAdapter == null) {
            mMemberListAdapter = new MemberListAdapter(getActivity(), mPresenter);
            mMemberListAdapter.setData(members);
            mRecyclerView.setAdapter(mMemberListAdapter);
        } else {
            mMemberListAdapter.setData(members);
            mMemberListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(MemberListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MemberChangedEvent event) {
        if (!event.getChID().equals(mChID)) {
            return;
        }
        showShortToast("MemberChangedEvent");
        mPresenter.refreshData();
    }
}
