package com.company.smartnotes.Reciever;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_MANAGER_SERVICE;
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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.company.smartnotes.Activities.MainActivity;
import com.company.smartnotes.R;

public class RecieverForAlarm extends BroadcastReceiver {

    public static MediaPlayer mp;
    public static PowerManager powerManager;
    public static PowerManager.WakeLock wakeLock;
    public static Vibrator vibrator1;
    public static PowerManager.WakeLock wl_cpu;

    @Override
    public void onReceive(Context context, Intent intent) {
        String getTitle = intent.getStringExtra("titleForReciever2");
        String getDescription = intent.getStringExtra("descriptionForReciever2");
        String getTime = intent.getStringExtra("timeForReciever2");
        int id = intent.getIntExtra("idForReciever2",0);


        setAlarm(context,id,getTitle,getDescription,getTime);
    }

    public void setAlarm(Context context,int id,String getTitle,String getDescription,String getTime)
    {

        powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();
        if(isScreenOn==false)
        {
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wakeLock.acquire();
            wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(VIBRATOR_MANAGER_SERVICE);
            vibrator1=vibratorManager.getDefaultVibrator();
        }
        else
        {
            vibrator1=(Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator1.vibrate(VibrationEffect.createWaveform(new long[] {0,1000,1000},0));
        }
        else
        {
            vibrator1.vibrate(new long[] {0,1000,1000},0);
        }


//        vibrator1 = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
//        if(vibrator1!=null)
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                vibrator1.vibrate(VibrationEffect.createWaveform(new long[]{700,700,700,700,700},0));
//            }
//            else
//            {
//                vibrator1.vibrate(700);
//            }
//        }

        setNotification(context,id,getTitle,getDescription,getTime);
       mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mp.setLooping(true);
        mp.start();



    }

    public void setNotification(Context context,int id,String getTitle,String getDescription,String getTime)
    {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.reminder,null);
        Bitmap largeIcon = bitmapDrawable.getBitmap();
        Notification notification;

        Intent intent = new Intent(context.getApplicationContext(), RecieverForStopAlarm.class);
        intent.putExtra("idSentFromReciever",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_IMMUTABLE);


        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle()
                .bigLargeIcon(largeIcon)
                .setBigContentTitle(getTitle)
                .setSummaryText(getDescription);

        NotificationManager notificationManager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText(getTitle + " at " + getTime)
                    .setSubText("Reminder")
                    .addAction(R.drawable.alarm_reminder,"Stop Alarm",pendingIntent)
                    .setStyle(bigPictureStyle)
                    .setOngoing(true)
                    .setChannelId("Alarm Notification")
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
            notificationManager.createNotificationChannel(new NotificationChannel("Alarm Notification","Alarm",NotificationManager.IMPORTANCE_HIGH));


        }
        else
        {
            notification = new Notification.Builder(context)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText(getTitle + " at " + getTime)
                    .setSubText("Reminder")
                    .addAction(R.drawable.alarm_reminder,"Stop Alarm",pendingIntent)
                    .setStyle(bigPictureStyle)
                    .setOngoing(true)
                    .build();
        }
        notificationManager.notify(id,notification);
    }
    public static void stopAlarm()
    {
        mp.stop();
        vibrator1.cancel();
        if(wl_cpu!=null && wakeLock!=null)
        {
            wl_cpu.release();
            wakeLock.release();
        }


    }
}
