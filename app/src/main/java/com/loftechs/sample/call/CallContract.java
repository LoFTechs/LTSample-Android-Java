package com.loftechs.sample.call;

import com.loftechs.sample.base.BaseContract;

public interface CallContract {
    interface View<T> extends BaseContract.View<T> {
        void toastResult(String returnMsg);

        void setCallLog(String callLog);

        void setCallFunctionVisiable(boolean visiable);

    }

    interface Presenter extends BaseContract.Presenter {

        void checkAccount(String account);

        void outgoingCall();

        void getCallLog();

    }
}
