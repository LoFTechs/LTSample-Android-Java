package com.loftechs.sample.call;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class CallFragment extends BaseFragment implements CallContract.View<CallContract.Presenter>, View.OnClickListener {
    CallContract.Presenter mPresenter;
    EditText mAccountEdit;
    Button mCheckButton;
    Button mCallButton;
    Button mCallLogButton;
    TextView mCallLogTxt;
    ProgressBar mLoadingProgressBar;
    ProgressBar mCallLogLoadingProgressBar;

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_outgoing_call, container, false);
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
        getActivity().setTitle(R.string.string_call);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void setPresenter(CallContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public void initView(View root) {
        mAccountEdit = root.findViewById(R.id.account_edittext);
        mCheckButton = root.findViewById(R.id.check_button);
        mCallButton = root.findViewById(R.id.call_button);
        mCallLogButton = root.findViewById(R.id.cdr_button);
        mCallLogTxt = root.findViewById(R.id.cdr_textview);
        mLoadingProgressBar = root.findViewById(R.id.loading);
        mCallLogLoadingProgressBar = root.findViewById(R.id.cdr_loading);
        mCheckButton.setOnClickListener(this);
        mCallButton.setOnClickListener(this);
        mCallLogButton.setOnClickListener(this);
        mCallButton.setEnabled(false);
        mAccountEdit.addTextChangedListener(accountWatcher);
    }

    private final TextWatcher accountWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setCallFunctionVisiable(false);
        }

    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_button:
                setCallFunctionVisiable(true);
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                mPresenter.checkAccount(mAccountEdit.getText().toString());
                break;
            case R.id.call_button:
                mPresenter.outgoingCall();
                break;
            case R.id.cdr_button:
                mCallLogLoadingProgressBar.setVisibility(View.VISIBLE);
                mPresenter.getCallLog();
                break;
        }
    }

    @Override
    public void toastResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void setCallLog(String callLog) {
        mCallLogLoadingProgressBar.setVisibility(View.GONE);
        mCallLogTxt.setText(callLog);
    }

    @Override
    public void setCallFunctionVisiable(boolean visiable) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mCheckButton.setVisibility(visiable ? View.GONE : View.VISIBLE);
        mCallButton.setEnabled(visiable);
    }
}
