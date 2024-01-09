package com.company.smartnotes.Reciever;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.company.smartnotes.Activities.MainActivity;
import com.company.smartnotes.R;


public class RecieverForReminderNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String getTitle = intent.getStringExtra("titleForReciever");
        String getDescription = intent.getStringExtra("descriptionForReciever");
        String getTime = intent.getStringExtra("timeForReciever");
        int getId = intent.getIntExtra("idForReciever",0);

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire(10000);
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if(vibrator!=null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{700,700,700,700,700},VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else
            {
                vibrator.vibrate(700);
            }
        }

        setNotification(context,getId,getTitle,getDescription,getTime);



    }

    public void setNotification(Context context,int id,String getTitle,String getDescription,String getTime)
    {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.reminder,null);
        Bitmap largeIcon = bitmapDrawable.getBitmap();
        Notification notification;

        Intent intentNotify = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,id,intentNotify,PendingIntent.FLAG_IMMUTABLE);


        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle()
                .bigLargeIcon(largeIcon)
                .setBigContentTitle(getTitle + " at " + getTime)
                .setSummaryText(getDescription);

        NotificationManager notificationManager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText(getTitle + " at " + getTime)
                    .setSubText("Reminder")
                    .setContentIntent(pendingIntent)
                    .setStyle(bigPictureStyle)
                    .setOngoing(true)
                    .setChannelId("Reminder Notification")
                    .build();
            notificationManager.createNotificationChannel(new NotificationChannel("Reminder Notification","Reminder",NotificationManager.IMPORTANCE_HIGH));

        }
        else
        {
            notification = new Notification.Builder(context)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText(getTitle + " at " + getTime)
                    .setSubText("Reminder")
                    .setContentIntent(pendingIntent)
                    .setStyle(bigPictureStyle)
                    .setOngoing(true)
                    .build();
        }
        notificationManager.notify(id,notification);
    }




}
