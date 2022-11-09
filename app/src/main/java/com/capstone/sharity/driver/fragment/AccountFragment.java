package com.capstone.sharity.driver.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.model.Driver;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

public class AccountFragment extends Fragment {

    //Variables
    DriverViewModel driverViewModel;
    SwitchMaterial switchAvailability;
    TextView textViewCode, textViewName, textViewPhoneNum, textViewEmailAddress, textViewAvailability;
    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar
        NavController navControllerContact = Navigation.findNavController(view);
        MaterialToolbar materialToolbarBulkContact = view.findViewById(R.id.materialToolbarAccount);
        AppBarConfiguration appBarConfigurationBulkContact = new AppBarConfiguration.Builder().setFallbackOnNavigateUpListener(new AppBarConfiguration.OnNavigateUpListener() {
            @Override
            public boolean onNavigateUp() {
                return false;
            }
        }).build();
        NavigationUI.setupWithNavController(materialToolbarBulkContact, navControllerContact, appBarConfigurationBulkContact);

        //Initialize Widgets
        switchAvailability = view.findViewById(R.id.switchAvailability);
        textViewCode = view.findViewById(R.id.textViewCode);
        textViewName = view.findViewById(R.id.textViewName);
        textViewPhoneNum = view.findViewById(R.id.textViewPhoneNum);
        textViewEmailAddress = view.findViewById(R.id.textViewEmailAddress);
        textViewAvailability = view.findViewById(R.id.textViewAvailability);
        btnLogout = view.findViewById(R.id.btnLogout);

        //Initialize ViewModel
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Set Values
        driverViewModel.driver.observe(requireActivity(), new Observer<Driver>() {
            @Override
            public void onChanged(Driver driver) {
                switchAvailability.setChecked(Objects.equals(driver.getStatus(), "Available"));
                textViewCode.setText(driver.getCode());
                textViewName.setText(driver.getFirstName() + " " + driverViewModel.driver.getValue().getMiddleName() + " " + driverViewModel.driver.getValue().getLastName());
                textViewPhoneNum.setText(driver.getPhone());
                textViewEmailAddress.setText(driver.getEmail());
                textViewAvailability.setText(driver.getStatus());
            }
        });

        switchAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                driverViewModel.setAvailability(isChecked);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Session Clear
                SharedPreferences sharedpreferences = requireContext().getSharedPreferences("Driver", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();

                //Set Driver Availability
                driverViewModel.setAvailability(false);

                //Navigate
                NavDirections action = AccountFragmentDirections.actionAccountFragmentToLoginFragment2();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}