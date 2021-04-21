package com.loftechs.sample.call;

import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.model.api.CallManager;
import com.loftechs.sample.model.api.UserManager;
import com.loftechs.sample.utils.DateFormatUtil;
import com.loftechs.sdk.call.api.LTUserCDRResponse;
import com.loftechs.sdk.call.message.LTCallCDRNotificationMessage;
import com.loftechs.sdk.user.LTUserStatus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class CallPresenter implements CallContract.Presenter {
    static final String TAG = CallPresenter.class.getSimpleName();
    final CallContract.View<?> mView;
    private LTUserStatus mLTUserStatus;

    public CallPresenter(@NonNull CallContract.View view) {
        mView = checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void create() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void checkAccount(String account) {
        if (Strings.isNullOrEmpty(account)) {
            mView.toastResult("Account can't empty");
            mView.setCallFunctionVisiable(false);
            return;
        }
        List<String> accountList = new ArrayList<>();
        accountList.add(account);
        UserManager.getInstance().getUserStatusWithSemiUIDs(accountList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LTUserStatus>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull List<LTUserStatus> ltUserStatuses) {
                        Log.d(TAG, "checkAccount : " + ltUserStatuses.toString());
                        if (ltUserStatuses.isEmpty()) {
                            mView.toastResult("Account has error");
                            mView.setCallFunctionVisiable(false);
                        } else {
                            LTUserStatus ltUserStatus = ltUserStatuses.get(0);
                            mLTUserStatus = ltUserStatus;
                            if (ltUserStatus.isCanVOIP()) {
                                mView.toastResult("Get account success");
                                mView.setCallFunctionVisiable(true);
                            } else {
                                mView.toastResult("Isn't support voice call.");
                                mView.setCallFunctionVisiable(false);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "checkAccount e : " + e.toString());
                        mView.toastResult("Account has error");
                        mView.setCallFunctionVisiable(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void outgoingCall() {
        if (mLTUserStatus == null || Strings.isNullOrEmpty(mLTUserStatus.getUserID())) {
            mView.toastResult("Please check account!");
            return;
        }
        CallManager.getInstance().doOutgoingCallWithUserID(mLTUserStatus.getSemiUID(), mLTUserStatus.getUserID());
    }

    @Override
    public void getCallLog() {
        CallManager.getInstance().getCallLog(System.currentTimeMillis(), -20)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTUserCDRResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull LTUserCDRResponse ltUserCDRResponse) {
                        Log.d(TAG, "getCallLog : " + ltUserCDRResponse.getCdrMessages().toString());
                        if (ltUserCDRResponse.getCdrMessages().isEmpty()) {
                            mView.setCallLog("Call Log is empty!");
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Count : " + ltUserCDRResponse.getCdrMessages().size());
                            stringBuilder.append("\n");
                            for (LTCallCDRNotificationMessage cdrMessage : ltUserCDRResponse.getCdrMessages()) {
                                stringBuilder.append("-------------\n");
                                stringBuilder.append("Call time : " + DateFormatUtil.getStringFormat(cdrMessage.getSendTime(), "MM/dd HH:mm:ss"));
                                stringBuilder.append("\n");
                                stringBuilder.append("Caller : " + cdrMessage.getCallerInfo().getSemiUID());
                                stringBuilder.append("\n");
                                stringBuilder.append("Callee : " + cdrMessage.getCalleeInfo().getSemiUID());
                                stringBuilder.append("\n");
                                stringBuilder.append("Duation : " + (DateFormatUtil.getStringFormat(cdrMessage.getCallEndTime() - cdrMessage.getCallStartTime(), "mm:ss")));
                                stringBuilder.append("\n");
                                stringBuilder.append("-------------\n");
                            }
                            mView.setCallLog(stringBuilder.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "getCallLog e : " + e.toString());
                        mView.toastResult("Get call log failed.");
                        mView.setCallLog("");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
