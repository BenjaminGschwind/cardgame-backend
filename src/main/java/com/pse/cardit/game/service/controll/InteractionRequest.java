package com.pse.cardit.game.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractionRequest {
    private String authToken;
    private String interaction;
}
