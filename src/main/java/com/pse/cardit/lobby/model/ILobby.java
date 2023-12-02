package com.pse.cardit.lobby.model;

import java.util.List;

public interface ILobby {
    boolean add(Participant participant);

    boolean remove(Participant participant);

    /**
     * Sets the host of the lobby. Since there can only be one host at any time in the lobby, the old hosts
     * permissions will be revoked. <br>
     * If the participant is not part of the lobby, false will be returned.
     *
     * @param participant the host to be
     * @return whether the permissions were granted successfully
     */
    boolean setHost(Participant participant);

    List<Participant> getParticipants();

    LobbyCode getLobbyCode();

    void setVisibility(Visibility visibility);

    Visibility getVisibility();

    void setAfkTimer(int afkTimer);

    int getAfkTimer();

    void setActive(boolean active);

    boolean isActive();


    int getParticipantLimit();
}
