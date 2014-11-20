package com.saracraba.garageradio;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

/**
 * Created by Sara Craba.
 *
 * Build and menage a notification that recall a single instanced Activity
 */
public class NotificationManager {

    private static android.app.NotificationManager notificationManager;
    private final Activity mainActivity;

    NotificationManager(Activity mainActivity)
    {
        if (mainActivity== null)
        {
            throw  new NullPointerException();
        }

        this.mainActivity= mainActivity;

        //getting system notification service
        notificationManager= (android.app.NotificationManager) this.mainActivity.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    private final void startNotification()
    {
        Intent intent = new Intent(mainActivity, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mainActivity, 0, intent, 0);

        // build notification
        Notification n  = new Notification.Builder(mainActivity)
                .setContentTitle("Garage Radio")
                .setContentText("La Radio che ti sorprende ti sta tenendo compagnia.")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent).build();

        //fire notification
        notificationManager.notify(0, n);
    }

    private final void stopNotification()
    {
        notificationManager.cancelAll();
    }

    public void start()
    {
        startNotification();
    }

    public void stop()
    {
        stopNotification();
    }

}
