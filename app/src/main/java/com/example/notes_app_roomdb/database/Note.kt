package com.example.notes_app_roomdb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)@ColumnInfo(name = "id") val id:Int?,
    @ColumnInfo(name = "title") val title:String?,
    @ColumnInfo(name = "note") val content:String?,
    @ColumnInfo(name = "created_date") val createdDate:String,
    @ColumnInfo(name = "updated_date") val updatedDate: String?,
    @ColumnInfo(name = "color") val color:Int,

    var selected: Boolean = false,
    val isInRecycleBin: Boolean = false

):java.io.Serializable
