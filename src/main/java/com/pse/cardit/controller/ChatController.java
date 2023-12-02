package com.pse.cardit.controller;

import com.pse.cardit.chat.service.IChatService;
import com.pse.cardit.chat.service.controll.ChatIdResponse;
import com.pse.cardit.chat.service.controll.ChatMessageRequest;
import com.pse.cardit.chat.service.controll.JoinChatroomRequest;
import com.pse.cardit.chat.service.controll.LeaveChatroomRequest;
import com.pse.cardit.chat.service.controll.OutputMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final IChatService service;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/chat/getId")
    public ResponseEntity<ChatIdResponse> getChatroomId(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(service.getChatroomId(token), HttpStatus.OK);
    }



    @MessageMapping("/chat/{chatroomId}/join")
    public void joinChatroom(@Payload JoinChatroomRequest request, @DestinationVariable String chatroomId) {
        broadcastMessage(service.addUserToChatroom(request.getAuthToken(), chatroomId),
                chatroomId);
    }

    @MessageMapping("/chat/{chatroomId}/leave")
    public void leaveChatroom(@Payload LeaveChatroomRequest request, @DestinationVariable String chatroomId) {
        broadcastMessage(service.removeUserFromChatroom(request.getAuthToken(), chatroomId),
            chatroomId);
    }

    @MessageMapping("/chat/{chatroomId}/send/message")
    public void receiveMessage(@Payload ChatMessageRequest request, @DestinationVariable String chatroomId) {
        broadcastMessage(service.broadcastMessage(request.getAuthToken(), request.getContent(), chatroomId),
            chatroomId);
    }

    private void broadcastMessage(OutputMessageResponse response,
                                  @DestinationVariable String chatroomId) {
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatroomId + "/receive/message", response);
    }
}
