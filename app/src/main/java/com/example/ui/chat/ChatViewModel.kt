package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatRepository
import com.example.data.Message
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _activeConversationId = kotlinx.coroutines.flow.MutableStateFlow<Int?>(null)
    val activeConversationId: StateFlow<Int?> = _activeConversationId
    
    val allConversations = repository.getAllConversations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeConversation: StateFlow<com.example.data.Conversation?> = _activeConversationId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getAllConversations().map { list -> list.find { it.id == id } }
            } else {
                flowOf(null)
            }
        }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val allMessages: StateFlow<List<Message>> = _activeConversationId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getMessagesForConversation(id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun setActiveConversation(id: Int) {
        _activeConversationId.value = id
    }

    fun createConversation(name: String, type: String, networkType: String, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val id = repository.createConversation(name, type, networkType)
            onCreated(id)
        }
    }

    fun sendMessage(content: String, networkType: String = "WIFI") {
        if (content.isBlank()) return
        val convId = _activeConversationId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(convId, content, networkType)
            
            // Simulate a reply if we are talking in 'BLUETOOTH' or 'HOTSPOT' mesh mode
            if (networkType != "WIFI") {
                kotlinx.coroutines.delay(1000)
                repository.receiveMockMessage(
                    conversationId = convId,
                    content = "Received via $networkType mesh! Beautiful connection.",
                    sender = "MeshNode_7x",
                    networkType = networkType
                )
            }
        }
    }
}
