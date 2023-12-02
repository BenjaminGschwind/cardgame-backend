package com.pse.cardit.chat.service.controll;

public class JoinMessageResponse extends OutputMessageResponse {

    private static final String JOIN_MESSAGE = "Joined the chat";

    public JoinMessageResponse(String username, String timestamp) {
        super(username, JOIN_MESSAGE, timestamp);
    }
}
