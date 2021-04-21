package com.loftechs.sample.model.http.entity;

import java.util.List;

import lombok.Data;

@Data
public class RegisterRequest {
    String turnkey;
    String verifyMode="turnkey";
    List<User> users;

    @Data
    public static class User{
        String semiUID;
        String semiUID_PW;
    }

}
