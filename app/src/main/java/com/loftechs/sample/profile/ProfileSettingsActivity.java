package com.loftechs.sample.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseAppCompatActivity;
import com.loftechs.sample.utils.ActivityUtil;

import androidx.annotation.Nullable;

import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;
import static com.loftechs.sample.profile.ProfileSettingsFragment.newInstance;

public class ProfileSettingsActivity extends BaseAppCompatActivity {
    static String TAG = ProfileSettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.string_profile);
        Log.d(TAG, "onCreate");
        String receiverID = "";
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                receiverID = bundle.getString(EXTRA_RECEIVER_ID);
            }
        }
        ProfileSettingsFragment fragment = (ProfileSettingsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (fragment == null) {
            fragment = newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.contentFrame);
        }
        ProfileSettingsPresenter mPresenter = new ProfileSettingsPresenter(fragment, receiverID);
        fragment.setPresenter(mPresenter);
    }


}
