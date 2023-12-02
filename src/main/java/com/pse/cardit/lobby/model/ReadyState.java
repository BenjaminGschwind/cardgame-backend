package com.pse.cardit.lobby.model;

public enum ReadyState {
    READY(true),
    NOT_READY(false);

    private final boolean canStartGame;

    ReadyState(boolean canStartGame) {
        this.canStartGame = canStartGame;
    }

    public boolean isCanStartGame() {
        return canStartGame;
    }
}
