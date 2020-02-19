package com.example.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//@Database(entities = {Task.class}, version = 1)
@androidx.room.Database(entities = {Task.class}, exportSchema = false, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
