package com.example.griptrainerapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sections")
public class Section {
    @PrimaryKey(autoGenerate = true)
    public int sectionId;

    @ColumnInfo(name = "steps")
    public int steps;

    @ColumnInfo(name = "dec_len")
    public int decLen;

    @ColumnInfo(name = "training_id")
    public int trainingId; // Foreign key linking to the Training entity
}

