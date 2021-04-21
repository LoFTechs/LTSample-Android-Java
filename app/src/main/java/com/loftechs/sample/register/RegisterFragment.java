package com.loftechs.sample.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.main.MainActivity;
import com.loftechs.sample.utils.VersionUtil;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_ACCOUNT_ID;

public class RegisterFragment extends BaseFragment implements RegisterContract.View<RegisterContract.Presenter>, View.OnClickListener {
    RegisterContract.Presenter mPresenter;
    EditText mAccountEdit;
    EditText mPasswordEdit;
    Button mRegisterButton;
    Button mLoginButton;
    TextView mStatusTxt;
    ProgressBar mLoadingProgressBar;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
        getActivity().setTitle(R.string.string_register);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public void initView(View root) {
        mAccountEdit = root.findViewById(R.id.account_edittext);
        mPasswordEdit = root.findViewById(R.id.password_edittext);
        mRegisterButton = root.findViewById(R.id.register_button);
        mLoginButton = root.findViewById(R.id.login_button);
        mStatusTxt = root.findViewById(R.id.status_textview);
        TextView versionTxt = root.findViewById(R.id.version_textview);
        versionTxt.setText(VersionUtil.getSDKVersionCode());
        mLoadingProgressBar = root.findViewById(R.id.loading);
        mRegisterButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        mStatusTxt.setText("");
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.register_button:

                mPresenter.register(mAccountEdit.getText().toString(), mPasswordEdit.getText().toString());
                break;
            case R.id.login_button:
                mPresenter.login(mAccountEdit.getText().toString(), mPasswordEdit.getText().toString());
                break;
        }
    }

    @Override
    public void toastResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void setStatus(String status) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mStatusTxt.setText(status);
    }

    @Override
    public void startMainActivity(String account) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ACCOUNT_ID, account);
        Intent intent = new Intent();
        intent.setClass(getContext(), MainActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
