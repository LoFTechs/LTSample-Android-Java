package com.loftechs.sample.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loftechs.sample.BuildConfig;
import com.loftechs.sample.R;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.base.BaseAppCompatActivity;
import com.loftechs.sample.call.CallActivity;
import com.loftechs.sample.chat.ChatActivity;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sample.profile.ProfileSettingsActivity;
import com.loftechs.sample.register.RegisterActivity;
import com.loftechs.sample.utils.VersionUtil;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.LTSDKOptions;

import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.loftechs.sample.common.IntentKey.EXTRA_ACCOUNT_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private MainViewModel mainViewModel;
    private String mAccountID;
    private AccountEntity mAccountEntity;
    private Button mProfileBtn;
    private Button mIMBtn;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitleBackButtonVisiable(false);
        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory())
                .get(MainViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAccountID = bundle.getString(EXTRA_ACCOUNT_ID);
                mAccountEntity = mainViewModel.getAccountEntity(mAccountID);
            }
        }
        if (mAccountEntity == null) {
            mAccountEntity = mainViewModel.getAccountEntity();
            mAccountID = (mAccountEntity != null) ? mAccountEntity.getAccount() : "";
        }
        if (!mainViewModel.isExistAccount(mAccountID)) {
            startActivity(RegisterActivity.class);
            finish();
            return;
        }

        LTSDKOptions options = LTSDKOptions.builder()
                .context(SampleApp.context)
                .licenseKey(BuildConfig.License_Key)
                .url(BuildConfig.Auth_API)
                .userID(mAccountEntity.getUserID())
                .uuid(mAccountEntity.getUuid())
                .build();
        LTSDK.init(options).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.i("CHECK", "SampleApp init:" + aBoolean);
                mainViewModel.connect(mAccountEntity.getUserID());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("CHECK", "onError init:" + e.getMessage());
                // ReturnCode: 6000 ErrorMessage: Not current user
                if (e.getMessage().contains("6000")) {
                    mainViewModel.resetSDK();
                }
            }

            @Override
            public void onComplete() {

            }
        });

        TextView accountTextView = findViewById(R.id.account_textview);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        TextView statusTextView = findViewById(R.id.status_textview);
        TextView versionTextView = findViewById(R.id.version_textview);
        findViewById(R.id.button_call).setOnClickListener(this);
        mProfileBtn = findViewById(R.id.button_profile);
        mProfileBtn.setOnClickListener(this);
//        Button buttonSendFcmTOken = findViewById(R.id.button_send_fcm_token);
        mIMBtn = findViewById(R.id.button_im);
        mIMBtn.setOnClickListener(this);
        findViewById(R.id.button_log_out).setOnClickListener(this);
//        buttonSendFcmTOken.setOnClickListener(this);
        accountTextView.setText(getString(R.string.string_account) + " : " + mAccountEntity.getAccount());
        versionTextView.setText(VersionUtil.getSDKVersionCode());
        mainViewModel.getConnectResult().observe(this, connectResult -> {
            if (connectResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (connectResult.getType() == Type.LOGIN) {
                if (connectResult.getError() != null) {
                    setFunctionBtnEnable(false);
                    statusTextView.setText(getString(connectResult.getError()) + " (" + mAccountEntity.getUserID() + ")");
                } else if (connectResult.getSuccess() != null && connectResult.getSuccess()) {
                    if (connectResult.getType() == Type.LOGIN) {
                        setFunctionBtnEnable(true);
                        statusTextView.setText(getString(R.string.string_status) + " : " + getString(R.string.string_connect) + " (" + mAccountEntity.getUserID() + ")");
//                    buttonSendFcmTOken.setVisibility(View.VISIBLE);
                    }
                } else {
                    setFunctionBtnEnable(false);
                    statusTextView.setText(getString(R.string.string_disconnect) + " (" + mAccountEntity.getUserID() + ")");
                }

//                    buttonSendFcmTOken.setVisibility(View.VISIBLE);
            } else {
                setFunctionBtnEnable(false);
                new AlertDialog.Builder(this)
                        .setTitle(this.getText(R.string.string_logout))
                        .setMessage(this.getText(R.string.error_clear_accunt))
                        .setPositiveButton(R.string.string_yes, (dialog, which) -> {
                            // Clear user data here
                            ((ActivityManager) SampleApp.context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .create()
                        .show();
            }
            setResult(Activity.RESULT_OK);
        });
    }

    private void setFunctionBtnEnable(boolean enable) {
        mProfileBtn.setEnabled(enable);
        mIMBtn.setEnabled(enable);
    }

    public static void restartApp(Context context) {
        // not work after clearApplicationUserData ><
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_RECEIVER_ID, mAccountEntity.getUserID());
        bundle.putString(EXTRA_ACCOUNT_ID, mAccountEntity.getAccount());
        switch (view.getId()) {
            case R.id.button_call:
                startActivity(CallActivity.class, bundle);
                break;
            case R.id.button_im:
                startActivity(ChatActivity.class, bundle);
                break;
            case R.id.button_profile:
                startActivity(ProfileSettingsActivity.class, bundle);
                break;
            case R.id.button_log_out:
                mainViewModel.logout(mAccountEntity.getUserID());
                break;
        }
    }

}
