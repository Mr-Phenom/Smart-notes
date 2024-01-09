package com.company.smartnotes.Reciever;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecieverForStopNotification extends BroadcastReceiver {
    int id;
    @Override
    public void onReceive(Context context, Intent intent) {
        id = intent.getIntExtra("idForStopNotification",00);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
