package com.loftechs.sample.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public static int VIEW_TYPE_ITEM = 0;
    public static int VIEW_TYPE_LOADING = 1;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
