package com.pse.cardit.leaderboard;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.leaderboard.service.ILeaderboardService;
import com.pse.cardit.security.config.JwtService;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class LeaderBoardTest {

    @Autowired
    ILeaderboardService leaderboardService;

    @Autowired
    UserService userService;
    @Autowired
    JwtService jwtService;


    User user1;
    User user2;
    @BeforeAll
    void init() {
            leaderboardService.flushDB();
            userService.resetRepo();
            user1 = userService.addUser("Peter");
            user2 = userService.addUser("Hans");

    }

    @Test
    void addNewUserStatistics() {
        leaderboardService.addStatistics(user1);
        leaderboardService.addStatistics(user2);

    }

    @Test
    void addWinToUser(){
        System.out.println(user1.getUserId());
        leaderboardService.addWinToUser(user1.getUserId(), GameType.MAU_MAU);
        leaderboardService.addWinToUser(user2.getUserId(), GameType.SCHWIMMEN);

    }

    @Test
    void addPlayedToUser() {
        leaderboardService.addPlayedGameToUser(user1.getUserId());
        leaderboardService.addPlayedGameToUser(user1.getUserId());
        leaderboardService.addPlayedGameToUser(user1.getUserId());
        leaderboardService.addPlayedGameToUser(user2.getUserId());
    }

    @Test
    void getUserStat() {
        String token = jwtService.generateToken(user1);
        System.out.println(leaderboardService.getPersonalStats(token).toString());
    }

    @Test
    void getLeaderboard() {
        System.out.println(leaderboardService.getLeaderboard());
    }



}
