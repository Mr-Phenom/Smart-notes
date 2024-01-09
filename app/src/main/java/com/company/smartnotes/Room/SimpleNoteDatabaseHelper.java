package com.company.smartnotes.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RoomSimpleNotes.class,RoomReminder.class},version = 4)
public abstract class SimpleNoteDatabaseHelper extends RoomDatabase {

    public abstract RoomSimpleNoteDAO roomSimpleNoteDAO();
    public abstract RoomReminderDAO roomReminderDAO();
    private static SimpleNoteDatabaseHelper instance;

    public static synchronized SimpleNoteDatabaseHelper getDInstance(Context context)
    {
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),
                            SimpleNoteDatabaseHelper.class,"note_database")
                    .fallbackToDestructiveMigration().allowMainThreadQueries().addCallback(roomCallback).build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            RoomReminderDAO daoReminder = instance.roomReminderDAO();

            RoomSimpleNoteDAO dao = instance.roomSimpleNoteDAO();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    RoomSimpleNotes n = new RoomSimpleNotes("Demo","You are Great and Loved.\nDon't Give up.\nRise High King.");
                    dao.addNotes(n);

//                   RoomReminder r = new RoomReminder("Demo","ExamDemo","Date: 30 December 2023");
//                    daoReminder.addReminder(r);

                }
            });
        }
    };



}

