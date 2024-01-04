package com.example.griptrainerapp.tutorial;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class TutorialAdapter extends FragmentStateAdapter {

    public TutorialAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TutorialPageOneFragment();
            case 1:
                return new TutorialPageTwoFragment();
            case 2:
                return new TutorialPageThreeFragment();
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Total number of pages
    }
}

