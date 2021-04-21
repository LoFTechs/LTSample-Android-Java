package com.loftechs.sample.common.event;


import lombok.Data;

@Data
public class UpdateMessageEvent {
    String receiverID;
    String chID;

    public UpdateMessageEvent(String receiverID, String chID) {
        this.receiverID = receiverID;
        this.chID = chID;
    }
}
