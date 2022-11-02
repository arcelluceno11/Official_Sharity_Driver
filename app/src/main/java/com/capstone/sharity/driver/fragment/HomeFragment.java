package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.adapter.TaskViewPagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    //Variables
    MaterialToolbar materialToolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Teliver.init(requireContext(), "7537696e87db9f4ffb2c9e28a5fd51ea")

        //Initialize Variables
        materialToolbar = view.findViewById(R.id.materialToolbar);
        tabLayout = view.findViewById(R.id.tabLayoutTask);
        viewPager = view.findViewById(R.id.viewPagerTask);

        //Toolbar
        materialToolbar.inflateMenu(R.menu.main_toolbar_menu);
        materialToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainToolbarNotificationsActivity:
                    NavDirections notificationFragment = HomeFragmentDirections.actionHomeFragmentToNotificationFragment();
                    Navigation.findNavController(view).navigate(notificationFragment);
                    break;
                case R.id.mainToolbarAccountActivity:
                    NavDirections accountFragment = HomeFragmentDirections.actionHomeFragmentToAccountFragment();
                    Navigation.findNavController(view).navigate(accountFragment);
                    break;
                default:
                    break;
            }

            return true;
        });

        //ViewPager
        TaskViewPagerAdapter accountOrderViewPagerAdapter = new TaskViewPagerAdapter(this);
        viewPager.setAdapter(accountOrderViewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Assigned" : "History")
        ).attach();
    }
}