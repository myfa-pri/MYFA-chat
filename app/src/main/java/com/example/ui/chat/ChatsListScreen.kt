package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsListScreen(navController: NavController, viewModel: ChatViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Private", "Groups", "Channels")
    
    val allConversations by viewModel.allConversations.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Myfa Chat", fontWeight = FontWeight.Bold, color = NeonBlue) },
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Users")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NeonBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            
            // IG-like Notes Feature
            Text(
                text = "Notes", 
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { NoteItem(name = "Your Note", isAdd = true) }
                // Only show real active notes in the future if implemented
                if (allConversations.isNotEmpty()) {
                    val recent = allConversations.take(3)
                    items(recent.size) { index -> 
                        NoteItem(name = recent[index].name, isAdd = false) 
                    }
                }
            }
            
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Tabs for Private | Groups | Channels
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if(selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Chat List
            val currentType = when(selectedTab) {
                0 -> "PRIVATE"
                1 -> "GROUP"
                else -> "CHANNEL"
            }
            val filteredChats = allConversations.filter { it.type == currentType }
            
            if (filteredChats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No chats here yet. Tap + to start one!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredChats.size) { index ->
                        val chat = filteredChats[index]
                        ChatListItem(
                            name = chat.name,
                            lastMessage = chat.lastMessage.ifBlank { "No messages yet" },
                            time = "Now", // In a real app we'd format timestamp
                            unreadCount = 0,
                            onClick = { 
                                viewModel.setActiveConversation(chat.id)
                                navController.navigate("chat_detail") 
                            }
                        )
                    }
                }
            }
        }
        
        if (showCreateDialog) {
            CreateChatDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, type, network ->
                    viewModel.createConversation(name, type, network) { id ->
                        showCreateDialog = false
                        viewModel.setActiveConversation(id)
                        navController.navigate("chat_detail")
                    }
                }
            )
        }
        
        if (showSearchDialog) {
            SearchPeerDialog(
                onDismiss = { showSearchDialog = false },
                onPeerFound = { username -> 
                    // Simulate finding the user and creating a private chat.
                    viewModel.createConversation(
                        name = username,
                        type = "PRIVATE",
                        networkType = "BLUETOOTH"
                    ) { id ->
                        showSearchDialog = false
                        viewModel.setActiveConversation(id)
                        navController.navigate("chat_detail")
                    }
                }
            )
        }
    }
}

@Composable
fun SearchPeerDialog(onDismiss: () -> Unit, onPeerFound: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Discover Nearby Peers") },
        text = {
            Column {
                Text("Search by username over active Mesh (Bluetooth / Hotspot / WiFi):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("@username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSearching) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Scanning Mesh Network...", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (searchQuery.isNotBlank()) {
                        isSearching = true
                        // Simulate network delay
                        onPeerFound(searchQuery)
                    }
                }
            ) {
                Text("Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateChatDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("PRIVATE") }
    var network by remember { mutableStateOf("WIFI") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Conversation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "PRIVATE", onClick = { type = "PRIVATE" }, label = { Text("Private") })
                    FilterChip(selected = type == "GROUP", onClick = { type = "GROUP" }, label = { Text("Group") })
                    FilterChip(selected = type == "CHANNEL", onClick = { type = "CHANNEL" }, label = { Text("Channel") })
                }
                Text("Network", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = network == "WIFI", onClick = { network = "WIFI" }, label = { Text("WiFi (World)") })
                    FilterChip(selected = network == "HOTSPOT", onClick = { network = "HOTSPOT" }, label = { Text("Hotspot") })
                    FilterChip(selected = network == "BLUETOOTH", onClick = { network = "BLUETOOTH" }, label = { Text("Bluetooth") })
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onCreate(name, type, network) }) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NoteItem(name: String, isAdd: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Add a small bubble for the note text here
            Box(
                modifier = Modifier
                    .offset(x = 16.dp, y = (-20).dp)
                    .background(NeonCyan.copy(alpha=0.2f), shape = MaterialTheme.shapes.small)
                    .padding(4.dp)
            ) {
                if(!isAdd) Text("Hello!", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                else Text("Add", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ChatListItem(
    name: String,
    lastMessage: String,
    time: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha=0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(name.first().toString(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    lastMessage, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(NeonBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(unreadCount.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
