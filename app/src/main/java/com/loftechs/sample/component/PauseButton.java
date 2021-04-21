package com.loftechs.sample.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.loftechs.sample.model.api.CallManager;

public class PauseButton extends ToggleButton implements OnCheckedChangeListener {

	public PauseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnCheckedChangeListener(this);
	}


	public boolean isPauseOn() {
		return isChecked();
	}

	public void setPauseOn(boolean state) {
		if (state != isChecked())
			setChecked(state);
	}


	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CallManager.getInstance().setCallHeld(isChecked);
	}

}
