package com.example.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Message
import androidx.navigation.NavController
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel, navController: NavController, modifier: Modifier = Modifier) {
    val messages by viewModel.allMessages.collectAsStateWithLifecycle()
    val activeConv by viewModel.activeConversation.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    
    val chatName = activeConv?.name ?: "Unknown"
    val networkStatus = "Online via ${activeConv?.networkType ?: "WIFI"}"
    val conversationNetwork = activeConv?.networkType ?: "WIFI"

    // Fast & lightweight design
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ChatHeader(name = chatName, status = networkStatus, onBack = { navController.popBackStack() })
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        MessageInput(
            input = input,
            onInputChange = { input = it },
            onSend = {
                viewModel.sendMessage(input, conversationNetwork)
                input = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(name: String, status: String, onBack: () -> Unit) {
    TopAppBar(
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(NeonBlue.copy(alpha=0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().toString(), style = MaterialTheme.typography.titleMedium, color = NeonBlue)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(status, color = NeonCyan, fontSize = 12.sp)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* Call */ }) {
                Icon(Icons.Default.Videocam, contentDescription = "Video Call")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun MessageBubble(message: Message) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isFromMe) NeonBlue else MaterialTheme.colorScheme.surface
    val textColor = if (message.isFromMe) Color.White else MaterialTheme.colorScheme.onBackground
    val shape = if (message.isFromMe) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start) {
            Surface(
                color = bgColor,
                shape = shape,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(message.content, color = textColor, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "12:45 PM",
                        fontSize = 9.sp,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
            
            // Reaction mock
            if (!message.isFromMe) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.offset(x = 16.dp, y = (-12).dp)
                ) {
                    Text("👍", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(input: String, onInputChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Attach */ }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            TextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Message...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)) },
                leadingIcon = { 
                    IconButton(onClick = { /* Stickers */ }) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = "Stickers", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                trailingIcon = {
                    if (input.isEmpty()) {
                        Row {
                            IconButton(onClick = { /* Voice Note */ }) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice Message", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            if (input.isNotEmpty()) {
                IconButton(
                    onClick = onSend,
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonBlue, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            } else {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonCyan, CircleShape)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video Message", tint = Color.Black)
                }
            }
        }
    }
}
