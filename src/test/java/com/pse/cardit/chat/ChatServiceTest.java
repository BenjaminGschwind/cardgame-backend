package com.pse.cardit.chat;

import com.pse.cardit.ServiceTest;
import com.pse.cardit.chat.service.IChatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatServiceTest extends ServiceTest {

    @Autowired
    IChatService chatService;
    final String chatroomId = "Chatroom1";
    @BeforeAll
    void init() {
    chatService.createChatroom(chatroomId);

    }



    @Test
    void showUsersInChatroom() {
        assertDoesNotThrow(() -> chatService.addUserToChatroom(super.token,chatroomId), "Should be able to do that");


    }

    @Test
    void getIdByToken() {
        chatService.addUserToChatroom(token, chatroomId);
        assertEquals(chatroomId, chatService.getChatroomId(token).getChatroomId(), "Should be in the channel I just "
                + "joined.");
        //TODO: Test getting chat that does not exist.
    }



}
