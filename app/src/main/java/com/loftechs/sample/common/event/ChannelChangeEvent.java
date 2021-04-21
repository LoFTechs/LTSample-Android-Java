package com.loftechs.sample.common.event;


import lombok.Data;

@Data
public class ChannelChangeEvent {
    String receiverID;
    String chID;
    boolean mute;
    String subject;

    public ChannelChangeEvent(String receiverID, String chID) {
        this.receiverID = receiverID;
        this.chID = chID;
    }
}
