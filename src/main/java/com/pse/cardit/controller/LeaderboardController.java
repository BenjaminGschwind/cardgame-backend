package com.pse.cardit.controller;

import com.pse.cardit.leaderboard.service.ILeaderboardService;
import com.pse.cardit.leaderboard.service.controll.LeaderboardResponse;
import com.pse.cardit.leaderboard.service.controll.PersonalStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LeaderboardController {
    private final ILeaderboardService service;


    @GetMapping("/leaderboard/stats")
    public ResponseEntity<PersonalStatsResponse> getPersonalStatistics(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(service.getPersonalStats(token), HttpStatus.OK);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
        return new ResponseEntity<>(service.getLeaderboard(), HttpStatus.OK);
    }
}
