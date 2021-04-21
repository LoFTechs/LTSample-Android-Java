package com.loftechs.sample.register;

import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.BuildConfig;
import com.loftechs.sample.LTSDKManager;
import com.loftechs.sample.R;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.model.AccountPreferenceHelper;
import com.loftechs.sample.model.api.AccountManager;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sample.model.http.entity.LoginResponse;
import com.loftechs.sample.model.http.entity.RegisterResponse;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.LTSDKNoInitializationException;
import com.loftechs.sdk.LTSDKOptions;
import com.loftechs.sdk.user.LTUser;
import com.loftechs.sdk.user.LTUsers;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegisterPresenter implements RegisterContract.Presenter {
    static final String TAG = RegisterPresenter.class.getSimpleName();
    final RegisterContract.View<?> mView;

    public RegisterPresenter(@NonNull RegisterContract.View view) {
        mView = checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void create() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }


    @Override
    public void login(String account, String password) {
        AccountManager.getInstance().login(account, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull LoginResponse loginResponse) {
                        Log.d(TAG, "login loginResponse : " + loginResponse.toString());
                        if (loginResponse.getReturnCode().equals("0")) {
                            init(new AccountEntity(account, password, loginResponse.getUserID(), loginResponse.getUuid()));
                        } else {
                            parseErrorMsg(loginResponse.getReturnCode());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "login e : " + e.toString());
                        parseErrorMsg("6");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void register(String account, String password) {
        AccountManager.getInstance().register(account, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull RegisterResponse registerResponse) {
                        Log.d(TAG, "register registerResponse : " + registerResponse.toString());
                        List<RegisterResponse.User> users = registerResponse.getUsers();
                        // 範列帶一個帳號
                        if (users != null) {
                            RegisterResponse.User user = users.get(0);
                            if (!Strings.isNullOrEmpty(user.getErr())) {
                                parseErrorMsg(user.getErr());
                                return;
                            }
                            init(new AccountEntity(account, password, user.getUserID(), user.getUuid()));
//                            initLTSDK(new AccountEntity(account, password, user.getUserID(), user.getUuid()))
//                                    .subscribe();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "register e : " + e.toString());
                        parseErrorMsg("6");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void checkAccount(ArrayList<String> accounts) {

    }

    private Observable<List<LTUser>> initLTSDK(AccountEntity accountEntity) {
        return LTSDKManager.getLTSDK().flatMap((Function<LTSDK, ObservableSource<List<LTUser>>>) ltsdk -> ltsdk.getUsers()
                .map(ltUsers -> {
                    if (ltUsers != null && ltUsers.getUsers() != null) {
                        AccountPreferenceHelper.getInstance().setAccountEntity(accountEntity);
                        mView.setStatus("Success");
                        mView.startMainActivity(accountEntity.getAccount());
                        return ltUsers.getUsers();
                    }
                    return null;
                }));
    }

    private void parseErrorMsg(String error) {
        String errorMsg = "";
        switch (error) {
            case "6":
                errorMsg = String.format(SampleApp.context.getString(R.string.error_input), error);
                break;
            case "601":
                errorMsg = String.format(SampleApp.context.getString(R.string.error_password), error);
                break;
            case "602":
                errorMsg = String.format(SampleApp.context.getString(R.string.error_account_no_exist), error);
                break;
            case "603":
                errorMsg = String.format(SampleApp.context.getString(R.string.error_account_is_already), error);
                break;
            case "604":
                errorMsg = String.format(SampleApp.context.getString(R.string.error_need_password), error);
                break;
        }
        mView.setStatus(errorMsg);
    }

    private void init(AccountEntity accountEntity) {
        LTSDKOptions options = LTSDKOptions.builder()
                .context(SampleApp.context)
                .licenseKey(BuildConfig.License_Key)
                .url(BuildConfig.Auth_API)
                .userID(accountEntity.getUserID())
                .uuid(accountEntity.getUuid())
                .build();
        LTSDK.init(options).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.i(TAG, "initSDK :" + aBoolean);
                wLogin(accountEntity);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError init :" + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void wLogin(AccountEntity accountEntity) {
        try {
            LTSDK.getInstance().getUsers()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<LTUsers>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LTUsers ltUsers) {
                            Log.d(TAG, "ltUsers:" + ltUsers);
                            AccountPreferenceHelper.getInstance().setAccountEntity(accountEntity);
                            mView.setStatus("Success");
                            mView.startMainActivity(accountEntity.getAccount());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "getUsers onError:" + e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (LTSDKNoInitializationException e) {
            e.printStackTrace();
        }


    }
}
