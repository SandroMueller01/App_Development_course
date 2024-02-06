package com.example.griptrainerapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Training.class, Section.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

