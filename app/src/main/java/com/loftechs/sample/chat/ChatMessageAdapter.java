package com.loftechs.sample.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loftechs.sample.R;
import com.loftechs.sample.base.BaseRecyclerViewAdapter;
import com.loftechs.sample.utils.DateFormatUtil;
import com.loftechs.sdk.im.message.LTMessageResponse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatMessageAdapter extends BaseRecyclerViewAdapter<LTMessageResponse, ChatMessageAdapter.ChatMessageViewHolder> {

    private Context mContext;
    private ChatMessageContract.Presenter mPresenter;

    public ChatMessageAdapter(Context context, ChatMessageContract.Presenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_chatmessage, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        LTMessageResponse item = (LTMessageResponse) getItem(position);
        ((ChatMessageViewHolder) holder).senderView.setText(mPresenter.getSender(item));
        ((ChatMessageViewHolder) holder).typeView.setText(mPresenter.getType(item));
        ((ChatMessageViewHolder) holder).contentView.setText(mPresenter.getContent(item));
        ((ChatMessageViewHolder) holder).timeView.setText(DateFormatUtil.getStringFormat(item.getSendTime(),"YYYY/MM/dd HH:mm:ss"));
        ((ChatMessageViewHolder) holder).recallView.setVisibility(mPresenter.getRecall(item) ? View.VISIBLE : View.GONE);
        ((ChatMessageViewHolder) holder).deleteView.setVisibility(mPresenter.getDelete(item) ? View.VISIBLE : View.GONE);
    }

    public class ChatMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView senderView;
        private TextView typeView;
        private TextView contentView;
        private TextView timeView;
        private Button recallView;
        private Button deleteView;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderView = itemView.findViewById(R.id.chat_message_sender);
            contentView = itemView.findViewById(R.id.chat_message_content);
            typeView = itemView.findViewById(R.id.chat_message_type);
            timeView = itemView.findViewById(R.id.chat_message_time);
            recallView = itemView.findViewById(R.id.chat_message_recall);
            deleteView = itemView.findViewById(R.id.chat_message_delete);
            recallView.setOnClickListener(this::onClick);
            deleteView.setOnClickListener(this::onClick);
        }


        @Override
        public void onClick(View v) {
            LTMessageResponse item = getItem(getAdapterPosition());
            switch (v.getId()) {
                case R.id.chat_message_recall: {
                    mPresenter.recallMessage(item.getMsgID());
                }
                break;
                case R.id.chat_message_delete: {
                    mPresenter.deleteMessage(item.getMsgID());
                }
                break;
            }
        }
    }

    public void removeNull() {
        getData().remove(getData().size() - 1);
        notifyItemRemoved(getData().size());
    }

    public void addDate(LTMessageResponse response) {
        getData().add(0, response);
    }

}
