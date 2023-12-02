package com.pse.cardit.chat.service;

import com.pse.cardit.chat.model.Chatroom;
import com.pse.cardit.chat.model.ChatroomManager;
import com.pse.cardit.chat.service.controll.ChatIdResponse;
import com.pse.cardit.chat.service.controll.JoinMessageResponse;
import com.pse.cardit.chat.service.controll.LeaveMessageResponse;
import com.pse.cardit.chat.service.controll.OutputMessageResponse;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatService is a concrete class that implements the IChatService interface.
 */
@Service
public class ChatService implements IChatService {

    private final ChatroomManager chatroomManager;
    private final IUserService userService;

    /**
     * Instantiates a new Chat service.
     *
     * @param userService the user service
     */
    public ChatService(IUserService userService) {
        this.userService = userService;
        this.chatroomManager = ChatroomManager.getInstance();
    }

    @Override
    public OutputMessageResponse broadcastMessage(String authToken, String content, String chatroomId) {
        return chatroomManager.getChatroom(chatroomId)
            .broadcastMessage(userService.getUserFromToken(authToken).getUsername(), content);
    }

    @Override
    public void createChatroom(String chatroomId) {
        chatroomManager.createChatroom(chatroomId);
    }

    @Override
    public void deleteChatroom(String chatroomId) {
        chatroomManager.deleteChatroom(chatroomId);
    }

    @Override
    public JoinMessageResponse addUserToChatroom(String authToken, String chatroomId) {
        return chatroomManager.getChatroom(chatroomId).joinChatroom(userService.getUserFromToken(authToken));
    }

    @Override
    public LeaveMessageResponse removeUserFromChatroom(String authToken, String chatroomId) {
        return chatroomManager.getChatroom(chatroomId).leaveChatroom(userService.getUserFromToken(authToken));
    }

    @Override
    public Chatroom getChatroom(String chatroomId) {
        return chatroomManager.getChatroom(chatroomId);
    }

    @Override
    public List<OutputMessageResponse> getMessages(String chatroomId) {
        return chatroomManager.getChatroom(chatroomId).getMessageHistory();
    }

    @Override
    public ChatIdResponse getChatroomId(String token) {
        User user = userService.getUserFromToken(token);
        return new ChatIdResponse(chatroomManager.getChatroomIdByUser(user));
    }
}
