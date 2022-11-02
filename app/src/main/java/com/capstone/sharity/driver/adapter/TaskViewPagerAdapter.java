package com.capstone.sharity.driver.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.capstone.sharity.driver.fragment.TaskAssignedFragment;
import com.capstone.sharity.driver.fragment.TaskHistoryFragment;

public class TaskViewPagerAdapter extends FragmentStateAdapter {

    public TaskViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new TaskAssignedFragment();
        else if (position == 1)
            fragment = new TaskHistoryFragment();

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
