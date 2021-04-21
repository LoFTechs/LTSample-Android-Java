package com.loftechs.sample.model.http.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TokenResponse extends Response{
    String accessToken;
}
