package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String = "PRIVATE", // PRIVATE, GROUP, CHANNEL
    val networkType: String = "WIFI", // WIFI, HOTSPOT, BLUETOOTH
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
