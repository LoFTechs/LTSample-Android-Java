package com.loftechs.sample.call;

import android.os.Bundle;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseAppCompatActivity;

public class CallActivity extends BaseAppCompatActivity {
    CallFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.string_call);
        if (savedInstanceState == null) {
            fragment = CallFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, fragment)
                    .commitNow();
        }
        CallPresenter presenter = new CallPresenter(fragment);
        fragment.setPresenter(presenter);
    }
}
