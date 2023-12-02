package com.pse.cardit.chat.service.controll;

public class LeaveMessageResponse extends OutputMessageResponse {
    private static final String LEAVE_MESSAGE = "Left the chat";

    public LeaveMessageResponse(String username, String timestamp) {
        super(username, LEAVE_MESSAGE, timestamp);
    }
}
