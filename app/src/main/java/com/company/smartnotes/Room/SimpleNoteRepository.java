package com.company.smartnotes.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleNoteRepository {
    private RoomSimpleNoteDAO dao;
    private LiveData<List<RoomSimpleNotes>> notes;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    public SimpleNoteRepository(Application application)
    {
        SimpleNoteDatabaseHelper databaseHelper = SimpleNoteDatabaseHelper.getDInstance(application);
        dao = databaseHelper.roomSimpleNoteDAO();
        notes=dao.getNotes();
    }

    public void insert(RoomSimpleNotes note)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dao.addNotes(note);
            }
        });
    }

    public void update(RoomSimpleNotes note)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateNotes(note);
            }
        });
    }
    public void delete(RoomSimpleNotes note)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteNotes(note);
            }
        });
    }

    public LiveData<List<RoomSimpleNotes>> getNotes()
    {
        return notes;
    }
}

