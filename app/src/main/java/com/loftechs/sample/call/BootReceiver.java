/*
BootReceiver.java
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
package com.loftechs.sample.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	static final String TAG = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.v(TAG, "receive boot broadcast.");
	    if(context == null || intent == null) return;
			try{
			    Log.v(TAG, "BootReceiver Waiting");
				Thread.sleep(10 * 1000);
				Log.v(TAG, "Start Voip service in background.");
			} catch(Exception e) {}
	}
}
