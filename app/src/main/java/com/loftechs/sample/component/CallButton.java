/*
CallButton.java
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
import android.widget.Toast;

import com.loftechs.sample.R;
import com.loftechs.sample.call.VibrateHelper;


/**
 * @author Guillaume Beraudo
 *
 */
public class CallButton extends ImageButton implements OnClickListener, AddressAware, OnLongClickListener, OnTouchListener {
	private AddressButton mAddress;

	public void setAddressWidget(AddressButton a) {mAddress = a;}

	@Override
	public void setToneGeneratorUtils(ToneGeneratorUtils _toneUtils) {

	}

	private OnClickListener externalClickListener;
	public void setExternalClickListener(OnClickListener e) {externalClickListener = e;}


	public CallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);

		setOnTouchListener(this);
	}


	public void onClick(View v) {
		if (externalClickListener != null) externalClickListener.onClick(v);
	}


	protected void onWrongDestinationAddress() {
		Toast toast = Toast.makeText(getContext()
				,String.format(getResources().getString(R.string.toast_warning_wrong_destination_address),mAddress.getText().toString())
				,Toast.LENGTH_LONG);
		toast.show();
	}


	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN ) {
			VibrateHelper.onPressed(getContext());
		}
		return false;
	}


	public boolean onLongClick(View v) {
		if(mAddress == null) {
			return false;
		}

		if(mAddress.getText().length() > 0) {
			// Called if "0+" dtmf
			// MVPN先設定為false, 之後再修改為向Server查詢
			return true;
		}
		return false;
	}
}
