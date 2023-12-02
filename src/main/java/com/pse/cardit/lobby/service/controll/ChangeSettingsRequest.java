package com.pse.cardit.lobby.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeSettingsRequest {
    private String authToken;
    private String gameType;
    private String visibility;
    private String difficulty;
    private int amountBots;
    private int afkTimer;
    private List<String> rules;
}
