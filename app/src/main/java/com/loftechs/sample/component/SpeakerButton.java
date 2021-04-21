/*
SpeakerButton.java
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.loftechs.sample.model.api.CallManager;

/**
 * @author Guillaume Beraudo
 *
 */
public class SpeakerButton extends ToggleButton implements OnCheckedChangeListener {

	public SpeakerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnCheckedChangeListener(this);
	}


	public boolean isSpeakerOn() {
		return isChecked();
	}

	public void setSpeakerOn(boolean state) {
		if (state != isChecked())
			setChecked(state);
	}


	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			CallManager.getInstance().routeAudioToSpeaker();
		} else {
			CallManager.getInstance().routeAudioToReceiver();
		}
	}


}
