package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class, Conversation::class], version = 2, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
}
