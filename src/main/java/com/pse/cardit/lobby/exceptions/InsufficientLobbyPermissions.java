package com.pse.cardit.lobby.exceptions;

public class InsufficientLobbyPermissions extends IllegalArgumentException {
    public InsufficientLobbyPermissions(String missingPermissionsError) {
        super(missingPermissionsError);
    }
}
