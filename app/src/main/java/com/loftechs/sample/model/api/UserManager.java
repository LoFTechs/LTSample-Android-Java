package com.loftechs.sample.model.api;

import com.loftechs.sample.LTSDKManager;
import com.loftechs.sdk.LTSDK;
import com.loftechs.sdk.user.LTUserStatus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class UserManager {
    static final String TAG = UserManager.class.getSimpleName();

    private static class LazyHolder {
        private static final UserManager instance = new UserManager();
    }

    public static UserManager getInstance() {
        return LazyHolder.instance;
    }

    /**
     * Loftechs SDK LTChannelHelper queryChannelMembersByChID
     */
    public Observable<List<LTUserStatus>> getUserStatusWithSemiUIDs(List<String> accountList) {
        return LTSDKManager.getLTSDK()
                .flatMap((Function<LTSDK, ObservableSource<List<LTUserStatus>>>)
                        ltsdk -> ltsdk.getUserStatusWithSemiUIDs(accountList));
    }

}
