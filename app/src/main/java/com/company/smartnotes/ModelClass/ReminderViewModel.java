package com.company.smartnotes.ModelClass;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.company.smartnotes.Room.ReminderRepository;
import com.company.smartnotes.Room.RoomReminder;
import com.company.smartnotes.Room.RoomSimpleNotes;

import java.util.List;

public class ReminderViewModel extends AndroidViewModel {

    ReminderRepository repository;
    LiveData<List<RoomReminder>> reminders;
    public ReminderViewModel(@NonNull Application application) {
        super(application);

        repository = new ReminderRepository(application);
        reminders=repository.getAllReminder();
    }

    public void insert(RoomReminder reminder)
    {
        repository.insert(reminder);
    }
    public void update(RoomReminder reminder)
    {
        repository.update(reminder);
    }
    public void delete(RoomReminder reminder)
    {
        repository.delete(reminder);
    }
    public LiveData<List<RoomReminder>> getAllReminders()
    {
        return reminders;
    }
}
