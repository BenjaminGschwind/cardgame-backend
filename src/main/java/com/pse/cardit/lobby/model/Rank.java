package com.pse.cardit.lobby.model;

public enum Rank {
    NONE,
    HOST;

    public static final Rank CAN_START_GAME_RANK = HOST;
    public static final Rank CAN_KICK_OTHERS = HOST;
    public static final Rank CAN_CHANGE_SETTINGS = HOST;
}
