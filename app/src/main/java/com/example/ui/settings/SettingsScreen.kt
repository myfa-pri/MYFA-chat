package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.data.ProfileManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(profileManager: ProfileManager) {
    val profile = remember { profileManager.getProfile() }
    val name = profile["name"] ?: "User"
    val username = profile["username"] ?: "username"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(NeonBlue.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.titleLarge)
                        Text("@$username", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { /* Edit Profile */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeonPink)
                    }
                }
            }

            Text("Network Rules", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

            NetworkRuleItem(
                title = "Bluetooth Connection",
                desc = "Works for 2 people in close range without internet.",
                color = NeonBlue
            )
            NetworkRuleItem(
                title = "Hotspot / Mesh",
                desc = "For group communication. Connect multiple users through a local hotspot.",
                color = NeonOrange
            )
            NetworkRuleItem(
                title = "WiFi Global",
                desc = "Connect to anyone worldwide using an active internet connection.",
                color = NeonCyan
            )
        }
    }
}

@Composable
fun NetworkRuleItem(title: String, desc: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = color)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
