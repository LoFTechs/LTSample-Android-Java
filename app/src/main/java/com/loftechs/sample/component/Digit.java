/*
Digit.java
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
import android.widget.ImageButton;


public class Digit extends ImageButton implements AddressAware {
	private static final String ns = null;
	private AddressButton mAddress;
	private ToneGeneratorUtils toneUtils;
	String mDigit;
	private boolean bVoice = true;

//	@Override
//	protected void onTextChanged(CharSequence text, int start, int before,
//			int after) {
//		super.onTextChanged(text, start, before, after);
//
//		if (text == null || text.length() < 1) return;
//
//		DialKeyListener lListener = new DialKeyListener();
//		setOnClickListener(lListener);
//		setOnTouchListener(lListener);
//
//		if ("0+".equals(text)) {
//			setOnLongClickListener(lListener);
//
//		}
//	}


	protected void onSetDigit(String digit) {
		if (digit == null || digit.length() < 1) return;

		mDigit = digit;
		DialKeyListener lListener = new DialKeyListener();
		setOnClickListener(lListener);
		if(bVoice)
			setOnTouchListener(lListener);

		if ("0+".equals(digit)) {
			setOnLongClickListener(lListener);
		}
	}


	public Digit(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		toneUtils = ToneGeneratorUtils.getInstance();
		setLongClickable(true);
		if("false".equals(attrs.getAttributeValue(ns, "voice"))){
			bVoice = false;
		}
		onSetDigit(attrs.getAttributeValue(ns, "digit"));
	}

	public Digit(Context context, AttributeSet attrs) {
		super(context, attrs);
		toneUtils = ToneGeneratorUtils.getInstance();
		setLongClickable(true);
		if("false".equals(attrs.getAttributeValue(ns, "voice"))){
			bVoice = false;
		}
		onSetDigit(attrs.getAttributeValue(ns, "digit"));
	}

	public Digit(Context context) {
		super(context);
		toneUtils = ToneGeneratorUtils.getInstance();
		setLongClickable(true);
	}


	public String getDigit() {
		return mDigit;
	}

	public ToneGeneratorUtils getToneGeneratorUtils() {
		return toneUtils;
	}

	public void setToneGeneratorUtils(ToneGeneratorUtils _toneUtils) {
		toneUtils =_toneUtils;
	}

	private class DialKeyListener implements OnClickListener, OnTouchListener, OnLongClickListener {
		final CharSequence mKeyCode;
		boolean mIsDtmfStarted=false;

		DialKeyListener() {
//			mKeyCode = Digit.this.getText().subSequence(0, 1);
			mKeyCode = Digit.this.getDigit().subSequence(0, 1);
		}

		public void onClick(View v) {
			if(bVoice){
//                          if (LinphoneManager.isReady()) {
//                            LinphoneCore lc = LinphoneManager.getLc();
////                            lc.stopDtmf();
//
//                            if (lc.isIncall()) {
//                            	lc.sendDtmf(mKeyCode.charAt(0));
//                            	lc.playDtmf(mKeyCode.charAt(0), 1);
//                            }
//                          }
				//todo no dtmf temporally
//				if(CallManager.isInCall()) {
//					CallManager.getInstance().sendDTMF(String.valueOf(mKeyCode.charAt(0)));
//				}

				mIsDtmfStarted =false;
			}
			if (mAddress != null) {
				int lBegin = mAddress.getSelectionStart();
				if (lBegin == -1) {
					lBegin = mAddress.length();
				}
				if (lBegin >=0) {
//					mAddress.getEditableText().insert(lBegin,mKeyCode);
					if(mKeyCode.equals("-")){// back
						if(mAddress.length() > 0){
							mAddress.setText(mAddress.getText().subSequence(0, mAddress.length() - 1));
						}
					}else{
						mAddress.setText(mAddress.getText() + mKeyCode.toString());
					}
				}
			}
		}

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                          VibrateHelper.onPressed(getContext());

				if (!mIsDtmfStarted) {
					toneUtils.playTone(mKeyCode.charAt(0));
					mIsDtmfStarted = true;
//                            }
				}
			} else {
				if (event.getAction() == MotionEvent.ACTION_UP) {
				}
				mIsDtmfStarted =false;
			}
			return false;
		}

		public boolean onLongClick(View v) {
			// Called if "0+" dtmf

			if (mAddress == null) return true;

//			int lBegin = mAddress.getSelectionStart();
			int lBegin = mAddress.getText().length();
			if (lBegin == -1) {
				lBegin = mAddress.getEditableText().length();
			}
			if (lBegin >=0) {
//			mAddress.getEditableText().insert(lBegin,"+");
				mAddress.setText(mAddress.getText() + "+");
			}
			return true;
		}
	};


	public void setAddressWidget(AddressButton address) {
		mAddress = address;
	}
}
