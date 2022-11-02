package com.capstone.sharity.driver.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class DriverViewModel extends ViewModel {
    public MutableLiveData<Double> latitude = new MutableLiveData<>(new Double(0));
    public MutableLiveData<Double> longitude = new MutableLiveData<>(new Double(0));

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(Activity activity) {

    }
}
