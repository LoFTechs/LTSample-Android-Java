package com.loftechs.sample.base;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.loftechs.sample.R;
import com.loftechs.sample.chat.ChatListFragment;
import com.loftechs.sample.common.event.ChannelCloseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID;
import static com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showShortToast(String toastContent) {
        Toast.makeText(getContext(), toastContent, Toast.LENGTH_SHORT).show();
    }

    public void showShortToast(int toastContentResId) {
        Toast.makeText(getContext(), toastContentResId, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String toastContent) {
        Toast.makeText(getContext(), toastContent, Toast.LENGTH_LONG).show();
    }

    public void showLongToast(int toastContentResId) {
        Toast.makeText(getContext(), toastContentResId, Toast.LENGTH_LONG).show();
    }

    public void changeFragment(Fragment f, Bundle intentBundle) {
        DetailsTransition detailsTransition = new DetailsTransition();
        f.setSharedElementEnterTransition(detailsTransition);
        f.setSharedElementReturnTransition(detailsTransition);
        f.setArguments(intentBundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.addToBackStack(this.getClass().getName());
        transaction.commit();
    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChannelCloseEvent event) {
        String userID = getArguments().getString(EXTRA_RECEIVER_ID);
        String chID = getArguments().getString(EXTRA_CHANNEL_ID);
        if (Strings.isNullOrEmpty(userID) || Strings.isNullOrEmpty(chID)) {
            return;
        }
        showShortToast("ChannelCloseEvent");
        clearBackStack();
    }

    public void clearBackStack() {
        getActivity().getSupportFragmentManager().popBackStackImmediate(ChatListFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
