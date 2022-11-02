package com.capstone.sharity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.sharity.driver.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

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
    }
}