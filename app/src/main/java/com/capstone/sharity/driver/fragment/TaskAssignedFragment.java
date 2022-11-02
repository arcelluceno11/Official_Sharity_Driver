package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.capstone.sharity.driver.R;

import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.TripBuilder;

public class TaskAssignedFragment extends Fragment {

    //Variables
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_assigned, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.task);
        button.setOnClickListener(new View.OnClickListener() {
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