package com.capstone.sharity.driver.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.repository.Firebase;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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

        //ViewModel Initialization
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Session Initialization
        sharedpreferences = requireContext().getSharedPreferences("Driver", Context.MODE_PRIVATE);
        code = sharedpreferences.getString("code", null);
        if (code != null) {
            //Set ViewModel driverCode
            driverViewModel.driverCode.setValue(code);

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
                                //Iterate Donations
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    if (Objects.equals(postSnapshot.getKey(), editTextLoginCode.getText().toString())) {
                                        Toast.makeText(requireContext(), "Login successfully!", Toast.LENGTH_SHORT).show();

                                        //Set ViewModel driverCode
                                        driverViewModel.driverCode.setValue(editTextLoginCode.getText().toString());

                                        //Session Save
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("code", editTextLoginCode.getText().toString());
                                        editor.apply();

                                        //Navigate
                                        NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
                                        Navigation.findNavController(view).navigate(action);
                                    } else {
                                        Toast.makeText(requireContext(), "Code does'nt exist!", Toast.LENGTH_SHORT).show();
                                    }
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