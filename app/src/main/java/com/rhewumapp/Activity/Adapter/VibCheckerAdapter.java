package com.rhewumapp.Activity.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rhewumapp.Activity.Fragments.PsdFragment;
import com.rhewumapp.Activity.Fragments.SummeryFragment;

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
