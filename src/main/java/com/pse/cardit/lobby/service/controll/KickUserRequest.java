package com.pse.cardit.lobby.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickUserRequest {
    private String authToken;
    private String targetUsername;
}
