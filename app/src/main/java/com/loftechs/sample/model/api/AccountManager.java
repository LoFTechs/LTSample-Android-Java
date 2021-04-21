package com.loftechs.sample.model.api;

import android.util.Base64;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.loftechs.sample.BuildConfig;
import com.loftechs.sample.model.PreferencesSetting;
import com.loftechs.sample.model.http.AuthHttpClientHelper;
import com.loftechs.sample.model.http.TokenHttpClientHelper;
import com.loftechs.sample.model.http.entity.LoginRequest;
import com.loftechs.sample.model.http.entity.LoginResponse;
import com.loftechs.sample.model.http.entity.RegisterRequest;
import com.loftechs.sample.model.http.entity.RegisterResponse;
import com.loftechs.sample.model.http.entity.TokenRequest;
import com.loftechs.sample.model.http.entity.TokenResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class AccountManager {

    TokenHttpClientHelper tokenHttpClientHelper;
    AuthHttpClientHelper authHttpClientHelper;
    private final Long ACCESS_TIME = 600000L;


    private static class LazyHolder {
        private static final AccountManager instance = new AccountManager();
    }

    public static AccountManager getInstance() {
        return LazyHolder.instance;
    }

    private TokenHttpClientHelper getTokenHttpClientHelper() {
        if (tokenHttpClientHelper == null) {
            return new TokenHttpClientHelper();
        }
        return tokenHttpClientHelper;
    }

    private AuthHttpClientHelper getAuthHttpClientHelper() {
        if (authHttpClientHelper == null) {
            return new AuthHttpClientHelper();
        }
        return authHttpClientHelper;
    }

    private Observable<String> getToken() {
        String token = PreferencesSetting.getInstance().getToken();
        if (!Strings.isNullOrEmpty(token)) {
            return getTokenTime(token)
                    .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
                        if (aLong == null || aLong == 0L || (System.currentTimeMillis() > (aLong - ACCESS_TIME))) {
                            return getTokenByServer();
                        }
                        return Observable.just(token);
                    });
        }
        return getTokenByServer();

    }

    private Observable<String> getTokenByServer() {
        return getTokenHttpClientHelper().getService().getAccessToken(new TokenRequest())
                .map(new Function<TokenResponse, String>() {
                    @Override
                    public String apply(@NonNull TokenResponse tokenResponse) throws Exception {
                        PreferencesSetting.getInstance().setToken(tokenResponse.getAccessToken());
                        return tokenResponse.getAccessToken();
                    }
                });
    }

    private Observable<Long> getTokenTime(String token) {
        return Observable.create(emitter -> {
            Long exp = null;
            String[] split_string = token.split("\\.");
            if (split_string.length > 2) {
                String base64EncodedBody = split_string[1].replace("-", "+").replace("_", "/");
                String tokenBody = new String(Base64.decode(base64EncodedBody, Base64.NO_WRAP), "UTF-8");
                Gson gson = new Gson();
                JWTObject jwtObject = gson.fromJson(tokenBody, JWTObject.class);
                exp = jwtObject.exp * 1000;
            }
            emitter.onNext(exp);
            emitter.onComplete();
        });
    }

    private static class JWTObject {
        String id;
        String jti;
        String iss;
        String aud;
        String sub;
        Long exp;
        Long iat;
        String token_type;
        String scope;
        String deviceID;
    }

    public Observable<RegisterResponse> register(String account, String pwd) {

        return getToken()
                .flatMap((Function<String, ObservableSource<RegisterResponse>>) s -> {
                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setTurnkey(BuildConfig.LTSDK_TurnKey);
                    RegisterRequest.User user = new RegisterRequest.User();
                    user.setSemiUID(account);
                    user.setSemiUID_PW(pwd);
                    ArrayList<RegisterRequest.User> users = new ArrayList<>();
                    users.add(user);
                    registerRequest.setUsers(users);
                    return getAuthHttpClientHelper().getService().register(registerRequest);
                });
    }

    public Observable<LoginResponse> login(String account, String pwd) {
        return getToken()
                .flatMap((Function<String, ObservableSource<LoginResponse>>) s -> {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setSemiUID(account);
                    loginRequest.setSemiUID_PW(pwd);
                    return getAuthHttpClientHelper().getService().login(loginRequest);
                });

    }
}
