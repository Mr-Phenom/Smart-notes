package com.company.smartnotes.Reciever;

import static com.company.smartnotes.Reciever.RecieverForAlarm.stopAlarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecieverForStopAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("idSentFromReciever",00);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        stopAlarm();
    }
}
