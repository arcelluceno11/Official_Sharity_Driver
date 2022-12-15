package com.capstone.sharity.driver.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.example.swipebutton_library.OnActiveListener;
import com.example.swipebutton_library.SwipeButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

import io.teliver.sdk.models.Customer;

public class TaskFragment extends Fragment implements OnMapReadyCallback {

    //Variables
    DriverViewModel driverViewModel;
    MapView mapView;
    Location lastKnownLocation;
    FusedLocationProviderClient fusedLocationClient;
    Customer customer;
    String address;
    Double latitude, longitude;
    CardView cardViewButtons;
    LinearLayout linearLayoutStart, linearLayoutComplete, linearLayoutPickUp;
    TextView textViewTime, textViewName, textViewType, textViewAddress, textViewOrderID;
    ImageButton imgBtnCall, imgBtnDirection;
    SwipeButton swipeStart, swipePickUp,  swipeComplete;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyDrQnBzhOFfjrIqmOUabkt14wvx-LVnzug";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar
        NavController navControllerContact = Navigation.findNavController(view);
        MaterialToolbar materialToolbarBulkContact = view.findViewById(R.id.materialToolbarTask);
        AppBarConfiguration appBarConfigurationBulkContact = new AppBarConfiguration.Builder().setFallbackOnNavigateUpListener(new AppBarConfiguration.OnNavigateUpListener() {
            @Override
            public boolean onNavigateUp() {
                return false;
            }
        }).build();
        NavigationUI.setupWithNavController(materialToolbarBulkContact, navControllerContact, appBarConfigurationBulkContact);

        //Initialize System Services
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //Initialize Map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //Initialize Widgets
        cardViewButtons = view.findViewById(R.id.cardViewButtons);
        linearLayoutStart = view.findViewById(R.id.linearLayoutStart);
        linearLayoutPickUp = view.findViewById(R.id.linearLayoutPickUp);
        linearLayoutComplete = view.findViewById(R.id.linearLayoutComplete);
        textViewOrderID = view.findViewById(R.id.textViewOrderID);
        textViewTime = view.findViewById(R.id.textViewTime);
        textViewName = view.findViewById(R.id.textViewName);
        textViewType = view.findViewById(R.id.textViewType);
        textViewAddress = view.findViewById(R.id.textViewAddress);
        imgBtnCall = view.findViewById(R.id.imgBtnCall);
        imgBtnDirection = view.findViewById(R.id.imgBtnDirection);
        swipeStart = view.findViewById(R.id.swipeStart);
        swipePickUp = view.findViewById(R.id.swipePickUp);
        swipeComplete = view.findViewById(R.id.swipeComplete);

        //Initialize ViewModel
        driverViewModel = new ViewModelProvider(requireActivity()).get(DriverViewModel.class);

        //Check Status
        if(Objects.equals(driverViewModel.taskSelected.getValue().getStatus(), "assigned")){
            linearLayoutStart.setVisibility(View.VISIBLE);
            linearLayoutPickUp.setVisibility(View.GONE);
            linearLayoutComplete.setVisibility(View.GONE);
        } else if (Objects.equals(driverViewModel.taskSelected.getValue().getStatus(), "in_progress")) {
            linearLayoutStart.setVisibility(View.GONE);
            linearLayoutPickUp.setVisibility(View.VISIBLE);
            linearLayoutComplete.setVisibility(View.GONE);
        } else if (Objects.equals(driverViewModel.taskSelected.getValue().getStatus(), "complete")) {
            linearLayoutStart.setVisibility(View.GONE);
            linearLayoutPickUp.setVisibility(View.GONE);
            linearLayoutComplete.setVisibility(View.GONE);
        }

        //Get Task Details
        if(Objects.equals(driverViewModel.taskSelected.getValue().getType(), "1")){
            customer = driverViewModel.taskSelected.getValue().getPickUp().getCustomer();
            address = driverViewModel.taskSelected.getValue().getPickUp().getAddress();
            longitude = driverViewModel.taskSelected.getValue().getPickUp().getLatLongs().get(0);
            latitude = driverViewModel.taskSelected.getValue().getPickUp().getLatLongs().get(1);
        } else {
            linearLayoutPickUp.setVisibility(View.GONE);
            customer = driverViewModel.taskSelected.getValue().getDrop().getCustomer();
            address = driverViewModel.taskSelected.getValue().getDrop().getAddress();
            longitude = driverViewModel.taskSelected.getValue().getDrop().getLatLongs().get(0);
            latitude = driverViewModel.taskSelected.getValue().getDrop().getLatLongs().get(1);
        }

        //Set Details
        textViewOrderID.setText("Order: " + driverViewModel.taskSelected.getValue().getOrderId());
        textViewTime.setText(driverViewModel.taskSelected.getValue().getCreatedAt());
        textViewName.setText(customer.getName());
        textViewType.setText(Objects.equals(driverViewModel.taskSelected.getValue().getType(), "1") ? "Pick Up" : "Drop Off");
        textViewAddress.setText(address);

        //Call Donor
        imgBtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + customer.getMobile()));
                startActivity(intent);
            }
        });

        //Open Google Map
        imgBtnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        //Start the Trip
        swipeStart.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                linearLayoutStart.setVisibility(View.GONE);
                if(Objects.equals(driverViewModel.taskSelected.getValue().getType(), "1")){
                    linearLayoutPickUp.setVisibility(View.VISIBLE);
                    linearLayoutComplete.setVisibility(View.GONE);
                } else {
                    linearLayoutPickUp.setVisibility(View.GONE);
                    linearLayoutComplete.setVisibility(View.VISIBLE);
                }
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                //driverViewModel.startTask(driverViewModel.taskSelected.getValue().getTaskId());
            }
        });

        //Complete PickUp
        swipePickUp.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                linearLayoutStart.setVisibility(View.GONE);
                linearLayoutPickUp.setVisibility(View.GONE);
                linearLayoutComplete.setVisibility(View.VISIBLE);
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                //driverViewModel.completePickUpTask(driverViewModel.taskSelected.getValue().getTaskId());
            }
        });

        //Complete the Trip
        swipeComplete.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                linearLayoutStart.setVisibility(View.GONE);
                linearLayoutPickUp.setVisibility(View.GONE);
                linearLayoutComplete.setVisibility(View.GONE);
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));

                //driverViewModel.completeTask(driverViewModel.taskSelected.getValue().getTaskId());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        //Initialize Map Functions
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });
        map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {

            }
        });

        fusedLocationClient =LocationServices.getFusedLocationProviderClient(requireActivity());
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        //Current Location
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),15));

                        //Task Location
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(latitude, longitude))
                                        .zoom(15)
                                        .bearing(90)
                                        .tilt(30)
                                        .build()));

                        //Add Marker
                        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(address));
                    }
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}