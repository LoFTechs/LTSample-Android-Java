package com.loftechs.sample.model.http;

import com.loftechs.sample.model.http.entity.LoginRequest;
import com.loftechs.sample.model.http.entity.LoginResponse;
import com.loftechs.sample.model.http.entity.RegisterRequest;
import com.loftechs.sample.model.http.entity.RegisterResponse;
import com.loftechs.sample.model.http.entity.TokenRequest;
import com.loftechs.sample.model.http.entity.TokenResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IService {

    @POST("/oauth2/getDeveloperToken")
    Observable<TokenResponse> getAccessToken(@Body TokenRequest request);

    @POST("/oauth2/register")
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @POST("/oauth2/authenticate")
    Observable<LoginResponse> login(@Body LoginRequest request);
}
