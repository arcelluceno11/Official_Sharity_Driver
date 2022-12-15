package com.capstone.sharity.driver.repository;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Firebase {

    //Firebase
    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference databaseReference = firebaseDatabase.getReference();

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public static void sendNotification(String topicID, String title, String body ,String status, String orderID) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String json = "{\n" +
                "  \"to\": \"/topics/" + topicID + "\",\n" +
                "  \"notification\": {\n" +
                "      \"title\": \"" + title + "\",\n" +
                "      \"body\": \""+ body +"\"\n" +
                "  },\n" +
                "  \"data\": {\n" +
                "      \"status\": \"" + status + "\",\n" +
                "      \"orderID\": \"" + orderID + "\"\n" +
                "  }\n" +
                "}";

        //Build Request
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", "key=AAAAi6cWnAI:APA91bHOYyb923ZMD50-LOMMqqUcygdRbP9t7YsyACQnDxiyZJbDzJpMRYyC8QAV6V6r8ycj6uyv3hSvvpSI6qSsYZLTJJRVWAlym1ul5YL_vp1kzgysK1VRA3YXpENhgkQ_0vf6z7xb")
                .post(requestBody)
                .build();

        //Execute Request
        client.newCall(request).execute();

    }
}
