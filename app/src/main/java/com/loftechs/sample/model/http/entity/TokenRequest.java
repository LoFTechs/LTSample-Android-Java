package com.loftechs.sample.model.http.entity;

import lombok.Getter;

public class TokenRequest {
    @Getter
    String scope = "tw:api:sdk";
}
