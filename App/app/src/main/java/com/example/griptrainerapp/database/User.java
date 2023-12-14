package com.example.griptrainerapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int userId;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "e-mail")
    public String e_mail;

    @ColumnInfo(name= "gender")
    public String gender;

    @ColumnInfo(name = "date-of-birth")
    public String date_of_birth;

}