/*
EraseButton.java
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import com.loftechs.sample.call.VibrateHelper;

public class EraseButton extends ImageButton implements OnClickListener, OnLongClickListener, OnTouchListener {

    private AddressButton address;

    public EraseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        setOnLongClickListener(this);
        setOnTouchListener(this);
    }


    public void onClick(View v) {
        if (address.getText().length() > 0) {
//			int lBegin = address.getSelectionStart();
            int lBegin = address.getText().length();
//			if (lBegin == -1) {
//				lBegin = address.getEditableText().length()-1;
//			}
            if (lBegin > 0) {
//				address.getEditableText().delete(lBegin-1,lBegin);
                String text = address.getText().toString();
                address.setText(text.substring(0, lBegin - 1));

            }
        } else {

        }
    }


    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            VibrateHelper.onPressed(getContext());
        }
        return false;
    }


    public boolean onLongClick(View v) {
        address.setText("");
        address.clearAll();
        return true;
    }

    public void setAddressView(AddressButton view) {
        address = view;
    }

}
