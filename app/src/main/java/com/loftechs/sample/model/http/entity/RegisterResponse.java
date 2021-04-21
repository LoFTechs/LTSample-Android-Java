package com.loftechs.sample.model.http.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RegisterResponse extends Response{

    List<User> users;

    @Data
    public static class User{
        String semiUID;
        String userID;
        String uuid;
        String err;
    }
}
