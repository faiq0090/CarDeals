package com.example.smdproject.com.example.smdproject

class Message() {
    var messageId: String? = null
    var messageContent: String? = null
    var timestamp: Long = 0
    var messageType: MessageType? = null
    var mediaUrl: String? = null

    constructor(
        messageId: String?,
        messageContent: String?,
        timestamp: Long,
        messageType: MessageType?,
        mediaUrl: String?
    ) : this() {
        this.messageId = messageId
        this.messageContent = messageContent
        this.timestamp = timestamp
        this.messageType = messageType
        this.mediaUrl = mediaUrl
    }
}

enum class MessageType {
    TEXT,
    VIDEO,
    IMAGE,
    VOICE_NOTE,
    FILE // New message type for files
}