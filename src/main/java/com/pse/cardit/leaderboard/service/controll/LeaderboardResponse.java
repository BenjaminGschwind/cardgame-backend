package com.pse.cardit.leaderboard.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponse {
    private List<PersonalStatsResponse> leaderboard;
}
