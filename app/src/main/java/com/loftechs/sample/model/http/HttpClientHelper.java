package com.loftechs.sample.model.http;

import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

abstract class HttpClientHelper {

    public static final String CONTENT_TYPE_JSON = "application/json";
    protected OkHttpClient.Builder client = new OkHttpClient.Builder();
    GsonConverterFactory gsonFactory = null;
    ScalarsConverterFactory scalarsFactory = null;
    protected Retrofit mRetrofit = null;

    protected abstract Response setInterceptorAction(Interceptor.Chain chain) throws IOException;

    protected abstract String getBaseUrl();

    public HttpClientHelper() {
        gsonFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
                        .create());
        scalarsFactory = ScalarsConverterFactory.create();
    }

    protected void init() {
        restClient();
        resetApp();
    }

    private CertificatePinner createCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("baby.juiker.net", "sha256/jR3zdhzwnG+GruQYsx51BYWBVVqcOipsvA7l9l8KpHA=")
                .build();
    }


    private void restClient() {
        client.certificatePinner(createCertificatePinner())
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LTIntercept());
    }

    class LTIntercept implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            return setInterceptorAction(chain);
        }
    }

    private void resetApp() {
        String baseURL = getBaseUrl();
        if(Strings.isNullOrEmpty(baseURL)) {
            baseURL = "https://";
        } else if(!baseURL.startsWith("http")){
            baseURL = "https://" + baseURL;
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client.build())
                .addConverterFactory(scalarsFactory)
                .addConverterFactory(gsonFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int code = json.getAsInt();
            return code == 0 ? false : true;
        }
    }

    public IService getService() {
        return mRetrofit.create(IService.class);
    }

    public <T> T createService(Class<T> service) {
        return mRetrofit.create(service);
    }
}
