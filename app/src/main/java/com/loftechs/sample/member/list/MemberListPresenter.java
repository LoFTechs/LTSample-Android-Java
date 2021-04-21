package com.loftechs.sample.member.list;

import android.util.Log;

import com.loftechs.sample.model.api.MemberManager;
import com.loftechs.sample.model.data.MemberEntity;
import com.loftechs.sdk.im.channels.LTKickMemberResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemberListPresenter implements MemberListContract.Presenter {
    static final String TAG = MemberListPresenter.class.getSimpleName();
    final MemberListContract.View<MemberListFragment> mView;
    ArrayList<MemberEntity> mMemberEntities;
    String mUserID;
    String mChID;

    public MemberListPresenter(@NonNull MemberListContract.View view, String userID, String chID) {
        mView = checkNotNull(view, "view cannot be null!");
        mUserID = userID;
        mChID = chID;
        mMemberEntities = new ArrayList<>();
    }

    private ArrayList<MemberEntity> getMemberEntities() {
        if (null == mMemberEntities) {
            mMemberEntities = new ArrayList<>();
        }
        return mMemberEntities;
    }


    @Override
    public void create() {

    }

    @Override
    public void resume() {
        refreshData();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void initData() {
        Log.d(TAG, mUserID + " chID  " + mChID);
        MemberManager.getInstance().queryAllChannelMembers(mUserID, mChID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MemberEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<MemberEntity> memberEntities) {
                        getMemberEntities().addAll(memberEntities);
                        mView.refreshList(getMemberEntities());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("queryAllChannelMembers error.");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onItemClick(MemberEntity memberEntity) {
        Set<String> members = new HashSet<>();
        members.add(memberEntity.getUserID());
        MemberManager.getInstance().kickMembers(mUserID, mChID, members)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTKickMemberResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTKickMemberResponse ltKickMemberResponse) {
                        refreshData();
                        mView.toastUpdateResult("kickMembers Success.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toastUpdateResult("kickMembers error.");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void refreshData() {
        mMemberEntities = null;
        initData();
    }
}
