package com.capstone.sharity.driver.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.model.Driver;
import com.capstone.sharity.driver.repository.Firebase;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.teliver.sdk.core.TLog;
import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.Task;
import io.teliver.sdk.models.UserBuilder;

public class LoginFragment extends Fragment {

    //Variables
    DriverViewModel driverViewModel;
    SharedPreferences sharedpreferences;
    String code;
    EditText editTextLoginCode;
    Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize Variables
        editTextLoginCode = view.findViewById(R.id.editTextLoginCode);
        btnLogin = view.findViewById(R.id.btnLogin);

        //Initialize Teliver
        Teliver.init(requireContext(), "3e02ba274b5fee70ce4405e618dd3117");

        //ViewModel Initialization
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Session Initialization
        sharedpreferences = requireContext().getSharedPreferences("Driver", Context.MODE_PRIVATE);

        //Check if Already Login
        code = sharedpreferences.getString("code", null);
        if (code != null) {
            //Set ViewModel
            driverViewModel.driverCode.setValue(code);
            driverViewModel.getDriverDetails();
            driverViewModel.setAvailability();
            driverViewModel.subscribeUpdates();
            driverViewModel.getTasks();
            driverViewModel.getTasksHistory();

            //Identify Teliver User
            Teliver.identifyUser(new UserBuilder(driverViewModel.driverCode.getValue()).setUserType(UserBuilder.USER_TYPE.OPERATOR).registerPush().build());

            NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
            Navigation.findNavController(view).navigate(action);
        }
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.getDatabaseReference()
                        .child("Drivers")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Driver> tempDrivers = new ArrayList<>();

                                //Iterate Donations
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    tempDrivers.add(postSnapshot.getValue(Driver.class));
                                }

                                //Check if Code Exist
                                if(tempDrivers.stream().anyMatch(x -> x.getCode().equals(editTextLoginCode.getText().toString()))){
                                    Toast.makeText(requireContext(), "Login successfully!", Toast.LENGTH_SHORT).show();

                                    //Set ViewModel
                                    driverViewModel.driverCode.setValue(editTextLoginCode.getText().toString());
                                    driverViewModel.getDriverDetails();
                                    driverViewModel.setAvailability();
                                    driverViewModel.subscribeUpdates();
                                    driverViewModel.getTasks();
                                    driverViewModel.getTasksHistory();

                                    //Identify Teliver User
                                    Teliver.identifyUser(new UserBuilder(driverViewModel.driverCode.getValue()).setUserType(UserBuilder.USER_TYPE.OPERATOR).registerPush().build());

                                    //Session Save
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("code", editTextLoginCode.getText().toString());
                                    editor.apply();

                                    //Navigate
                                    NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
                                    Navigation.findNavController(view).navigate(action);

                                } else {
                                    Toast.makeText(requireContext(), "Code doesn't exist!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

}