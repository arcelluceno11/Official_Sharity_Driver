package com.capstone.sharity.driver.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.capstone.sharity.driver.fragment.LoginFragmentDirections;
import com.capstone.sharity.driver.model.Driver;
import com.capstone.sharity.driver.repository.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import io.teliver.sdk.core.EventListener;
import io.teliver.sdk.core.TLog;
import io.teliver.sdk.core.TaskListListener;
import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.PushData;
import io.teliver.sdk.models.Task;
import io.teliver.sdk.models.TripBuilder;

public class DriverViewModel extends ViewModel {
    public MutableLiveData<String> driverCode = new MutableLiveData<>();
    public MutableLiveData<Driver> driver = new MutableLiveData<>(new Driver());
    public MutableLiveData<List<Task>> driverTasks = new MutableLiveData<>();
    public MutableLiveData<List<Task>> driverTasksHistory = new MutableLiveData<>();
    public MutableLiveData<Task> taskSelected = new MutableLiveData<>(new Task());

    public void getDriverDetails(){
        Firebase.getDatabaseReference()
                .child("Drivers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(postSnapshot.child("code").getValue(String.class), driverCode.getValue())) {
                                driver.setValue(postSnapshot.getValue(Driver.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public void getTasks(){
        Teliver.getMyTaskList("assigned,accepted,in_progress", 1, 0, new TaskListListener() {
            @Override
            public void onSuccess(List<Task> tasks, int total) {
                driverTasks.setValue(tasks);
            }

            @Override
            public void onFailure(String reason) {
                TLog.log(reason);
            }
        });
    }
    public void getTasksHistory(){
        Teliver.getMyTaskList("completed", 1, 0, new TaskListListener() {
            @Override
            public void onSuccess(List<Task> tasks, int total) {
                driverTasksHistory.setValue(tasks);
            }

            @Override
            public void onFailure(String reason) {
                TLog.log(reason);
            }
        });
    }
    public void setAvailability(Boolean value){
        String status = "Available";

        if(!value) {
            status = "Unavailable";
        }

        Firebase.getDatabaseReference()
                .child("Drivers")
                .child(driverCode.getValue())
                .child("status")
                .setValue(status);
        Teliver.updateDriverAvailability(value);
    }
    public void startTask(String taskId) {

        Teliver.acceptTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFailure(String reason) {

            }

        });

        Teliver.startTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFailure(String reason) {

            }
        });

        //Start Teliver Trip
        TripBuilder tripBuilder = new TripBuilder(taskSelected.getValue().getOrderId());

        /*
        PushData pushData = new PushData(taskSelected.getValue().getNotes());
        pushData.setMessage("Your Order: " + taskSelected.getValue().getOrderId() + "is On the Way!");
        pushData.setPayload();
        tripBuilder.withUserPushObject(pushData);
         */

        Teliver.startTrip(tripBuilder.build());

        //Update Order Status
        Firebase.getDatabaseReference()
                .child(Objects.equals(taskSelected.getValue().getType(), "1") ? "Donations" : "Purchases")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(postSnapshot.child("id").getValue(String.class), taskSelected.getValue().getOrderId())) {
                                Firebase.getDatabaseReference()
                                        .child(Objects.equals(taskSelected.getValue().getType(), "1") ? "Donations" : "Purchases")
                                        .child(postSnapshot.child("id").getValue(String.class))
                                        .child("status")
                                        .setValue("On the Way");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
    public void completeTask(String taskId) {
        Teliver.completeTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {


            }

            @Override
            public void onFailure(String reason) {

            }
        });

        Teliver.stopTrip(taskSelected.getValue().getOrderId());

        //Update Order Status
        Firebase.getDatabaseReference()
                .child(Objects.equals(taskSelected.getValue().getType(), "1") ? "Donations" : "Purchases")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(postSnapshot.child("id").getValue(String.class), taskSelected.getValue().getOrderId())) {
                                Firebase.getDatabaseReference()
                                        .child(Objects.equals(taskSelected.getValue().getType(), "1") ? "Donations" : "Purchases")
                                        .child(postSnapshot.child("id").getValue(String.class))
                                        .child("status")
                                        .setValue(Objects.equals(taskSelected.getValue().getType(), "1") ? "Complete" : "Delivered");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public void completePickUpTask(String taskId){
        Teliver.completePickupTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFailure(String reason) {

            }
        });

        //Update Order Status
        Firebase.getDatabaseReference()
                .child("Donations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(postSnapshot.child("id").getValue(String.class), taskSelected.getValue().getOrderId())) {
                                Firebase.getDatabaseReference()
                                        .child("Donations")
                                        .child(postSnapshot.child("id").getValue(String.class))
                                        .child("status")
                                        .setValue("Picked Up");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
