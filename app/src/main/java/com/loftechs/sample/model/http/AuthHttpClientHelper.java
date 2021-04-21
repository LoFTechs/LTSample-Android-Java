package com.loftechs.sample.model.http;

import android.util.Log;

import com.loftechs.sample.BuildConfig;
import com.loftechs.sample.model.PreferencesSetting;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthHttpClientHelper extends HttpClientHelper {
    private static String TAG = "AuthHttpClientHelper";

    public AuthHttpClientHelper() {
        init();
    }

    protected void init() {
        super.init();
    }

    @Override
    protected Response setInterceptorAction(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Log.i(TAG, "request url:" + original.url().toString());
        String authorization = "Bearer " + PreferencesSetting.getInstance().getToken();
        String command = original.headers().get("Command");
        Request request = original.newBuilder()
                .addHeader("User-Agent", "android")
                .addHeader("Authorization", authorization)
                .addHeader("Brand-Id", BuildConfig.Brand_ID)
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .method(original.method(), original.body())
                .build();

        //nonce check
        Response response = chain.proceed(request);
        Log.i(TAG, "response:" + response.code());
        return response;
    }

    @Override
    protected String getBaseUrl() {
        return BuildConfig.LTSDK_API;
    }

}
