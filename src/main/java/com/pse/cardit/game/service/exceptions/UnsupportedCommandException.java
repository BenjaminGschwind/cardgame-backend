package com.pse.cardit.game.service.exceptions;

public class UnsupportedCommandException extends IllegalArgumentException {
    public UnsupportedCommandException(String message) {
        super(message);
    }
}
