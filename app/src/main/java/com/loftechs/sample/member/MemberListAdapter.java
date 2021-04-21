package com.loftechs.sample.member;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseRecyclerViewAdapter;
import com.loftechs.sample.member.list.MemberListContract;
import com.loftechs.sample.model.data.MemberEntity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MemberListAdapter extends BaseRecyclerViewAdapter<MemberEntity, MemberListAdapter.MemberListViewHolder> {

    private Context mContext;
    private MemberListContract.Presenter mPresenter;

    public MemberListAdapter(Context context, MemberListContract.Presenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }


    @Override
    public MemberListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_member, parent, false));
    }

    @Override
    public void onBindViewHolder(MemberListViewHolder holder, int position) {
        MemberEntity memberEntity = getItem(position);
        String showValue = "\n(" + memberEntity.getUserID() + ")";
        holder.nameView.setText((!Strings.isNullOrEmpty(memberEntity.getNickname()) ?
                memberEntity.getNickname() : memberEntity.getPhonenumber()) + showValue);

    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameView;
        private Button deleteButton;

        public MemberListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name_textview);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this::onClick);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MemberEntity memberEntity = getItem(position);
            Log.d("Sibo", "Click userID : " + memberEntity.getUserID() + " nickname : " + memberEntity.getNickname());
            mPresenter.onItemClick(getItem(position));
        }
    }

    public void removeNull() {
        getData().remove(getData().size() - 1);
        notifyItemRemoved(getData().size());
    }
}
