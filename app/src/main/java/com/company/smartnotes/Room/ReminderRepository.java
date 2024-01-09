package com.company.smartnotes.Room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderRepository {

    private RoomReminderDAO daoReminder;

    private LiveData<List<RoomReminder>> reminder;
    ExecutorService executorService =  Executors.newSingleThreadExecutor();



    public ReminderRepository(Application application)
    {
        SimpleNoteDatabaseHelper helper = SimpleNoteDatabaseHelper.getDInstance(application);
        daoReminder = helper.roomReminderDAO();
        reminder = daoReminder.getReminders();

    }



    public void insert(RoomReminder reminder)
    {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoReminder.addReminder(reminder);
            }
        });

    }
    public void update(RoomReminder reminder)
    {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoReminder.updateReminder(reminder);
            }
        });
    }
    public void delete(RoomReminder reminder)
    {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoReminder.deleteReminder(reminder);
            }
        });
    }

    public LiveData<List<RoomReminder>> getAllReminder()
    {
        return reminder;
    }
}
