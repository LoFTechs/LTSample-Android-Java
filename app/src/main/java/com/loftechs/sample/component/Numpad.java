/*
NumpadView.java
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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loftechs.sample.R;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Guillaume Beraudo
 *
 */
public class Numpad extends LinearLayout implements AddressAware {

	public Numpad(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Numpad, 0, 0);
		int numberLayoutId = a.getResourceId(R.styleable.Numpad_number_layout, 0);
        a.recycle();		
		LayoutInflater.from(context).inflate(numberLayoutId, this);
		setLongClickable(true);
	}

	public void setAddressWidget(AddressButton address) {
		for (AddressAware v : retrieveChildren(this)) {
			v.setAddressWidget(address);
		}
	}

	@Override
	public void setToneGeneratorUtils(ToneGeneratorUtils _toneUtils) {

	}


	private Collection<AddressAware> retrieveChildren(ViewGroup viewGroup) {
		final Collection<AddressAware> views = new ArrayList<AddressAware>();

		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				views.addAll(retrieveChildren((ViewGroup) v));
			} else {
				if (v instanceof AddressAware)
					views.add((AddressAware) v);
			}
		}

		return views;
	}

}
