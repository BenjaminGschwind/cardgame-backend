package com.pse.cardit.lobby.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLobbiesResponse {
    private List<LobbyStomp> lobbies;
}