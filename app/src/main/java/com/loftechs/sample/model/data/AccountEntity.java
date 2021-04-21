package com.loftechs.sample.model.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class AccountEntity implements Serializable {
    String account;
    String password;
    String userID;
    String uuid;

    public AccountEntity() {
        super();
    }

    public AccountEntity(String account, String password, String userID, String uuid) {
        this.account = account;
        this.password = password;
        this.userID = userID;
        this.uuid = uuid;
    }
}