package com.loftechs.sample.common.event;


import lombok.Data;

@Data
public class ChannelCloseEvent {
    String receiverID;
    String chID;

    public ChannelCloseEvent(String receiverID, String chID) {
        this.receiverID = receiverID;
        this.chID = chID;
    }
}
