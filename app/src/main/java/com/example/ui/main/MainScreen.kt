package com.example.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.chat.ChatScreen
import com.example.ui.chat.ChatViewModel
import com.example.ui.chat.ChatsListScreen
import com.example.ui.connections.ConnectionsScreen
import com.example.ui.onboarding.ProfileSetupScreen
import com.example.ui.theme.NeonCyan

@Composable
fun MainScreen(chatViewModel: ChatViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = currentRoute != "profile" && currentRoute != "chat_detail"

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chat") },
                        label = { Text("Chat") },
                        selected = currentRoute == "chat_list",
                        onClick = {
                            navController.navigate("chat_list") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Hub, contentDescription = "Mesh Hub") },
                        label = { Text("Mesh Hub") },
                        selected = currentRoute == "connections",
                        onClick = {
                            navController.navigate("connections") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "profile", modifier = Modifier.padding(innerPadding)) {
            composable("profile") {
                ProfileSetupScreen(navController = navController, onComplete = { 
                    navController.navigate("chat_list") {
                        popUpTo("profile") { inclusive = true }
                    }
                })
            }
            composable("chat_list") {
                ChatsListScreen(navController = navController, viewModel = chatViewModel)
            }
            composable("chat_detail") {
                ChatScreen(viewModel = chatViewModel, navController = navController)
            }
            composable("connections") {
                ConnectionsScreen()
            }
            composable("settings") {
                com.example.ui.settings.SettingsScreen()
            }
        }
    }
}

