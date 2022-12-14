package com.capstone.sharity.driver.repository;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Notification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {

            //Create Notification Channel
            NotificationChannel channel = new NotificationChannel("Assigned Orders","Assigned Orders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("To");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Explicit Intent from Notification
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("orderID", message.getData().get("orderID"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            //Build Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Assigned Orders")
                    .setSmallIcon(R.drawable.ic_baseline_assignment_turned_in_24)
                    .setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //Show Notification
            notificationManager.notify(0, builder.build());

            Log.d("Log Debug:", "Notification Received");
        }
    }
}
