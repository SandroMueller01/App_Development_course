package com.example.griptrainerapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trainings")
public class Training {
    @PrimaryKey(autoGenerate = true)
    public int trainingId;

    @ColumnInfo(name = "training_name")
    public String trainingName;

    @ColumnInfo(name = "date_time")
    public String dateTime; // Consider using a more appropriate type like long to store timestamps

    @ColumnInfo(name = "user_id")
    public int userId; // Foreign key linking to the User entity
}
