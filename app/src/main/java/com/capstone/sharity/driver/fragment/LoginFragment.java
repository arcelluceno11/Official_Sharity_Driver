package com.capstone.sharity.driver.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.model.Driver;
import com.capstone.sharity.driver.repository.Firebase;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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
    ProgressBar progressBar;
    Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Check Internet Connection
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = conMgr.getNetworkCapabilities(conMgr.getActiveNetwork());
        if(capabilities == null || (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))){
            new AlertDialog.Builder(getContext()).setMessage("No Internet Connection")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        }

        //Initialize Variables
        editTextLoginCode = view.findViewById(R.id.editTextLoginCode);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.progressBar);

        //Initialize Teliver
        Teliver.init(requireContext(), "7537696e87db9f4ffb2c9e28a5fd51ea");
        TLog.setVisible(true);

        //ViewModel Initialization
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Session Initialization
        sharedpreferences = requireContext().getSharedPreferences("Driver", Context.MODE_PRIVATE);

        //Check if Already Login
        code = sharedpreferences.getString("code", null);
        if (code != null) {
            //Set ViewModel
            driverViewModel.driverCode.setValue(code);

            //Identify Teliver User
            Teliver.identifyUser(new UserBuilder(driverViewModel.driverCode.getValue())
                    .setUserType(UserBuilder.USER_TYPE.OPERATOR)
                    .setEmail(driverViewModel.driver.getValue().getEmail())
                    .setName(driverViewModel.driver.getValue().getFirstName())
                    .setPhone(driverViewModel.driver.getValue().getPhone())
                    .registerPush().build());

            //Navigate
            NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
            Navigation.findNavController(view).navigate(action);
        }
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

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
                                    //Set ViewModel
                                    driverViewModel.driverCode.setValue(editTextLoginCode.getText().toString());

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    //Identify Teliver User
                                    Teliver.identifyUser(new UserBuilder(editTextLoginCode.getText().toString())
                                            .setUserType(UserBuilder.USER_TYPE.OPERATOR)
                                            .setEmail(driverViewModel.driver.getValue().getEmail())
                                            .setName(driverViewModel.driver.getValue().getFirstName())
                                            .setPhone(driverViewModel.driver.getValue().getPhone())
                                            .registerPush().build());

                                    //Session Save
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("code", editTextLoginCode.getText().toString());
                                    editor.apply();

                                    //Navigate
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireContext(), "Login successfully!", Toast.LENGTH_SHORT).show();
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