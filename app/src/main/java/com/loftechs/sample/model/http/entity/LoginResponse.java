package com.loftechs.sample.model.http.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LoginResponse extends Response {
    String semiUID;
    String userID;
    String uuid;
}
