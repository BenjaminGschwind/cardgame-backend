package com.pse.cardit.leaderboard.service;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.leaderboard.service.controll.LeaderboardResponse;
import com.pse.cardit.leaderboard.service.controll.PersonalStatsResponse;
import com.pse.cardit.user.model.User;
import org.springframework.stereotype.Service;

/**
 * ILeaderBoardService is an interface that provides methods to create and interact with a leaderboard.
 */
@Service
public interface ILeaderboardService {

    /**
     * Reruns the Statistics of a user as an PersonalStatsResponse.
     *
     * @param authToken The authToken of the User.
     * @return The PersonalStatsResponse
     */
    PersonalStatsResponse getPersonalStats(String authToken);

    /**
     * Returns the Leaderboard in an LeaderboardResponse.
     *
     * @return The LeaderboardResponse.
     */
    LeaderboardResponse getLeaderboard();

    /**
     * Add a new Statistic Entry for the given User.
     *
     * @param user The user.
     */
    void addStatistics(User user);

    /**
     * Delete the Statistic Entry for the given User.
     *
     * @param user The user.
     */
    void deleteStatistics(User user);

    /**
     * Add a win to the statistics of a given user.
     *
     * @param userID   The identifier of a user.
     * @param gameType The gameType.
     */
    void addWinToUser(long userID, GameType gameType);

    /**
     * Increase the number of played games by one for the given user.
     *
     * @param userID   The identifier of a user.
     */
    void addPlayedGameToUser(long userID);

    /**
     * Flush db.
     * Just for debug purpose.
     */
    void flushDB();
}
