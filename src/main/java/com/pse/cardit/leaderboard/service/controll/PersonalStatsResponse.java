package com.pse.cardit.leaderboard.service.controll;

import com.pse.cardit.leaderboard.model.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalStatsResponse {
    String username;
    int gamesPlayed;
    int mauMauWins;
    int schwimmenWins;
    int maexxleWins;

    public PersonalStatsResponse(Statistics statistics, String username) {
        this.username = username;
        this.gamesPlayed = statistics.getGamesPlayed();
        this.mauMauWins = statistics.getMauMauWins();
        this.schwimmenWins = statistics.getSchwimmenWins();
        this.maexxleWins = statistics.getMaexxleWins();


    }
}
