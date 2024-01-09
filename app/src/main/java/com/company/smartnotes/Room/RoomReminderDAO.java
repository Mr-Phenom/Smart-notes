package com.company.smartnotes.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomReminderDAO {

    @Query("select * from reminder order by id asc")
    LiveData<List<RoomReminder>> getReminders();

    @Insert
    void addReminder(RoomReminder reminder);
    @Update
    void updateReminder(RoomReminder reminder);
    @Delete
    void deleteReminder(RoomReminder reminder);
}
