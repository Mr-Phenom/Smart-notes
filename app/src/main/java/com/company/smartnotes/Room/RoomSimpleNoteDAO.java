package com.company.smartnotes.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomSimpleNoteDAO {

    @Query("select * from notes order by id asc")
    LiveData<List<RoomSimpleNotes>> getNotes();

    @Insert
    void addNotes(RoomSimpleNotes note);

    @Update
    void updateNotes(RoomSimpleNotes note);

    @Delete
    void deleteNotes(RoomSimpleNotes note);

}

