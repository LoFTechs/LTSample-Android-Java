package com.loftechs.sample.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.common.base.Strings;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatSendMessageFragment extends BaseFragment implements ChatSendMessageContract.View<ChatSendMessageContract.Presenter>, View.OnClickListener {

    private ChatSendMessageContract.Presenter mPresenter;
    private String mChID;
    private String mReceiver;
    private EditText mSendTextEditView;
    private Button mSendTextButton;
    private Button mSendImageButton;
    private Button mSendDocumentButton;
    private ProgressBar mProgressBar;

    public static ChatSendMessageFragment newInstance() {
        return new ChatSendMessageFragment();
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
        return inflater.inflate(R.layout.fragment_chatsendmessage, container, false);
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
        initView(view);
        mPresenter.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.string_send_message);
        mPresenter.resume();
    }

    private void initView(View view) {
        mSendTextEditView = view.findViewById(R.id.chat_text_edit);
        mSendTextButton = view.findViewById(R.id.button_send_text);
        mSendImageButton = view.findViewById(R.id.button_send_image);
        mSendDocumentButton = view.findViewById(R.id.button_send_doc);
        mProgressBar = view.findViewById(R.id.loading);
        mSendTextButton.setOnClickListener(this);
        mSendImageButton.setOnClickListener(this);
        mSendDocumentButton.setOnClickListener(this);
    }

    @Override
    public void setPresenter(ChatSendMessageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_text: {
                if (!Strings.isNullOrEmpty(mSendTextEditView.getText().toString())) {
                    mPresenter.sendTextMessage(mSendTextEditView.getText().toString());
                }
            }
            break;
            case R.id.button_send_image: {
                Intent intent = new Intent();
                intent.setType("image/*");//可选择图片视频
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.button_send_doc: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + File.separator + "LTFolder" + File.separator), "*/*");
                startActivityForResult(intent, 2);
            }
            break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mPresenter.sendImageMessage(uri, uri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mPresenter.sendDocumentMessage(uri);
                }
                break;
        }
    }

    @Override
    public void backStack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setProgress(int value) {
        if (mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setProgress(value);
    }
}
