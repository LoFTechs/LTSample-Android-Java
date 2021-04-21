package com.loftechs.sample.common.event;


import com.loftechs.sdk.im.message.LTMessageResponse;

import lombok.Data;

@Data
public class IncomingMessageEvent {
    String receiverID;
    LTMessageResponse response;

    public IncomingMessageEvent(String receiverID, LTMessageResponse response) {
        this.response = response;
        this.receiverID = receiverID;
    }
}
