/*
IncallTimer.java
Copyright (C) 2011  Belledonne Communications, Grenoble, France

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
import android.widget.TextView;

/**
 * Widget displaying a time
 * @author Guillaume Beraudo
 *
 */
public class IncallTimer extends TextView {

	public IncallTimer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IncallTimer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IncallTimer(Context context) {
		super(context);
	}


	@Override
	public void setText(CharSequence text, BufferType type) {
		try {
			int seconds = Integer.parseInt(text.toString());
			super.setText(formatTime(seconds), type);
		} catch (Throwable e) {
			super.setText(text, type);
		}
	}

	public String formatTime(final int seconds) {
		String time = String.format("%02d : %02d",
				(seconds / 60) % 60,
				seconds % 60);
	
		int hours = seconds / 3600;
		if (hours != 0) {
			return hours + " - " + time;
		} else {
			return time;
		}
	}

	
	/**
	 * Format time as "days - hh : mm : ss".
	 * @param seconds
	 * @return formated string
	 */
	/*protected String formatTime(final int seconds) {
		int value = seconds;
		StringBuilder sb = new StringBuilder();

		for (int base : Arrays.asList(60, 60, 24)) {
			if (value == 0) break;

			int remainder = value % base;
			value /= base;

			if (sb.length() != 0) sb.insert(0, " : ");
			sb.insert(0, remainder < 10 ? "0" + remainder : remainder);
		}

		if (value != 0) sb.insert(0, value + " - ");

		return sb.toString();
	}
	*/
}
