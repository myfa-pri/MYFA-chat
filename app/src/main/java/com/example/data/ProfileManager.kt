package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("myfa_profile", Context.MODE_PRIVATE)

    private val _isSetupComplete = MutableStateFlow(prefs.getBoolean("is_setup_complete", false))
    val isSetupComplete: StateFlow<Boolean> = _isSetupComplete.asStateFlow()

    val isSetupCompleteSync: Boolean
        get() = prefs.getBoolean("is_setup_complete", false)

    fun saveProfile(name: String, username: String, bio: String) {
        prefs.edit()
            .putString("name", name)
            .putString("username", username)
            .putString("bio", bio)
            .putBoolean("is_setup_complete", true)
            .apply()
        _isSetupComplete.value = true
    }

    fun getProfile(): Map<String, String> {
        return mapOf(
            "name" to (prefs.getString("name", "") ?: ""),
            "username" to (prefs.getString("username", "") ?: ""),
            "bio" to (prefs.getString("bio", "") ?: "")
        )
    }
}
