package com.loftechs.sample.main;

import android.util.Log;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sample.R;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.fcm.FCMTokenHelper;
import com.loftechs.sample.model.AccountPreferenceHelper;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sample.receiver.IMReceiver;
import com.loftechs.sample.model.api.CallManager;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.im.LTIMManager;
import com.loftechs.sdk.listener.LTCallbackResultListener;
import com.loftechs.sdk.listener.LTErrorInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


public class MainViewModel extends ViewModel {
    private static String TAG = MainViewModel.class.getSimpleName();
    private MutableLiveData<ConnectResult> connectResult = new MutableLiveData<>();

    MainViewModel() {
    }

    LiveData<ConnectResult> getConnectResult() {
        return connectResult;
    }

    public void connect(String username) {
        LTSDKManager.getIMManager(username)
                .flatMap((Function<LTIMManager, ObservableSource<Boolean>>) ltimManager -> {
                    ltimManager.setManagerListener(new IMReceiver());
                    return Observable.create((ObservableOnSubscribe<Boolean>) emitter
                            -> ltimManager.connect(new LTCallbackResultListener<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            emitter.onNext(true);
                            emitter.onComplete();
                        }

                        @Override
                        public void onError(LTErrorInfo errorInfo) {
                            emitter.onNext(false);
                            emitter.onComplete();
                        }
                    }));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        FCMTokenHelper.performUpdate();
                        CallManager.getInstance();
                        connectResult.setValue(new ConnectResult(aBoolean, Type.LOGIN));
                    }

                    @Override
                    public void onError(Throwable e) {
                        connectResult.setValue(new ConnectResult(R.string.login_failed));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void logout(String username) {
        // can be launched in a separate asynchronous job
        LTSDKManager.getIMManager(username)
                .flatMap((Function<LTIMManager, ObservableSource<Boolean>>) ltimManager -> Observable.create((ObservableOnSubscribe<Boolean>) emitter
                        -> ltimManager.disconnect(new LTCallbackResultListener<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        emitter.onNext(true);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(LTErrorInfo errorInfo) {
                        emitter.onNext(false);
                        emitter.onComplete();
                    }
                })))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.d(TAG, "logoff status : " + aBoolean);
                        resetSDK();
                    }

                    @Override
                    public void onError(Throwable e) {
                        resetSDK();
                        Log.e(TAG, "logoff error e : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public boolean isExistAccount(String account) {
        return AccountPreferenceHelper.getInstance().isExistAccount(account);
    }

    public AccountEntity getAccountEntity(String account) {
        if (isExistAccount(account)) {
            return AccountPreferenceHelper.getInstance().getAccountEntities().get(account);
        }
        return null;
    }

    public AccountEntity getAccountEntity() {
        return AccountPreferenceHelper.getInstance().geFirstAccount();
    }

    public void resetSDK() {
        LTSDK.clean(SampleApp.context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        connectResult.setValue(new ConnectResult(aBoolean, Type.LOGOUT));
                        Log.d(TAG, "resetSDK status : " + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        connectResult.setValue(new ConnectResult(false, Type.LOGOUT));
                        Log.e(TAG, "resetSDK error e : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
