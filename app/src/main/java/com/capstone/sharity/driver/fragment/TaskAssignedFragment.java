package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.adapter.TasksRecyclerViewAdapter;
import com.capstone.sharity.driver.viewmodel.TasksViewModel;

import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.TripBuilder;

public class TaskAssignedFragment extends Fragment {

    //Variables
    TasksViewModel tasksViewModel;
    RecyclerView recyclerViewTasks;
    TextView textViewStatus, textViewType, textViewTime, textViewName, textviewAddress;
    TasksRecyclerViewAdapter tasksRecyclerViewAdapter;
    Button btnView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_assigned, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize Widgets
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        textViewStatus= view.findViewById(R.id.textViewStatus);
        textViewType= view.findViewById(R.id.textViewType);
        textViewTime= view.findViewById(R.id.textViewTime);
        textViewName= view.findViewById(R.id.textViewName);
        textviewAddress= view.findViewById(R.id.textViewAddress);

        //ViewModel
        //tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        //tasksViewModel.getTasks();

        //Populate Tasks
        /*TasksRecyclerViewAdapter = new TasksRecyclerViewAdapter(requireContext(), tasksViewModel.tasks.getValue(), new TasksRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(Tasks tasks) {
                tasksViewModel.deleteCartProduct(tasks);
                tasksRecyclerViewAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewTasks.setAdapter(tasksRecyclerViewAdapter);
        tasksViewModel.tasks.observe(requireActivity(), new Observer<ArrayList<Tasks>>() {
            @Override
            public void onChanged(ArrayList<Tasks> tasks) {
                tasksRecyclerViewAdapter.setTasksArrayList(tasks);
                tasksRecyclerViewAdapter.notifyDataSetChanged();
            }
        });*/

        btnView = view.findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teliver.init(requireContext(),"7537696e87db9f4ffb2c9e28a5fd51ea");
                Teliver.startTrip(new TripBuilder("Tracking_Id").build());
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToTaskFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}