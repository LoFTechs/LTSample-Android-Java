package com.loftechs.sample.call.voice;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loftechs.sample.R;
import com.loftechs.sample.model.api.CallManager;
import com.loftechs.sample.call.SoftVolumeActivity;
import com.loftechs.sample.component.IncallTimer;
import com.loftechs.sample.component.MuteMicButton;
import com.loftechs.sample.component.PauseButton;
import com.loftechs.sample.component.SpeakerButton;
import com.loftechs.sdk.call.LTCallStatusCode;
import com.loftechs.sdk.call.LTCallType;
import com.loftechs.sdk.call.core.LTCallState;
import com.loftechs.sdk.call.core.LTMediaType;
import com.loftechs.sdk.utils.ScheduleWorker;

import io.reactivex.disposables.CompositeDisposable;

import static com.loftechs.sample.common.IntentKey.INCOMINGMODE_KEY;
import static com.loftechs.sample.common.IntentKey.NAME_KEY;


public class IncallActivity extends SoftVolumeActivity implements OnClickListener, SensorEventListener, CallManager.UIListener {
    private final static String TAG = "IncallActivity";

    private static final String SCREEN_IS_HIDDEN = "screen_is_hidden";

    private SensorManager mSensorManager;
    private Sensor mProximitySensor;

    private LinearLayout mIncallView;
    private LinearLayout mIncomingCallView;

    private ImageButton hangButton;
    private SpeakerButton mSpeakerButton;
    private MuteMicButton mMuteButton;
    private PauseButton mPauseButton;
    private TextView mPhoneNumberView;
    private IncallTimer elapsedTime;

    private String mPhoneNumber;
    private boolean incomingMode = false;

    private Animation mShowDialPadAnim;
    private Animation mHideDialPadAnim;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.incall);


        mContainer = (FrameLayout) findViewById(R.id.incall_layout);
        mIncomingCallView = (LinearLayout) findViewById(R.id.incoming_call_main);
        mIncallView = (LinearLayout) findViewById(R.id.incall_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        hangButton = (ImageButton) findViewById(R.id.incallHang);
        hangButton.setOnClickListener(this);

        mSpeakerButton = (SpeakerButton) findViewById(R.id.speaker_button);
        mMuteButton = (MuteMicButton) findViewById(R.id.incall_mute_button);
        mPauseButton = (PauseButton) findViewById(R.id.incall_pause_button);
        mPauseButton.setChecked(false);
        mPauseButton.setEnabled(false);
        mPauseButton.setOnClickListener(this);

//		if (!PreferenceManager.getDefaultSharedPreferences(this)
//				.getBoolean(getString(R.string.pref_video_enable_key), false)) {
//			findViewById(R.id.AddVideo).setVisibility(View.GONE);
//		}

        mPhoneNumberView = (TextView) findViewById(R.id.incallPhoneNumber);

        elapsedTime = (IncallTimer) findViewById(R.id.incallElapsedTime);


        createPartialWakeLock();
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(SCREEN_IS_HIDDEN, false)) {
                hideScreen(true);
            }
        }
        setupIncomingCallView();
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void onClick(View v) {
        if (v == hangButton) {
            hangUp();
        } else if (v == mPauseButton) {
            if (mPauseButton.isPauseOn()) {
                elapsedTime.setText(this.getResources().getString(R.string.incall_pause));
            }
        }
    }

    private void hangUp() {
        CallManager.getInstance().doHangup();
        endCall();

    }


    // TODO: should we do something at onNewIntent?
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        if (intent != null) {
            setIntent(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");


        showUIWhenScreenIsLocked();
        mSubscriptions = new CompositeDisposable();


        LTCallState callState = CallManager.getInstance().getCallState();
        if (CallManager.getInstance().getLtCall() == null) {
            Log.d(TAG, "No Existed Call ");
            endCall();
            return;
        }

        final Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra(NAME_KEY);
        incomingMode = intent.getBooleanExtra(INCOMINGMODE_KEY, false);

        if (incomingMode) {
            preStartIncomingModel(callState);
        } else {
            preStartOutgoingModel(callState);
        }
        CallManager.getInstance().setUIListener(this);

        if (mPhoneNumber == null) {
            mPhoneNumber = "";
        }

        getDurationObservable();
        final String displayNumber = mPhoneNumber;

        mPhoneNumberView.setText(displayNumber);

        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        startBluetoothObservable();

        if (CallManager.getInstance().isSpeakerOn()) {
            mSpeakerButton.setSpeakerOn(true);
        } else {
            mSpeakerButton.setSpeakerOn(false);
        }

        if (CallManager.getInstance().isCallMuted()) {
            mMuteButton.setChecked(true);
        } else {
            mMuteButton.setChecked(false);
        }
        if (CallManager.getInstance().isCallHeld()) {
            updatePauseState();
            mPauseButton.setPauseOn(true);
        } else {
            mPauseButton.setPauseOn(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopBluetoothObservable();
        stopDurationObservable();
        CallManager.getInstance().setUIListener(null);
        clearSubscriptions();
        mSensorManager.unregisterListener(this, mProximitySensor);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent()
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME));
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }


    @Override
    public void onUIChange(final CallManager.CallStateBundle bundle) {
        ScheduleWorker.getInstance().getMainThread().schedule(new Runnable() {
            @Override
            public void run() {
                updateUIByCallState(bundle);
            }
        });

    }

    @Override
    public void onMediaChange(LTMediaType mediaType) {
        ScheduleWorker.getInstance().getMainThread().schedule(new Runnable() {
            @Override
            public void run() {
                updateMedia(mediaType);
            }
        });

    }


    private void updateMedia(LTMediaType mediaType) {

        switch (mediaType) {
            case CALLHELD:
                if (CallManager.getInstance().isCallHeld()) {
                    updatePauseState();
                    mPauseButton.setPauseOn(true);
                } else {
                    mPauseButton.setPauseOn(false);
                }
                break;
            case CALLMUTED:
                if (CallManager.getInstance().isCallMuted()) {
                    mMuteButton.setChecked(true);
                } else {
                    mMuteButton.setChecked(false);
                }
                break;
            case AUDIOROUTE:
                if (CallManager.getInstance().isSpeakerOn()) {
                    mSpeakerButton.setSpeakerOn(true);
                } else {
                    mSpeakerButton.setSpeakerOn(false);
                }
                break;
            default:
                break;
        }
    }


    private void updatePauseState() {
        elapsedTime.setTextColor(Color.GRAY);
        elapsedTime.setText(this.getResources().getString(R.string.incall_pause));
    }

    Runnable runnableStartBluetooth;

    private void startBluetoothObservable() {
        runnableStartBluetooth = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "start bluetooth when IncallActivity onResume() ");
                CallManager.getInstance().routeAudioToBluetooth();
            }
        };
        handler.postDelayed(runnableStartBluetooth, 500);
    }

    private void stopBluetoothObservable() {
        if (runnableStartBluetooth != null) {
            handler.removeCallbacks(runnableStartBluetooth);
        }
    }


    Handler handler = new Handler();
    Runnable runnable;

    private void getDurationObservable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int secs = CallManager.getInstance().getConnectDuration();
                if (secs != 0 && !mPauseButton.isPauseOn()) {
                    elapsedTime.setTextColor(Color.GRAY);
                    elapsedTime.setText(elapsedTime.formatTime(secs));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void stopDurationObservable() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    void hideScreen(boolean isHidden) {
        WindowManager.LayoutParams lAttrs = getWindow().getAttributes();
        if (isHidden) {
            lAttrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            mContainer.setVisibility(View.INVISIBLE);
        } else {
            lAttrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mContainer.setVisibility(View.VISIBLE);
        }
        getWindow().setAttributes(lAttrs);
    }


    private void preStartIncomingModel(LTCallState callState) {
        CallManager.CallStateBundle bundle = new CallManager.CallStateBundle(LTCallType.INCOMINGCALL, callState, LTCallStatusCode.OK);
        updateUIByCallState(bundle);
    }

    private void preStartOutgoingModel(LTCallState callState) {
        CallManager.CallStateBundle bundle = new CallManager.CallStateBundle(LTCallType.OUTGOINGCALL, callState, LTCallStatusCode.OK);
        updateUIByCallState(bundle);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.timestamp == 0) return;
        hideScreen(isProximitySensorNearby(event));
    }


    private Boolean isProximitySensorNearby(final SensorEvent event) {
        float threshold = 4.001f; // <= 4 cm is near

        final float distanceInCm = event.values[0];
        final float maxDistance = event.sensor.getMaximumRange();
        Log.d(TAG, "Proximity sensor report [" + distanceInCm
                + "] , for max range [" + maxDistance + "]");

        if (maxDistance <= threshold) {
            // Case binary 0/1 and short sensors
            threshold = maxDistance;
        }

        return distanceInCm < threshold;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showUIWhenScreenIsLocked() {
        // TODO: according to
        // http://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_SHOW_WHEN_LOCKED,
        // FLAG_SHOW_WHEN_LOCKED can be used with FLAG_KEEP_SCREEN_ON to
        // turn screen on and display windows directly before showing the
        // key guard window. But it seems not work!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    private CompositeDisposable mSubscriptions;

    private void updateUIByCallState(CallManager.CallStateBundle bundle) {
        final String callID = bundle.getCallID();
        final LTCallType type = bundle.getType();
        final LTCallState state = bundle.getState();
        final LTCallStatusCode statusCode = bundle.getStatusCode();

        if (callID != null) {
            Log.d(TAG, String.format("updateUIByCallState onNext -> callID=%s type=%s state=%s, statusCode=%s", callID, type.toString(), state.toString(), statusCode.toString()));
        } else {
            Log.d(TAG, String.format("updateUIByCallState onNext -> type=%s state=%s, statusCode=%s", type.toString(), state.toString(), statusCode.toString()));
        }

        if (incomingMode) {
            uiIncomingCallHandle(bundle);
        } else {
            uiOutgoingCallHandle(bundle);
        }


    }


    private void uiOutgoingCallHandle(CallManager.CallStateBundle bundle) {
        final LTCallState state = bundle.getState();
        final LTCallStatusCode statusCode = bundle.getStatusCode();

        switch (state) {
            case IDLE:
            case INIT:
                Log.d(TAG, "uiOutgoingCallhandle : INIT");
                elapsedTime.setText(R.string.msg_waiting_for_connection);
            case REGISTERED:
                Log.d(TAG, "uiOutgoingCallhandle : SETUPCALL");
                elapsedTime.setText(R.string.msg_incall_calling);

                mIncallView.setVisibility(View.VISIBLE);
                mIncomingCallViewModel.hide();
                elapsedTime.setTextColor(Color.RED);
                elapsedTime.setText(R.string.msg_incall_calling);
                mPauseButton.setEnabled(false);
                mPauseButton.setChecked(false);
                break;
            case CONNECTED:
                Log.d(TAG, "uiOutgoingCallhandle : INCALL");
                uiINCALLHandle();
                break;
            case TERMINATED:
                Log.d(TAG, "uiOutgoingCallhandle : ENDCALL");
                endCall();
                break;
            default:
                break;

        }

    }


    private void uiIncomingCallHandle(CallManager.CallStateBundle bundle) {
        final LTCallState state = bundle.getState();
        final LTCallStatusCode statusCode = bundle.getStatusCode();

        switch (state) {
            case INIT:
            case REGISTERED:
                mIncomingCallViewModel
                        .show()
                        .setPhoneNumber(mPhoneNumber)
                        .setStatus("");


                elapsedTime.setText(R.string.msg_waiting_for_connection);
                mIncallView.setVisibility(View.GONE);
                return;
            case CONNECTED:
                uiINCALLHandle();
                break;
            case TERMINATED:
                endCall();
                break;
            default:
                break;

        }

    }

    private void uiINCALLHandle() {
        mIncomingCallViewModel.hide();

        mIncallView.setVisibility(View.VISIBLE);
        mPauseButton.setEnabled(true);
    }

    private void endCall() {
        finish();
    }

    private void clearSubscriptions() {
        mSubscriptions.clear();
        mSubscriptions.dispose();
    }

    private IncomingCallViewModel mIncomingCallViewModel;

    private void setupIncomingCallView() {
        mIncomingCallViewModel = IncomingCallViewModel.init(mIncomingCallView);
    }


    private PowerManager.WakeLock wakeLock;

    private void createPartialWakeLock() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            return;
        }
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_DIM_WAKE_LOCK,
                "Loftechs IncomingCall by Gcm");
        wakeLock.acquire();

    }


}
