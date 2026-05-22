package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.data.ChatDatabase
import com.example.data.ChatRepository
import com.example.ui.chat.ChatViewModel
import com.example.ui.main.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    try {
        // Initialize Database & Repository
        val database = Room.databaseBuilder(
            applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()
        
        val repository = ChatRepository(database.messageDao(), database.conversationDao())
        val chatViewModel = ChatViewModel(repository)

        enableEdgeToEdge()
        setContent {
          MyApplicationTheme(dynamicColor = false) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                MainScreen(chatViewModel = chatViewModel)
            }
          }
        }
    } catch (e: Throwable) {
        val trace = android.util.Log.getStackTraceString(e)
        android.util.Log.e("APP_CRASH", "Crash caught in Activity", e)
        enableEdgeToEdge()
        setContent {
            Box(modifier = Modifier.fillMaxSize().background(Color.Red)) {
                Text(
                    text = "CRASH: $trace",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
  }
}
