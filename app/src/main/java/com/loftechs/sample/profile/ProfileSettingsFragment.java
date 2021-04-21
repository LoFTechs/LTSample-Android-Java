package com.loftechs.sample.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseFragment;
import com.loftechs.sample.model.api.UserProfileManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileSettingsFragment extends BaseFragment implements ProfileSettingsContract.View<ProfileSettingsContract.Presenter>, View.OnClickListener {
    ProfileSettingsContract.Presenter mPresenter;
    EditText mNameEdit;
    SwitchMaterial mMuteSwitch;
    SwitchMaterial mDidplaySwitch;
    SwitchMaterial mContentSwitch;

    public static ProfileSettingsFragment newInstance() {
        return new ProfileSettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void setPresenter(ProfileSettingsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public void initView(View root) {
        mNameEdit = root.findViewById(R.id.name_edittext);
        mMuteSwitch = root.findViewById(R.id.mute_switch);
        mDidplaySwitch = root.findViewById(R.id.display_switch);
        mContentSwitch = root.findViewById(R.id.content_switch);
        root.findViewById(R.id.setting_button).setOnClickListener(ProfileSettingsFragment.this);
        mMuteSwitch.setOnClickListener(ProfileSettingsFragment.this);
        mDidplaySwitch.setOnClickListener(ProfileSettingsFragment.this);
        mContentSwitch.setOnClickListener(ProfileSettingsFragment.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_button:
                mPresenter.updateNickname(mNameEdit.getText().toString());
                break;
            case R.id.mute_switch:
                mPresenter.updateNotifyMute(mMuteSwitch.isChecked());
                break;
            case R.id.display_switch:
            case R.id.content_switch:
                mPresenter.updateNotifyPreview(mDidplaySwitch.isChecked(), mContentSwitch.isChecked());
                break;
        }

    }

    @Override
    public void toastUpdateResult(String returnMsg) {
        showShortToast(returnMsg);
    }

    @Override
    public void setNickname(String nickname) {
        mNameEdit.setText(nickname);
    }

    @Override
    public void setMuteStatus(boolean mute) {
        mMuteSwitch.setChecked(mute);
    }

    @Override
    public void setEnableDisplay(boolean enable) {
        mDidplaySwitch.setChecked(enable);
    }

    @Override
    public void setEnableContent(boolean enable) {
        mContentSwitch.setChecked(enable);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserProfileManager event) {
        mPresenter.initData();
    }
}
