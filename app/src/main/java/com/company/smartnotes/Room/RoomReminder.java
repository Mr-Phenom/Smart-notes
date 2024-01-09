package com.company.smartnotes.Room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminder")
public class RoomReminder {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String time;
    private long notificationTime,selectTime;
    private boolean isNotify,isAlarm;

    public RoomReminder(String title, String description, String time,long notificationTime,long selectTime,boolean isNotify,boolean isAlarm) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.isAlarm=isAlarm;
        this.isNotify = isNotify;
        this.notificationTime=notificationTime;
        this.selectTime=selectTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public long getSelectTime() {
        return selectTime;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public boolean isAlarm() {
        return isAlarm;
    }
}
