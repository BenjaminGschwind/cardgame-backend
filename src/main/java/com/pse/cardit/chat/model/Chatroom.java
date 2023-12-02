package com.pse.cardit.chat.model;

import com.pse.cardit.chat.service.controll.JoinMessageResponse;
import com.pse.cardit.chat.service.controll.LeaveMessageResponse;
import com.pse.cardit.chat.service.controll.OutputMessageResponse;
import com.pse.cardit.user.model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Chatroom is a concrete class that provides the environment for a basic group based textchat.
 */
public class Chatroom {

    private final SimpleDateFormat dateTimeFormat;
    private final String chatroomId;

    private final List<OutputMessageResponse> messageHistory;
    private final List<User> chatUsers;

    /**
     * Constructs a Chatroom object using a given identifier for the chatroom.
     *
     * @param chatroomId The identifier for the chatroom.
     */
    public Chatroom(String chatroomId) {
        this.chatroomId = chatroomId;
        this.dateTimeFormat = new SimpleDateFormat("HH:mm");

        this.messageHistory = new ArrayList<>();
        this.chatUsers = new ArrayList<>();
    }

    /**
     * Returns a OutputMessageResponse from a given username and the message content and adds it to the messageHistory.
     *
     * @param username The name of the user that wants to send a message.
     * @param content  The content of the message.
     * @return OutputMessageResponse containing the username, the content and a timestamp.
     */
    public OutputMessageResponse broadcastMessage(String username, String content) {
        OutputMessageResponse message = new OutputMessageResponse(username, content,
            dateTimeFormat.format(new Timestamp(System.currentTimeMillis())));
        messageHistory.add(message);
        return message;
    }

    /**
     * Adds the given user into the chatroom and returns a JoinMessageResponse.
     *
     * @param user The user ot add.
     * @return JoinMessageResponse containing the username, the join message and a timestamp.
     */
    public JoinMessageResponse joinChatroom(User user) {
        chatUsers.add(user);
        return new JoinMessageResponse(user.getUsername(),
            dateTimeFormat.format(new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Removes the given user from the chatroom and returns a LeaveMessageResponse
     *
     * @param user The user to remove.
     * @return LeaveMessageResponse containing the username, the join message and a timestamp.
     */
    public LeaveMessageResponse leaveChatroom(User user) {
        chatUsers.remove(user);
        return new LeaveMessageResponse(user.getUsername(),
            dateTimeFormat.format(new Timestamp(System.currentTimeMillis())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chatroom chatroom = (Chatroom) o;
        return chatroomId.equals(chatroom.chatroomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatroomId);
    }

    /**
     * Gets message history.
     *
     * @return the message history
     */
    public List<OutputMessageResponse> getMessageHistory() {
        return messageHistory;
    }

    /**
     * Gets chat users.
     *
     * @return the chat users
     */
    public List<User> getChatUsers() {
        return chatUsers;
    }

    /**
     * Gets chatroom id.
     *
     * @return the chatroom id
     */
    public String getChatroomId() {
        return chatroomId;
    }
}
