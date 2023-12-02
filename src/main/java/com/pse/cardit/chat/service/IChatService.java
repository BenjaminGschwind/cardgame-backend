package com.pse.cardit.chat.service;

import com.pse.cardit.chat.model.Chatroom;
import com.pse.cardit.chat.service.controll.ChatIdResponse;
import com.pse.cardit.chat.service.controll.JoinMessageResponse;
import com.pse.cardit.chat.service.controll.LeaveMessageResponse;
import com.pse.cardit.chat.service.controll.OutputMessageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IChatService is an interface that provides methods to create and interact with a chatroom.
 */
@Service
public interface IChatService {

    /**
     * Returns a OutputMessageResponse and sends a message containing the given content in the chatroom with the given
     * identifier.
     *
     * @param authToken  The authToken of the user that wants to send the message.
     * @param content    The content of the message.
     * @param chatroomId The identifier of the chatroom where the message should be send in to.
     * @return the output message response
     */
    OutputMessageResponse broadcastMessage(String authToken, String content, String chatroomId);

    /**
     * Create a chatroom with the given identifier as the identifier of the chatroom.
     *
     * @param chatroomId The identifier.
     */
    void createChatroom(String chatroomId);

    /**
     * Delete a chatroom with the given identifier as the identifier of the chatroom.
     *
     * @param chatroomId The identifier.
     */
    void deleteChatroom(String chatroomId);

    /**
     * Adds a user to a chatroom with the given identifier and returns a JoinMessageResponse.
     *
     * @param authToken  The authToken of the user.
     * @param chatroomId The identifier.
     * @return The JoinMessageResponse.
     */
    JoinMessageResponse addUserToChatroom(String authToken, String chatroomId);

    /**
     * Removes a user from a chatroom with the given identifier and returns a LeaveMessageResponse.
     *
     * @param authToken  The authToken of the user.
     * @param chatroomId The identifier.
     * @return The LeaveMessageResponse.
     */
    LeaveMessageResponse removeUserFromChatroom(String authToken, String chatroomId);

    /**
     * Returns a Chatroom object with the given identifier.
     *
     * @param chatroomId The identifier.
     * @return The Chatroom object.
     */
    Chatroom getChatroom(String chatroomId);

    /**
     * Returns all broadcasted messages as a List of the chatroom with the given identifier.
     *
     * @param chatroomId The identifier.
     * @return The List of OutputMessageResponses
     */
    List<OutputMessageResponse> getMessages(String chatroomId);

    /**
     * Gets chatroom id.
     *
     * @param token the token
     * @return the chatroom id
     */
    ChatIdResponse getChatroomId(String token);

}
