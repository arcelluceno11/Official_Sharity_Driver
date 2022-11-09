package com.capstone.sharity.driver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.os.Bundle;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;

import io.teliver.sdk.core.Teliver;

public class MainActivity extends AppCompatActivity {

    //Variables
    DriverViewModel driverViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);

        //Request Permission
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                1);

    }
}