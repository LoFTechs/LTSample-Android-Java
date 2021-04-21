package com.loftechs.sample.register;

import com.loftechs.sample.base.BaseContract;

import java.util.ArrayList;

public interface RegisterContract {
    interface View<T> extends BaseContract.View<T> {
        void toastResult(String returnMsg);

        void setStatus(String status);

        void startMainActivity(String account);

    }

    interface Presenter extends BaseContract.Presenter {

        void login(String userID, String uuid);

        void register(String account, String password);

        void checkAccount(ArrayList<String> accounts);

    }
}
