package com.company.smartnotes.ModelClass;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.company.smartnotes.Room.RoomSimpleNotes;
import com.company.smartnotes.Room.SimpleNoteRepository;

import java.util.List;

public class SImpleNoteViewModel extends AndroidViewModel {

    private SimpleNoteRepository repository;
    private LiveData<List<RoomSimpleNotes>> notes;
    public SImpleNoteViewModel(@NonNull Application application) {
        super(application);

        repository = new SimpleNoteRepository(application);
        notes=repository.getNotes();
    }

    public void insert(RoomSimpleNotes note)
    {
        repository.insert(note);
    }
    public void update(RoomSimpleNotes note)
    {
        repository.update(note);
    }
    public void delete(RoomSimpleNotes note)
    {
        repository.delete(note);
    }
    public LiveData<List<RoomSimpleNotes>> getAllnotes()
    {
        return notes;
    }


}
