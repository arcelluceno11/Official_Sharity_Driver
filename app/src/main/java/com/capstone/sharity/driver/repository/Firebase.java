package com.capstone.sharity.driver.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {

    //Firebase
    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference databaseReference = firebaseDatabase.getReference();

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
