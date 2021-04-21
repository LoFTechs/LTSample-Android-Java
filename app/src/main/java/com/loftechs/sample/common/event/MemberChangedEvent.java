package com.loftechs.sample.common.event;

import lombok.Data;

@Data
public class MemberChangedEvent {
    String receiverID;
    String chID;

    public MemberChangedEvent(String receiverID, String chID) {
        this.receiverID = receiverID;
        this.chID = chID;
    }
}
