package com.loftechs.sample.call;

public interface AddressType {
    void setText(CharSequence s);

    CharSequence getInputText();

    CharSequence getText();

    void setDisplayedName(String s);

    String getDisplayedName();

    void setISRCode(String code);

    String getISRCode();
}
