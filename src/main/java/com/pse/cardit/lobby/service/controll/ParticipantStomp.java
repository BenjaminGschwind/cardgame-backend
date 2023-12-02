package com.pse.cardit.lobby.service.controll;

import com.pse.cardit.lobby.model.Participant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParticipantStomp extends UserStomp {
    private boolean readyCheck;
    private String rank;

    public ParticipantStomp(Participant participant) {
        super(participant.getUsername(), participant.getImageId());
        this.readyCheck = participant.getReadyState().isCanStartGame();
        this.rank = participant.getRank().toString();
    }

    @Override
    public String toString() {
        return "ParticipantStomp{" + System.lineSeparator()
                + "    username=" + getUsername() + ";" + System.lineSeparator()
                + "    imageId=" + getImageId() + ";" + System.lineSeparator()
                + "    readyCheck=" + readyCheck + "" + System.lineSeparator()
                + '}';
    }
}
