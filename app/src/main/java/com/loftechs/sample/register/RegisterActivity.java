package com.loftechs.sample.register;

import android.os.Bundle;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseAppCompatActivity;

public class RegisterActivity extends BaseAppCompatActivity {
    RegisterFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.string_register);
        setTitleBackButtonVisiable(false);
        if (savedInstanceState == null) {
            fragment = RegisterFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, fragment)
                    .commitNow();
        }
        RegisterPresenter presenter = new RegisterPresenter(fragment);
        fragment.setPresenter(presenter);
    }
}
