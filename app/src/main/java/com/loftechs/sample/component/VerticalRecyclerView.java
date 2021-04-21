package com.loftechs.sample.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class VerticalRecyclerView extends AbstractLinearRecyclerView {

    public VerticalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        RecyclerViewAnimator animator = new RecyclerViewAnimator();
        animator.setRemoveDuration(0);
        animator.setAddDuration(0);
        animator.setChangeDuration(0);
        animator.setMoveDuration(0);
        setItemAnimator(animator);
    }

    @Override
    public int getOrientation() {
        return 0;
    }
}
