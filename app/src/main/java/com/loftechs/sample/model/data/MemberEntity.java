package com.loftechs.sample.model.data;

import lombok.Data;

@Data
public class MemberEntity {
    String userID;
    String nickname;
    String phonenumber;

    public MemberEntity(String userID, String nickname, String phonenumber) {
        this.userID = userID;
        this.nickname = nickname;
        this.phonenumber = phonenumber;
    }
}