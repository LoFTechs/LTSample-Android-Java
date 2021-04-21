package com.loftechs.sample.model.http;

import android.util.Base64;
import android.util.Log;

import com.loftechs.sample.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenHttpClientHelper extends HttpClientHelper{
    private static String TAG = "TokenHttpClientHelper";

    public TokenHttpClientHelper() {
        init();
    }

    protected void init() {
        super.init();
    }

    @Override
    protected Response setInterceptorAction(Interceptor.Chain chain) throws IOException {
        String authorization = createAuthorization(BuildConfig.Developer_Account, BuildConfig.Developer_Password);
        Request original = chain.request();
        Log.i(TAG, "request url:"+original.url().toString());
        Request request = original.newBuilder()
                .addHeader("User-Agent", "android")
                .addHeader("Authorization", authorization)
                .addHeader("Brand-Id", BuildConfig.Brand_ID)
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .method(original.method(), original.body())
                .build();

        //nonce check
        Response response = chain.proceed(request);
        Log.i(TAG, "response:"+response.code());
        return response;
    }

    @Override
    protected String getBaseUrl() {
        return BuildConfig.Auth_API;
    }

    String createAuthorization(String account, String pwd) {
        String plain = account + ":" + pwd;
        String encoded = Base64.encodeToString(plain.getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }
}
