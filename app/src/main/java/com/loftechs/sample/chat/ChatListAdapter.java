package com.loftechs.sample.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseRecyclerViewAdapter;
import com.loftechs.sample.utils.DateFormatUtil;
import com.loftechs.sdk.im.channels.LTChannelResponse;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.channels.LTMemberPrivilege;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends BaseRecyclerViewAdapter<LTChannelResponse, ChatListAdapter.ChatListViewHolder> {

    private Context mContext;
    private ChatListContract.Presenter mPresenter;

    public ChatListAdapter(Context context, ChatListContract.Presenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }


    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_chatlist, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {
        LTChannelResponse item = getItem(position);
        String title = item.getChID();
        if (Strings.isNullOrEmpty(item.getSubject())
                && item.getChType() == LTChannelType.SINGLE) {
            for (LTMemberPrivilege ltMemberPrivilege : item.getMemberPrivilege()) {
                if (!Strings.isNullOrEmpty(ltMemberPrivilege.getNickname())
                        && !ltMemberPrivilege.getUserID().equals(mPresenter.getReceiverID())) {
                    title = ltMemberPrivilege.getNickname();
                    break;
                }
            }
        } else {
            title = item.getSubject();
        }
        ((ChatListViewHolder) holder).titleView.setText(title);
        String content = item.getLastMsgContent();
        ((ChatListViewHolder) holder).contentView.setText(content);
        ((ChatListViewHolder) holder).timeView.setText(DateFormatUtil.getStringFormat(item.getLastMsgTime(), "YYYY/MM/dd HH:mm:ss"));
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleView;
        private TextView contentView;
        private TextView timeView;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            titleView = itemView.findViewById(R.id.chat_list_title);
            contentView = itemView.findViewById(R.id.chat_list_content);
            timeView = itemView.findViewById(R.id.chat_list_time);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mPresenter.onItemClick(getItem(position));
        }
    }

    public void removeNull() {
        getData().remove(getData().size() - 1);
        notifyItemRemoved(getData().size());
    }
}
