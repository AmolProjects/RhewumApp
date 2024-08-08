package com.rhewum.Activity.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rhewum.Activity.Fragments.PsdFragment;
import com.rhewum.Activity.Fragments.SummeryFragment;

public class VibCheckerAdapter extends FragmentStateAdapter {

    public VibCheckerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new PsdFragment();
        }
        return new SummeryFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
