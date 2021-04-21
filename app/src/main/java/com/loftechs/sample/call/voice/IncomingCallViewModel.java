package com.loftechs.sample.call.voice;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.loftechs.sample.R;
import com.loftechs.sample.model.api.CallManager;
import com.loftechs.sample.component.CallButton;
import com.loftechs.sample.component.HangCallButton;


class IncomingCallViewModel {

    private final static String TAG = "IncomingCallViewModel";

    public enum State {
        STATE_CONNECT_HANDLE
    }

    ;

    public static IncomingCallViewModel init(final View rootView) {
        IncomingCallViewModel viewModel = new IncomingCallViewModel(rootView);

        viewModel.setupViews();

        return viewModel;
    }


    private void setupViews() {
        mPhoneNumber = (TextView) mRootView.findViewById(R.id.incoming_call_phone_number);
        mStatus = (TextView) mRootView.findViewById(R.id.incoming_call_status);

        mDeclineCall = (HangCallButton) mRootView.findViewById(R.id.incoming_call_decline);
        mDeclineCall.setExternalClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CallManager.getInstance().doHangup();
            }
        });

        mAcceptCall = (CallButton) mRootView.findViewById(R.id.incoming_call_accept);
        mAcceptCall.setExternalClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setStatus(R.string.msg_waiting_for_connection);
                setDisableAcceptCall();
                CallManager.getInstance().doAccept();
            }
        });
    }

    public IncomingCallViewModel show() {
        mRootView.setVisibility(View.VISIBLE);
        return this;
    }

    public IncomingCallViewModel hide() {
        mRootView.setVisibility(View.GONE);
        return this;
    }


    public IncomingCallViewModel setPhoneNumber(final String value) {

        mPhoneNumber.setText(value);

        return this;
    }

    public IncomingCallViewModel setStatus(final String value) {
        mStatus.setText(value);
        return this;
    }

    public IncomingCallViewModel setStatus(final int valueRes) {
        mStatus.setText(valueRes);
        return this;
    }

    public IncomingCallViewModel setStatus(final State state) {
        switch (state) {
            case STATE_CONNECT_HANDLE: {
                setStatus(R.string.msg_waiting_for_connection);
                mAcceptCall.setVisibility(View.GONE);
            }
            break;
            default:
                break;
        }
        return this;
    }

    public IncomingCallViewModel setDisableAcceptCall() {
        mAcceptCall.setEnabled(false);
        return this;
    }

    private final View mRootView;

    private TextView mPhoneNumber;
    private TextView mStatus;
    private HangCallButton mDeclineCall;
    private CallButton mAcceptCall;

    private IncomingCallViewModel(final View rootView) {
        mRootView = rootView;
    }
}
