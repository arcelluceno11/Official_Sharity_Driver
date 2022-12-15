package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.adapter.TasksRecyclerViewAdapter;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;

import java.util.List;

import io.teliver.sdk.models.Task;


public class TaskHistoryFragment extends Fragment {

    //Variables
    DriverViewModel driverViewModel;
    RecyclerView recyclerViewHistory;
    TasksRecyclerViewAdapter tasksRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize Widgets
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);

        //ViewModel
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Populate Tasks
        tasksRecyclerViewAdapter = new TasksRecyclerViewAdapter(requireContext(), driverViewModel.driverTasksHistory.getValue(), new TasksRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(Task task) {
                driverViewModel.taskSelected.setValue(task);

                NavDirections action = HomeFragmentDirections.actionHomeFragmentToTaskFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewHistory.setAdapter(tasksRecyclerViewAdapter);
        driverViewModel.driverTasksHistory.observe(requireActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                tasksRecyclerViewAdapter.setTasksList(tasks);
                tasksRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
}