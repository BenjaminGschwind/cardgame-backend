package com.pse.cardit.game.service.controll;

import com.pse.cardit.game.service.GameStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InteractResponse {
    private int statusCode;
    private String response;

    public InteractResponse(GameStatus statusCode, String response) {
        this.statusCode = statusCode.getStatusCode();
        this.response = response;
    }

    public GameStatus getStatus() {
        return GameStatus.getStatusByCode(this.statusCode);
    }
}
