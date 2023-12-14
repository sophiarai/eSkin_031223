package com.example.mediscanner_firebase.Sophia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationText = intent.getStringExtra("notificationText");
        //showNotification(context, notificationText);
    }

   /* private void showNotification(Context context, String text) {
        // Hier können Sie den Code für die Benachrichtigung erstellen und anzeigen
        // Beispiel:
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Medikamentenerinnerung")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }*/
}

