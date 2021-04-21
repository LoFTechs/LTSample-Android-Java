package com.loftechs.sample.chat;

import android.os.Bundle;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseAppCompatActivity;

import static com.loftechs.sample.common.IntentKey.EXTRA_ACCOUNT_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class ChatActivity extends BaseAppCompatActivity {
    ChatListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(R.string.string_chat_list);
        if (savedInstanceState == null) {
            fragment = ChatListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
        }
        String receiverID = this.getIntent().getStringExtra(EXTRA_RECEIVER_ID);
        String accountID = this.getIntent().getStringExtra(EXTRA_ACCOUNT_ID);
        ChatListPresenter chatListPresenter = new ChatListPresenter(fragment, receiverID,accountID);
        fragment.setPresenter(chatListPresenter);
    }
}
