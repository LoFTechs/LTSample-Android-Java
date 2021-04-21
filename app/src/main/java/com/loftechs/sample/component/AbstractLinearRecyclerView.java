package com.loftechs.sample.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractLinearRecyclerView  extends RecyclerView {

    private LinearLayoutManager mLinearLayoutManager;

    public AbstractLinearRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLinearLayoutManager = new LinearLayoutManager(context, getOrientation(), false);
        setLayoutManager(mLinearLayoutManager);
    }

    public int getFirstVisiblePosition() {
        return mLinearLayoutManager.findFirstVisibleItemPosition();
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        mLinearLayoutManager.scrollToPositionWithOffset(position, offset);
    }

    /**
     * Get the orientation of the linear layout.
     *
     * @return the orientation
     */
    public abstract int getOrientation();
}
