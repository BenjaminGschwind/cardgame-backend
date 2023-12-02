package com.pse.cardit.game.service;

public enum GameStatus {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    BAD_INTERACTION(600),
    GAME_ALREADY_ENDED(603);

    private static final String UNKNOWN_CODE_ERROR = "Error - The code %s does not belong to any known game "
            + "status.";
    private final int statusCode;

    GameStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean is2xxSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    public boolean is4xxClientError() {
        return this.statusCode >= 400 && this.statusCode < 500;
    }

    public boolean is6xxGameError() {
        return this.statusCode >= 600 && this.statusCode < 700;
    }

    public static GameStatus getStatusByCode(int code) {
        for (GameStatus status : GameStatus.values()) {
            if (status.getStatusCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException(UNKNOWN_CODE_ERROR);
    }


}
