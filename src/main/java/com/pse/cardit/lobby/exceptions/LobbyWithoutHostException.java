package com.pse.cardit.lobby.exceptions;

public class LobbyWithoutHostException extends IllegalStateException {
    public LobbyWithoutHostException(String message) {
        super(message);
    }
}
