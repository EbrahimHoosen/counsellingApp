package com.example.myapplication;

public class Message {
    public static final int SENT = -1;
    public static final int RECEIVED = -2;

    private int messageId;
    private String senderID;
    private String timestamp;
    private String content;
    private int type;

    public Message(int messageId, String senderID, String timestamp, String content, int type) {
        this.messageId = messageId;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.content = content;
        this.type = type;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}