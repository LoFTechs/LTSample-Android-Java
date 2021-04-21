package com.loftechs.sample.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.chat.ChatCreateFragment.ChatCreateType.Group;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_CREATE_TYPE;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatCreateFragment extends BaseFragment implements ChatCreateContract.View<ChatCreateContract.Presenter>
        , View.OnClickListener {

    public enum ChatCreateType {
        One_to_One,
        Group,
        Invite
    }

    private ChatCreateContract.Presenter mPresenter;
    private ChatCreateType mType;
    private String mUserID;
    private String mChID;

    private EditText subjectEdit;
    private EditText user1UserIDEdit;
    private EditText user2UserIDEdit;
    private EditText user1NicknameEdit;
    private EditText user2NicknameEdit;
    private Button check1AccountBtn;
    private Button check2AccountBtn;
    private ProgressBar loading1;
    private ProgressBar loading2;
    private RelativeLayout relativeLayoutAccount2;
    private TextInputLayout subjectInputLayout;
    private TextInputLayout nicknameInputLayout;


    public static ChatCreateFragment newInstance() {
        return new ChatCreateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatcreate, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @javax.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.string_chat_create);
        mType = (ChatCreateType) getArguments().getSerializable(EXTRA_CREATE_TYPE);
        mUserID = getArguments().getString(EXTRA_RECEIVER_ID);
        mChID = getArguments().getString(EXTRA_CHANNEL_ID);
        if (mType == ChatCreateType.Invite) {
            getActivity().setTitle(R.string.member_invite_member);
        } else {
            getActivity().setTitle(R.string.string_chat_create);
        }
        initView(view);
        mPresenter.create();
    }

    private void initView(View view) {
        subjectEdit = view.findViewById(R.id.chat_create_subject_edit);
        subjectInputLayout = view.findViewById(R.id.subjectTextField);
        user1UserIDEdit = view.findViewById(R.id.user1_user_id_edittext);
        user2UserIDEdit = view.findViewById(R.id.user2_user_id_edittext);
        user1NicknameEdit = view.findViewById(R.id.user1_nickname_edittext);
        user2NicknameEdit = view.findViewById(R.id.user2_nickname_edittext);
        check1AccountBtn = view.findViewById(R.id.check1_button);
        check2AccountBtn = view.findViewById(R.id.check2_button);
        relativeLayoutAccount2 = view.findViewById(R.id.account2_layout);
        nicknameInputLayout = view.findViewById(R.id.nickname2TextField4);
        loading1 = view.findViewById(R.id.loading1);
        loading2 = view.findViewById(R.id.loading2);
        user1UserIDEdit.addTextChangedListener(account1Watcher);
        user2UserIDEdit.addTextChangedListener(account2Watcher);
        check1AccountBtn.setOnClickListener(this);
        check2AccountBtn.setOnClickListener(this);
        if (mType == ChatCreateType.One_to_One || mType == ChatCreateType.Invite) {
            subjectInputLayout.setVisibility(View.GONE);
            relativeLayoutAccount2.setVisibility(View.GONE);
            nicknameInputLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setPresenter(ChatCreateContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create: {
                String subject = subjectEdit.getText().toString();
                mPresenter.setNickname(user1UserIDEdit.getText().toString(), user1NicknameEdit.getText().toString());
                switch (mType) {
                    case Group:
                        mPresenter.setNickname(user2UserIDEdit.getText().toString(), user2NicknameEdit.getText().toString());
                        mPresenter.createGroupChannel(subject);
                        break;
                    case Invite:
                        mPresenter.inviteMember(mUserID, mChID);
                        break;
                    case One_to_One:
                        mPresenter.createSingleChannel();
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public void toastUpdateResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void backStack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setCheckBtnVisiable() {
        check1AccountBtn.setVisibility(mPresenter.isVaildAccount(user1UserIDEdit.getText().toString()) ? View.GONE : View.VISIBLE);
        loading1.setVisibility(View.GONE);
        if (mType == Group) {
            check2AccountBtn.setVisibility(mPresenter.isVaildAccount(user2UserIDEdit.getText().toString()) ? View.GONE : View.VISIBLE);
            loading2.setVisibility(View.GONE);
        }
        checkOptionMenuVisiable();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check1_button:
                loading1.setVisibility(View.VISIBLE);
                check1AccountBtn.setVisibility(View.GONE);
                mPresenter.checkAccount(user1UserIDEdit.getText().toString());
                break;
            case R.id.check2_button:
                loading2.setVisibility(View.VISIBLE);
                check2AccountBtn.setVisibility(View.GONE);
                mPresenter.checkAccount(user2UserIDEdit.getText().toString());
                break;
        }
    }

    private final TextWatcher account1Watcher = new TextWatcher() {
        String orgText;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            orgText = s.toString();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            check1AccountBtn.setVisibility(View.VISIBLE);
            mPresenter.removeAccount(orgText);
            checkOptionMenuVisiable();
        }

    };

    private final TextWatcher account2Watcher = new TextWatcher() {
        String orgText;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            orgText = s.toString();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            check2AccountBtn.setVisibility(View.VISIBLE);
            mPresenter.removeAccount(orgText);
            checkOptionMenuVisiable();
        }

    };

    public void checkOptionMenuVisiable() {
        if (mPresenter.getLTMemberModels() != null
                && !mPresenter.getLTMemberModels().isEmpty()) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }
}
