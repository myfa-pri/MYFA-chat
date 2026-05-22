package com.example.ui.connections

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonOrange
import kotlin.math.cos
import kotlin.math.sin

data class ConnectionNode(val id: String, val name: String, val type: String, val strength: Float, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun ConnectionsScreen(modifier: Modifier = Modifier) {
    val nodes = listOf(
        ConnectionNode("1", "Local Wi-Fi Mesh", "WIFI", 0.9f, Icons.Default.Wifi),
        ConnectionNode("2", "Sarah's Hotspot", "HOTSPOT", 0.7f, Icons.Default.CellTower),
        ConnectionNode("3", "Peer_BT_99A", "BLUETOOTH", 0.4f, Icons.Default.Bluetooth),
        ConnectionNode("4", "Data Bridge", "CELLULAR", 0.8f, Icons.Default.SignalCellularAlt)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MeshScannerHeader()
        
        Text(
            text = "ADVANCED BLUETOOTH FEATURES",
            color = NeonOrange,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            letterSpacing = 1.sp,
            fontSize = 12.sp
        )
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = NeonOrange)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Offline Bluetooth Chat", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Discover nearby devices and send secure messages directly via Bluetooth without any internet connection. Messages will queue if the device is out of range.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Activate BT */ }, 
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Invisible Mode", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Text(
            text = "NEARBY PEERS",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            letterSpacing = 1.5.sp
        )
        
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nodes) { node ->
                ConnectionCard(node)
            }
        }
    }
}

@Composable
fun MeshScannerHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.height / 2.5f
            
            drawCircle(
                color = NeonCyan.copy(alpha = 0.1f + (0.2f * (1f - pulse))),
                radius = maxRadius * pulse,
                center = center
            )
            drawCircle(
                color = NeonCyan.copy(alpha = 0.05f),
                radius = maxRadius,
                center = center
            )
            drawCircle(
                color = NeonPink,
                radius = 12f,
                center = center
            )
            
            // Random nodes
            for (i in 0..4) {
                val angle = (i * Math.PI * 2 / 5) + (pulse * Math.PI / 4)
                val r = maxRadius * 0.7f
                val x = center.x + (cos(angle) * r).toFloat()
                val y = center.y + (sin(angle) * r).toFloat()
                
                drawLine(
                    color = NeonPink.copy(alpha = 0.4f),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 2f
                )
                
                drawCircle(
                    color = NeonCyan,
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
            Text("SCANNING FOR MESH PEERS...", color = NeonCyan, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun ConnectionCard(node: ConnectionNode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = node.icon,
                    contentDescription = node.type,
                    tint = if (node.type == "WIFI" || node.type == "CELLULAR") NeonCyan else NeonPink
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(node.name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(node.type, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            
            CircularProgressIndicator(
                progress = { node.strength },
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                strokeWidth = 3.dp,
            )
        }
    }
}
