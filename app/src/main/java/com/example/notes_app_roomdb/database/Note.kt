package com.example.notes_app_roomdb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)val id:Int?,
    @ColumnInfo(name = "title")val title:String?,
    @ColumnInfo(name = "note")val content:String?,
    @ColumnInfo(name = "date")val date:String,
):java.io.Serializable
