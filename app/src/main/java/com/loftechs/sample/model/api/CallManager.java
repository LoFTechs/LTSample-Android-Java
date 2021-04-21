package com.loftechs.sample.model.api;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.LTSDKManager;
import com.loftechs.sample.R;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.call.voice.IncallActivity;
import com.loftechs.sample.model.AccountPreferenceHelper;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sdk.LTNoPermissionException;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.LTSDKNoInitializationException;
import com.loftechs.sdk.call.LTCallOptions;
import com.loftechs.sdk.call.LTCallStatusCode;
import com.loftechs.sdk.call.LTCallType;
import com.loftechs.sdk.call.core.LTCallState;
import com.loftechs.sdk.call.message.LTCallCDRNotificationMessage;
import com.loftechs.sdk.call.message.LTCallNotificationMessage;
import com.loftechs.sdk.user.LTUserStatus;
import com.loftechs.sdk.call.LTCallNotificationListener;
import com.loftechs.sdk.call.LTCallStateListener;
import com.loftechs.sdk.call.LTCallCenterManager;
import com.loftechs.sdk.call.api.LTUserCDRResponse;
import com.loftechs.sdk.call.core.LTMediaType;
import com.loftechs.sdk.call.core.LTCall;
import com.loftechs.sdk.call.route.LTAudioRoute;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;

import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static com.loftechs.sample.common.IntentKey.INCOMINGMODE_KEY;
import static com.loftechs.sample.common.IntentKey.NAME_KEY;

public class CallManager implements LTCallStateListener, LTCallNotificationListener {

    private final String TAG = CallManager.class.getSimpleName();

    private static class LazyHolder {
        private static final CallManager instance = new CallManager();
    }

    public static CallManager getInstance() {
        return LazyHolder.instance;
    }

    @Getter
    LTCall ltCall;
    private UIListener uiListener;
    protected AudioManager audioManager;
    protected boolean callAudioFocus;
    protected MediaPlayer ringerPlayer;
    protected Vibrator vibrator;
    protected boolean isRinging;
    protected LTCallCenterManager ltCallCenterManager;
    private int connectionDuration;
    @Setter
    @Getter
    public String majorCallID;

    AccountEntity accountEntity;

    public CallManager() {
        Log.d(TAG, "CallCenter++");

        audioManager = ((AudioManager) SampleApp.context.getSystemService(Context.AUDIO_SERVICE));
        vibrator = (Vibrator) SampleApp.context.getSystemService(Context.VIBRATOR_SERVICE);
        accountEntity = AccountPreferenceHelper.getInstance().geFirstAccount();
        try {
            ltCallCenterManager = LTSDK.getInstance().getCallCenterManager();
            ltCallCenterManager.setCallNotificationListener(this);
        } catch (LTSDKNoInitializationException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | LTNoPermissionException e) {
            Log.d(TAG, "CallCenter Exception:" + e.getMessage());
        }

        Log.d(TAG, "ltCallCenterManager:" + ltCallCenterManager);

        resetStatus();

        Log.d(TAG, "CallCenter--");
    }


    public Observable<LTCallCenterManager> getLTSDKCallCenterManager() {
        Log.d(TAG, "CallNotificationListener:: get LTCallCenterManagerManager: " + ltCallCenterManager + " NewCallManager: " + this);
        if (ltCallCenterManager != null) {
            if (ltCallCenterManager.getCallNotificationListener() == null) {
                ltCallCenterManager.setCallNotificationListener(CallManager.this);
            }
            return Observable.just(ltCallCenterManager);
        }
        return LTSDKManager.getCallCenterManager()
                .map(new Function<LTCallCenterManager, LTCallCenterManager>() {
                    @Override
                    public LTCallCenterManager apply(LTCallCenterManager callCenterManager) throws Exception {
                        callCenterManager.setCallNotificationListener(CallManager.this);
                        callCenterManager = callCenterManager;
                        return callCenterManager;
                    }
                });
    }


    public void parseFCMCallMessage(String messageJson) {
        Log.d(TAG, "parseFCMCallMessage");
        getLTSDKCallCenterManager()
                .flatMap((Function<LTCallCenterManager, ObservableSource<LTSDK>>) callCenterManager -> LTSDKManager.getLTSDK())
                .subscribe(new Observer<LTSDK>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTSDK ltsdk) {
                        Log.d(TAG, "parseFCMCallMessage messageJson : " + messageJson);
                        ltsdk.parseIncomingPushWithNotify(messageJson);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void doOutgoingCallWithUserID(String account, String userID) {
        Log.d(TAG, "doOutgoingCallWithUserID");
        LTCallOptions callOptions = new LTCallOptions.UserIDBuilder()
                .setUserID(userID)
                .build();
        Log.d(TAG, "start a outgoing call with user " + accountEntity.getUserID());
        Log.d(TAG, "getNumberOfCalls " + ltCallCenterManager.getActiveCallCount());

        getLTSDKCallCenterManager().subscribe(new Observer<LTCallCenterManager>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LTCallCenterManager callCenterManager) {
                NotificationCompat.Builder builder = setNotificationBuilder("", account, false);
                callCenterManager.setAndroidNotification(builder, 1);
                ltCall = callCenterManager.startCallWithUserID(accountEntity.getUserID(), callOptions, CallManager.this);
                if (ltCall != null) {
                    Log.d(TAG, "Account user id  : " + ltCall.getAccountUserID());
                    Log.d(TAG, "start a outgoing call : " + ltCall.getCallID());
                    setMajorCallID(ltCall.getCallID());
                    startIncallActivity(account, false);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getCallCenterManager error: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public void doIncomingCall(LTCallNotificationMessage IncomingCallMessage) {

        Log.d(TAG, "doIncomingCall");

        if (!IncomingCallMessage.isIncomingCallMessage()) {
            Log.e(TAG, "Not IncomigCall Message");
            return;
        }


        getLTSDKCallCenterManager().subscribe(new Observer<LTCallCenterManager>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LTCallCenterManager centerManager) {
                String number = IncomingCallMessage != null ? (!Strings.isNullOrEmpty(IncomingCallMessage.getCallOptions().getSemiUID()) ?
                        IncomingCallMessage.getCallOptions().getSemiUID() : IncomingCallMessage.getCallOptions().getPhoneNumber()) : "";
                if (ltCall == null) {
                    NotificationCompat.Builder builder = setNotificationBuilder("", number, true);
                    centerManager.setAndroidNotification(builder, 1);
                }
                LTCall tempIncomingCall = centerManager.startCallWithNotificationMessage(IncomingCallMessage, CallManager.this);
                if (tempIncomingCall != null) {
                    Log.d(TAG, "getNumberOfCalls: " + centerManager.getActiveCallCount());
                    if (centerManager.getActiveCallCount() >= 2 || ltCall != null) {
                        Log.d(TAG, "busyCall: " + tempIncomingCall.getCallID());
                        tempIncomingCall.busyCall();
                        return;
                    }
                    ltCall = tempIncomingCall;
                    setMajorCallID(ltCall.getCallID());
                    startIncallActivity(number, true);
                    Log.d(TAG, "start a incoming call : " + ltCall.getCallID());
                    Log.d(TAG, "startRinging: ");
                    startRinging();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getCallCenterManager error: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void startIncallActivity(final String phoneNumber, boolean isIncomingCall) {
        Log.i(TAG, "startIncallActivity");
        Intent intent = getIncallIntent(phoneNumber, isIncomingCall);
        SampleApp.context.startActivity(intent);
    }

    @NotNull
    private Intent getIncallIntent(String phoneNumber, boolean isIncomingCall) {
        Intent intent = new Intent();
        intent.setClass(SampleApp.context, IncallActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(NAME_KEY, phoneNumber)
                .putExtra(INCOMINGMODE_KEY, isIncomingCall);
        return intent;
    }

    public LTCallState getCallState() {
        if (ltCall != null) {
            return ltCall.getLTCallState();
        }
        return LTCallState.IDLE;
    }

    public void doReinviteGroupCall() {
        Log.d(TAG, "doReinviteGroupCall");

        if (ltCall != null) {
            if (ltCall.isGroupCall()) {
                ltCall.reinviteGroupCall();
            }
        }
    }


    public void doAccept() {
        Log.d(TAG, "doAccept");

        if (ltCall != null) {
            ltCall.acceptCall();
        }

    }

    public void doHangup() {
        Log.d(TAG, "doHangup");

        if (ltCall != null) {
            ltCall.hangupCall();
        } else {
            Log.e(TAG, "doHangup error");
        }

    }


    public void setCallMuted(boolean mute) {
        if (ltCall != null) {
            ltCall.setCallMuted(mute);
        }
    }

    public void setCallHeld(boolean hold) {
        if (ltCall != null) {
            ltCall.setCallHeld(hold);
        }
    }

    public boolean isCallMuted() {
        return ltCall != null && ltCall.isCallMuted();
    }

    public boolean isCallHeld() {
        return ltCall != null && ltCall.isCallHeld();
    }


    public boolean isSpeakerOn() {
        return ltCall != null && ltCall.getSpeakerStatus();
    }


    public void routeAudioToSpeaker() {
        if (ltCall != null) {
            ltCall.setAudioRoute(LTAudioRoute.LTAudioRouteSpeaker);
        }
    }

    public void routeAudioToReceiver() {
        if (ltCall != null) {
            ltCall.setAudioRoute(LTAudioRoute.LTAudioRouteBuiltin);
        }
    }

    public void routeAudioToBluetooth() {
        if (ltCall != null) {
            ltCall.setAudioRoute(LTAudioRoute.LTAudioRouteBluetooth);
        }
    }

    public Observable<LTUserCDRResponse> getCallLog(long startTime, int count) {
        Log.d(TAG, "doQueryCDR");
        return ltCallCenterManager.queryCDRWithUserID(accountEntity.getUserID(), startTime, count);
    }

    public void doGetUserStatus() {
        Log.d(TAG, "doUserStatus");

        List<String> phoneNumberList = new ArrayList<String>();
        phoneNumberList.add("phoneNumber1");

        try {
            LTSDK.getInstance().getUserStatusWithPhoneNumbers(phoneNumberList)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<List<LTUserStatus>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<LTUserStatus> response) {
                            Log.d(TAG, "doGetUserStatus: " + response.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "requestCDR onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (LTSDKNoInitializationException e) {
            Log.d(TAG, "LTSDKNoInitializationException:" + e.getMessage());
        }


    }

    public void adjustVolume(int i) {
        try {
            if (isRinging) {
                audioManager.adjustStreamVolume(STREAM_RING,
                        i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI);
            } else {
                audioManager.adjustStreamVolume(STREAM_VOICE_CALL,
                        i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI);
            }
        } catch (Exception e) {
            Log.w(TAG, "adjustVolume Exception:" + e.getMessage());
        }

    }


    public void resetStatus() {
        Log.d(TAG, "resetStatus");

        connectionDuration = 0;
        ltCall = null;

    }


    public void setCallStatus(LTCallState status) {
        if (status == LTCallState.TERMINATED) {
            resetStatus();
        }
    }


    protected synchronized void startRinging() {
        try {
            if ((audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) && vibrator != null) {
                long[] pattern = {0, 1000, 1000};
                vibrator.vibrate(pattern, 0);
            }
            if (ringerPlayer == null) {
                ringerPlayer = new MediaPlayer();
                ringerPlayer.setAudioStreamType(STREAM_RING);
                onRingerPlayerCreated(ringerPlayer);
                ringerPlayer.prepare();
                ringerPlayer.setLooping(true);
                ringerPlayer.start();
            } else {
                Log.w(TAG, "already ringing");
            }
        } catch (Exception e) {
            Log.e(TAG, "cannot handle incoming call : " + e.toString());
        }
        isRinging = true;
    }

    private synchronized void stopRinging() {
        if (ringerPlayer != null) {
            ringerPlayer.stop();
            ringerPlayer.release();
            ringerPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }

//        if (Hacks.needGalaxySAudioHack())
//            mAudioManager.setMode(AudioManager.MODE_NORMAL);

        isRinging = false;
        // You may need to call galaxys audio hack after this method
    }

    private void onRingerPlayerCreated(MediaPlayer mRingerPlayer) {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            mRingerPlayer.setDataSource(SampleApp.context, ringtoneUri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "cannot set ringtone");
        }
    }

    public int getConnectDuration() {
        return connectionDuration;
    }

    private NotificationChannel setNotificationChannel() {
        /**
         * Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel or it will not appear.
         * By categorizing notifications into channels, users can disable specific notification channels for your app (instead of disabling all your notifications),
         * and users can control the visual and auditory options for each channel—all from the Android system settings.
         */

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null;
        }

        String appName = SampleApp.context.getApplicationInfo().loadLabel(SampleApp.context.getPackageManager()).toString();
        NotificationChannel channel = new NotificationChannel("chID" + appName + "LP", "chName" + appName + "LP", NotificationManager.IMPORTANCE_LOW);
        return channel;
    }


    private NotificationCompat.Builder setNotificationBuilder(String content, String displayname, boolean isIncomingCall) {
        Log.d(TAG, "scheduleNotificationChannel custom");
        String appName = SampleApp.context.getApplicationInfo().loadLabel(SampleApp.context.getPackageManager()).toString();
        NotificationCompat.Builder builder = null;
        if (Strings.isNullOrEmpty(content)) {
            content = isIncomingCall ? " : Incoming Call" : " : Call Out";
        }
        try {
            Intent activityIntent = getIncallIntent(displayname, isIncomingCall);
            PendingIntent notifContentIntent = PendingIntent.getActivity(
                    LTSDK.getContext(), 0, activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            builder = getNotificationBuilder("chID" + appName + "LP", "chName" + appName + "LP", NotificationManager.IMPORTANCE_LOW);
            builder.setSmallIcon(R.drawable.notif_call)
                    .setAutoCancel(false)
                    .setColor(ContextCompat.getColor(SampleApp.context, R.color.colorDefaultTheme))
                    .setSound(null)
                    .setContentTitle(appName)
                    .setContentText(displayname + content)
                    .setContentIntent(notifContentIntent);

        } catch (Exception exc) {
            Log.d(TAG, "customNotificationChannel error " + exc.toString());
        }

        return builder;
    }


    private NotificationCompat.Builder getNotificationBuilder(String channelID, String channelName, int importance) {
        /**
         * Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel or it will not appear.
         * By categorizing notifications into channels, users can disable specific notification channels for your app (instead of disabling all your notifications),
         * and users can control the visual and auditory options for each channel—all from the Android system settings.
         */
        return new NotificationCompat.Builder(
                LTSDK.getContext(), channelID);
    }

    public static class CallStateBundle {
        @Getter
        public String callID;
        @Getter
        public LTCallType type;
        @Getter
        public LTCallState state;
        @Getter
        public LTCallStatusCode statusCode;

        public CallStateBundle(String callID, LTCallType type, LTCallState state, LTCallStatusCode statusCode) {
            this.callID = callID;
            this.type = type;
            this.state = state;
            this.statusCode = statusCode;
        }

        public CallStateBundle(LTCallType type, LTCallState state, LTCallStatusCode statusCode) {
            this.type = type;
            this.state = state;
            this.statusCode = statusCode;
        }
    }

    public interface UIListener {
        void onUIChange(CallStateBundle bundle);

        void onMediaChange(LTMediaType mediaType);
    }

    public void setUIListener(UIListener listener) {
        uiListener = listener;
    }


    private void uiChange(CallStateBundle callStateBundle) {
        if (uiListener != null) { //ui is ready
            uiListener.onUIChange(callStateBundle);
        }
    }


    private boolean isValidLTCallManager(LTCall call, LTCallStatusCode callStatusCode) {
        if (call == null) {
            Log.d(TAG, "isValidLTCallManager: null ltCall statusCode: " + callStatusCode);
            uiChange(new CallStateBundle("", LTCallType.OUTGOINGCALL, LTCallState.TERMINATED, callStatusCode)); //通知IncallActivity關掉UI
            return false;
        }

        if (Strings.isNullOrEmpty(call.getCallID())) {
            return false;
        }

        if (call.isGroupCall() && Strings.isNullOrEmpty(majorCallID)) {
            setMajorCallID(call.getCallID());
        }

        if (!call.getCallID().equals(majorCallID)) {
            Log.d(TAG, "onLTCallStateChange: not the major call " + majorCallID);
            return false;
        }

        return true;
    }

    @Override
    public void onLTCallStateRegistered(LTCall call) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return;
        }

        Log.d(TAG, "onLTCallStateRegistered callID: " + call.getCallID() + " type: " + call.getCallType().toString());

        if (call.getCallType() == LTCallType.GROUPCALL) { //是否為groupcall發起者
            uiChange(new CallStateBundle(call.getCallID(), LTCallType.GROUPCALL, LTCallState.REGISTERED, LTCallStatusCode.OK));
        } else {
            uiChange(new CallStateBundle(call.getCallID(), call.getCallType(), LTCallState.REGISTERED, LTCallStatusCode.OK));
        }

        Log.d(TAG, "start bluetooth with REGSISTERD");
        call.setAudioRoute(LTAudioRoute.LTAudioRouteBluetooth); // bluetooth優先

    }

    public void onLTCallStateConnected(LTCall call) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return;
        }

        Log.d(TAG, "onLTCallStateConnected callID: " + call.getCallID());

        stopRinging();
        uiChange(new CallStateBundle(call.getCallID(), call.getCallType(), LTCallState.CONNECTED, LTCallStatusCode.OK));


        if (call.getCurrentAudioRoute() != LTAudioRoute.LTAudioRouteSpeaker) {
            Log.d(TAG, "start bluetooth with CONNECTED");
            call.setAudioRoute(LTAudioRoute.LTAudioRouteBluetooth);
        }

        Log.d(TAG, "sendNotification ");
        NotificationCompat.Builder builder = setNotificationBuilder("In Call", "", false);
        ltCallCenterManager.setAndroidNotification(builder, 1);


    }


    @Override
    public void onLTCallStateTerminated(LTCall call, LTCallStatusCode callStatusCode) {
        if (!isValidLTCallManager(call, callStatusCode)) {
            return;
        }


        Log.d(TAG, "onLTCallStateTerminated callID: " + call.getCallID() + " StatusCode: " + callStatusCode.toString());

        uiChange(new CallStateBundle(call.getCallID(), call.getCallType(), LTCallState.TERMINATED, callStatusCode));

        stopRinging();

        //release major call
        ltCall = null;
        setMajorCallID("");
    }


    @Override
    public void onLTCallStateWarning(LTCall warnedCall, LTCallStatusCode callStatusCode) {
        Log.d(TAG, "onLTCallStateWarning : " + warnedCall.getCallID() + " " + callStatusCode.toString());
    }


    @Override
    public void onLTCallMediaStateChanged(LTCall mediaChangedCall, LTMediaType mediaType) {
        Log.d(TAG, "onLTMediaStateChange " + mediaChangedCall.getCallID() + " mediaType: " + mediaType.toString());

        if (mediaType == LTMediaType.AUDIOROUTE) {
            Log.d(TAG, "getCurrentAudioRoute " + mediaChangedCall.getCurrentAudioRoute().toString());
        }

        if (mediaChangedCall != null &&
                !Strings.isNullOrEmpty(mediaChangedCall.getCallID()) &&
                mediaChangedCall.getCallID().equals(majorCallID) &&
                uiListener != null) {
            uiListener.onMediaChange(mediaType);
        }
    }


    @Override
    public void onLTCallConnectionDuration(LTCall call, int duration) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return;
        }

        if (call.getCallID().equals(majorCallID)) {
            connectionDuration = duration;
        }

    }


    @Override
    public void onLTCallNotification(LTCallNotificationMessage callNotificationMessage) {
        doIncomingCall(callNotificationMessage);
    }

    @Override
    public void onLTCallCDRNotification(LTCallCDRNotificationMessage callCDRNotificationMessage) {
        Log.d(TAG, "onLTCallCDRNotification : " + callCDRNotificationMessage.toString());
    }
}