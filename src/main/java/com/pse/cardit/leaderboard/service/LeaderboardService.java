package com.pse.cardit.leaderboard.service;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.leaderboard.model.Statistics;
import com.pse.cardit.leaderboard.service.controll.LeaderboardResponse;
import com.pse.cardit.leaderboard.service.controll.PersonalStatsResponse;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LeaderboardService is a concrete class that implements the ILeaderboardService interface.
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService implements ILeaderboardService {

    private final IUserService userService;
    private final IStatisticsRepository statisticsRepository;

    @Override
    public PersonalStatsResponse getPersonalStats(String authToken) {
        String username = userService.getUserFromToken(authToken).getUsername();
        Statistics stats = statisticsRepository.findStatisticsByUserId(
                userService.getUserFromToken(authToken).getUserId())
                .orElse(null);
        if (stats == null) {
            addStatistics(userService.getUserFromToken(authToken));
            stats = statisticsRepository.findStatisticsByUserId(userService.getUserFromToken(authToken).getUserId())
                    .orElseThrow(IllegalArgumentException::new);
        }

        return new PersonalStatsResponse(username, stats.getGamesPlayed(), stats.getMauMauWins(),
            stats.getMaexxleWins(), stats.getSchwimmenWins());
    }

    @Override
    public LeaderboardResponse getLeaderboard() {

        List<Statistics> stats = statisticsRepository.findTop25ByMauMauWinsGreaterThanOrderByMauMauWinsDesc(0);
        List<PersonalStatsResponse> personalStatsResponseList = new ArrayList<>();

        for (Statistics s : stats) {
            personalStatsResponseList.add(
                new PersonalStatsResponse(s, userService.getUser(s.getUserId()).getUsername()));
        }
        return new LeaderboardResponse(personalStatsResponseList);
    }

    @Override
    public void addStatistics(User user) {
        Statistics statEntry = new Statistics(user);
        statisticsRepository.save(statEntry);
    }

    @Override
    public void deleteStatistics(User user) {
        statisticsRepository.delete(this.getStats(user));
    }

    @Override
    public void addWinToUser(long userID, GameType gameType) {
        if (statisticsRepository.findStatisticsByUserId(userID).isEmpty()) {
            addStatistics(userService.getUser(userID));
        }
        Statistics stat = statisticsRepository.findStatisticsByUserId(userID).orElseThrow(IllegalStateException::new);
        stat.addWin(gameType);
        statisticsRepository.save(stat);
    }

    @Override
    public void addPlayedGameToUser(long userID) {
        if (statisticsRepository.findStatisticsByUserId(userID).isEmpty()) {
            addStatistics(userService.getUser(userID));
        }
        Statistics stat = statisticsRepository.findStatisticsByUserId(userID).orElseThrow(IllegalStateException::new);
        stat.addPlayedGame();
        statisticsRepository.save(stat);
    }

    private Statistics getStats(User user) {
        return statisticsRepository.findStatisticsByUserId(user.getUserId()).orElseThrow(IllegalStateException::new);
    }

    public void flushDB() {
        statisticsRepository.deleteAll();
    }

}
