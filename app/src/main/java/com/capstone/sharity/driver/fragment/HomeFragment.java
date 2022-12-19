package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.adapter.TaskViewPagerAdapter;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Objects;

import io.teliver.sdk.core.TLog;
import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.Task;
import io.teliver.sdk.models.UserBuilder;

public class HomeFragment extends Fragment {

    //Variables
    DriverViewModel driverViewModel;
    MaterialToolbar materialToolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize Variables
        materialToolbar = view.findViewById(R.id.materialToolbar);
        tabLayout = view.findViewById(R.id.tabLayoutTask);
        viewPager = view.findViewById(R.id.viewPagerTask);
        progressBar = view.findViewById(R.id.progressBar);

        //Initialize ViewModel
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Toolbar
        materialToolbar.inflateMenu(R.menu.main_toolbar_menu);
        materialToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainToolbarRefresh:
                    driverViewModel.driverTasks.setValue(null);
                    driverViewModel.driverTasksHistory.setValue(null);
                    progressBar.setVisibility(View.VISIBLE);
                    driverViewModel.getTasks();
                    driverViewModel.getTasksHistory();
                    break;
                case R.id.mainToolbarAccount:
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

        //Refresh Values
        driverViewModel.driverTasks.observe(requireActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks == null){
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        driverViewModel.driverTasksHistory.observe(requireActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks == null){
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}