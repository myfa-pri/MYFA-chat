package com.example.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(private val messageDao: MessageDao, private val conversationDao: ConversationDao) {

    fun getMessagesForConversation(conversationId: Int): Flow<List<Message>> = messageDao.getMessagesForConversation(conversationId)

    fun getAllConversations(): Flow<List<Conversation>> = conversationDao.getAllConversations()

    suspend fun createConversation(name: String, type: String, networkType: String): Int {
        val conv = Conversation(name = name, type = type, networkType = networkType, timestamp = System.currentTimeMillis())
        return conversationDao.insertConversation(conv).toInt()
    }

    suspend fun sendMessage(conversationId: Int, content: String, networkType: String = "WIFI") {
        val msg = Message(
            conversationId = conversationId,
            senderName = "Me",
            content = content,
            isFromMe = true,
            networkType = networkType
        )
        messageDao.insertMessage(msg)
        updateConversationLastMessage(conversationId, content)
    }

    suspend fun receiveMockMessage(conversationId: Int, content: String, sender: String, networkType: String = "BLUETOOTH") {
        val msg = Message(
            conversationId = conversationId,
            senderName = sender,
            content = content,
            isFromMe = false,
            networkType = networkType
        )
        messageDao.insertMessage(msg)
        updateConversationLastMessage(conversationId, content)
    }
    
    private suspend fun updateConversationLastMessage(conversationId: Int, content: String) {
        val conv = conversationDao.getConversationById(conversationId)
        if (conv != null) {
            conversationDao.updateConversation(conv.copy(lastMessage = content, timestamp = System.currentTimeMillis()))
        }
    }
}
