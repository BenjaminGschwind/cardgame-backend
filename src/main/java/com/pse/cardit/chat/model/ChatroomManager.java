package com.pse.cardit.chat.model;

import com.pse.cardit.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatroomManager is a concrete class that manages the creation and removal of chatrooms.
 * It is implemented as a Singleton.
 */
public final class ChatroomManager {

    private static ChatroomManager instance;

    private final List<Chatroom> chatrooms;

    private ChatroomManager() {
        this.chatrooms = new ArrayList<>();
    }

    /**
     * Returns the ChatroomManager instance. If there is none a new instance will be created.
     *
     * @return The instance.
     */
    public static ChatroomManager getInstance() {
        if (ChatroomManager.instance == null) {
            ChatroomManager.instance = new ChatroomManager();
        }
        return ChatroomManager.instance;
    }

    /**
     * Creates a new Chatroom with a given identifier and adds it to the lists of chatrooms.
     *
     * @param chatroomId The identifier.
     */
    public void createChatroom(String chatroomId) {
        chatrooms.add(new Chatroom(chatroomId));
    }

    /**
     * Removes the chatroom with the given identifier from the list of chatrooms.
     *
     * @param chatroomId The identifier.
     */
    public void deleteChatroom(String chatroomId) {
        chatrooms.remove(new Chatroom(chatroomId));
    }

    /**
     * Returns the chatroom object with the given identifier.
     *
     * @param chatroomId The identifier.
     * @return The chatroom object.
     */
    public Chatroom getChatroom(String chatroomId) {
        return chatrooms.get(chatrooms.indexOf(new Chatroom(chatroomId)));
    }

    /**
     * Returns the identifier of the chatroom that the given user is part of.
     *
     * @param user The user in the chatroom.
     * @return The identifier of the chatroom the user is part of.
     */
    public String getChatroomIdByUser(User user) {
        for (Chatroom c:chatrooms) {
            if (c.getChatUsers().contains(user)) {
                return c.getChatroomId();
            }
        }
        return null;
    }

}
