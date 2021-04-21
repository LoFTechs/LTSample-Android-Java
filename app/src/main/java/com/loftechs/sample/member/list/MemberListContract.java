package com.loftechs.sample.member.list;

import com.loftechs.sample.base.BaseContract;
import com.loftechs.sample.model.data.MemberEntity;

import java.util.List;

public interface MemberListContract {
    interface View<T> extends BaseContract.View<T> {
        void toastUpdateResult(String returnMsg);

        void refreshList(List<MemberEntity> memberEntities);
    }

    interface Presenter extends BaseContract.Presenter {
        void initData();

        void refreshData();

        void onItemClick(MemberEntity memberEntity);

    }
}
