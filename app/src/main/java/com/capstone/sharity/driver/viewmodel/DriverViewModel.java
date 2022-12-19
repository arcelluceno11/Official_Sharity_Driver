package com.capstone.sharity.driver.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
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

        Log.d("Log Debug:", "Driver Details: Success" );
    }
    public void getTasks(){
        Teliver.getMyTaskList("assigned,accepted,in_progress", 1, 0, new TaskListListener() {
            @Override
            public void onSuccess(List<Task> tasks, int total) {
                driverTasks.setValue(tasks);
                Log.d("Log Debug:", "Driver Tasks: " + total);
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
                Log.d("Log Debug:", "Driver History: " + total);
            }

            @Override
            public void onFailure(String reason) {
                TLog.log(reason);
            }
        });
    }
    public void setAvailability(){
        String status = "Available";

        //Update Availability in Realtime Database
        Firebase.getDatabaseReference()
                .child("Drivers")
                .child(driverCode.getValue())
                .child("status")
                .setValue(status);

        Log.d("Log Debug:", "Driver Available: True");
    }
    public void startTask(String taskId) {

        driverTasks.setValue(null);
        driverTasksHistory.setValue(null);

        //Accept Task
        Teliver.acceptTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("Log Debug:", response);
            }

            @Override
            public void onFailure(String reason) {

            }

        });

        //Start Task
        Teliver.startTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {
                getTasks();
                getTasksHistory();
            }

            @Override
            public void onFailure(String reason) {
                Log.d("Log Debug:", reason);
            }
        });

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
                                        .child(taskSelected.getValue().getOrderId())
                                        .child("status")
                                        .setValue("On the Way");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Start Trip
        Teliver.startTrip(new TripBuilder(taskSelected.getValue().getOrderId()).build());

        //Send Notification
        try {
            Firebase.sendNotification(taskSelected.getValue().getNotes(), taskSelected.getValue().getOrderId() + " is On the Way!", "Track here", "On the Way", taskSelected.getValue().getOrderId());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void completeTask(String taskId) {

        driverTasks.setValue(null);
        driverTasksHistory.setValue(null);

        //Complete Task
        Teliver.completeTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("Log Debug:", response);
                getTasks();
                getTasksHistory();
            }

            @Override
            public void onFailure(String reason) {
                Log.d("Log Debug:", reason);
            }
        });

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
                                        .child(taskSelected.getValue().getOrderId())
                                        .child("status")
                                        .setValue(Objects.equals(taskSelected.getValue().getType(), "1") ? "Complete" : "Delivered");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Stop Trip
        Teliver.stopTrip(taskSelected.getValue().getOrderId());

        //Send Notification
        try {
            Firebase.sendNotification(taskSelected.getValue().getNotes(), taskSelected.getValue().getOrderId() + " is Complete", "", Objects.equals(taskSelected.getValue().getType(), "1") ? "Complete" : "Delivered" , taskSelected.getValue().getOrderId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void completePickUpTask(String taskId){

        driverTasks.setValue(null);
        driverTasksHistory.setValue(null);

        //Complete Pick Up Task
        Teliver.completePickupTask(taskId, new EventListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("Log Debug:", response);
                getTasks();
                getTasksHistory();
            }

            @Override
            public void onFailure(String reason) {
                Log.d("Log Debug:", reason);
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
                                        .child(taskSelected.getValue().getOrderId())
                                        .child("status")
                                        .setValue("Picked Up");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Send Notification
        try {
            Firebase.sendNotification(taskSelected.getValue().getNotes(), taskSelected.getValue().getOrderId() + " is Picked", "Track here", "Picked Up", taskSelected.getValue().getOrderId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void subscribeUpdates(){
        FirebaseMessaging.getInstance().subscribeToTopic(driverCode.getValue());
        Log.d("Log Debug:", "Driver Subscribed To Topic: " + driverCode.getValue().toString());
    }
    public void unsubscribeUpdates(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(driverCode.getValue());
        Log.d("Log Debug:", "Driver Unsubscribed To Topic: " + driverCode.getValue().toString());
    }
}
