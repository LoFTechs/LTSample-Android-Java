/*
AddressView.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.loftechs.sample.component;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.loftechs.sample.call.AddressType;
import com.loftechs.sample.call.VibrateHelper;

/**
 * @author Guillaume Beraudo
 */
public class AddressButton extends Button
        implements AddressType, OnClickListener, OnTouchListener {

    private String displayedName;
    private Uri pictureUri;
    private boolean bAutoShowCallChooserDialog = true;
    private boolean bCheckHiddenNumberType = true;

    public void setPictureUri(Uri uri) {
        pictureUri = uri;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    private OnClickListener externalClickListener;

    public void setExternalClickListener(OnClickListener e) {
        externalClickListener = e;
    }

    public AddressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this);
        this.setOnTouchListener(this);
    }

    public void clearDisplayedName() {
        displayedName = "";
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setContactAddress(String uri, String displayedName) {
        setText(uri);
        this.displayedName = displayedName;
    }

    @Override
    public CharSequence getInputText() {
        return null;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public void setAutoShowCallChooserDialog(boolean autoShow) {
        this.bAutoShowCallChooserDialog = autoShow;
    }

    public void setCheckHiddenNumberType(boolean checkHiddenNumberType) {
        this.bCheckHiddenNumberType = checkHiddenNumberType;
    }

    public void clearAll() {
        clearDisplayedName();
        setPictureUri(null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        clearDisplayedName();
        pictureUri = null;
        super.onTextChanged(text, start, before, after);
    }

    public void onClick(View v) {
        if (bAutoShowCallChooserDialog) {
            showCallChooserDialog();
        }
        if (externalClickListener != null) {
            externalClickListener.onClick(v);
        }
    }

    private void showCallChooserDialog() {
        if (this.getText().length() > 0) {
            String number = this.getText().toString();

        }
    }

    protected void onWrongDestinationAddress() {
//        Toast toast = Toast.makeText(getContext()
//                , String.format(getResources().getString(R.string.toast_warning_wrong_destination_address), this
//                        .getText().toString())
//                , Toast.LENGTH_LONG);
//        toast.show();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            VibrateHelper.onPressed(getContext());
        }
        return false;
    }

    @Override
    public void setISRCode(String code) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getISRCode() {
        // TODO Auto-generated method stub
        return "";
    }

}
