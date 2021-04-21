package com.loftechs.sample.base;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class BaseRecyclerViewAdapter<Data, ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    @Getter
    @Accessors(prefix = "m")
    private List<Data> mData;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<Data> data) {
        if(data != null){
            mData = new ArrayList<>(data);
        }
    }

    public Data getItem(int position) {
        return mData.get(position);
    }

}
