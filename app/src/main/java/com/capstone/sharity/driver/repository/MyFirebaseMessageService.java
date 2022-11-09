package com.capstone.sharity.driver.repository;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;

import java.util.Map;

import io.teliver.sdk.core.Teliver;
import io.teliver.sdk.models.NotificationData;
import io.teliver.sdk.models.TConstants;
import io.teliver.sdk.models.Task;
import io.teliver.sdk.models.TrackingBuilder;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        try {
            if (Teliver.isTeliverPush(message)) {
                Map<String, String> pushData = message.getData();
                NotificationData data = new GsonBuilder().create().fromJson(pushData.get("description"),  NotificationData.class);

                if (data.getCommand().equals(TConstants.TELIVER_ASSIGN_TASK)) {
                    Task task = new GsonBuilder().create().fromJson(data.getPayload(), Task.class);

                    Intent notificationIntent = new Intent(this, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationChannel channel = new NotificationChannel("Driver", "Driver", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("Driver");
                    notificationManager.createNotificationChannel(channel);

                    NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, "Driver");
                    notBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                    notBuilder.setContentIntent(pendingIntent);
                    notBuilder.setSmallIcon(R.drawable.ic_baseline_assignment_turned_in_24);
                    notBuilder.setContentTitle("New Task");
                    notBuilder.setContentText(task.getType());
                    notBuilder.setAutoCancel(true);
                    notBuilder.setOnlyAlertOnce(true);
                    Notification notification = notBuilder.build();
                    notification.defaults = Notification.DEFAULT_SOUND;
                    notification.flags = Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(7, notification);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
