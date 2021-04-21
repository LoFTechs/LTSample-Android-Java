package com.loftechs.sample.common.event;

import lombok.Data;

@Data
public class UserProfileChangeEvent {
    String receiverID;
    String userID;

    public UserProfileChangeEvent(String receiverID, String userID) {
        this.receiverID = receiverID;
        this.userID = userID;
    }
}
