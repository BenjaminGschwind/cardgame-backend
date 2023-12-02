package com.pse.cardit.leaderboard.model;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Statistic is a concrete class that represents an object and database entity that
 * contains statistics about a player like the amount of player games an wins.
 *
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    private int mauMauWins;
    private int schwimmenWins;
    private int maexxleWins;
    private int gamesPlayed;
    private long userId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long statisticsId;


    /**
     * Instantiates a new Statistics with the given user as an identifier.
     *
     * @param user the user
     */
    public Statistics(User user) {
        this.userId = user.getUserId();
        this.mauMauWins = 0;
        this.schwimmenWins = 0;
        this.maexxleWins = 0;
        this.gamesPlayed = 0;
        this.statisticsId = 0;

    }

    /**
     * Add a win in the given game type category.
     *
     * @param gameType the game type
     */
    public void addWin(GameType gameType) {
        switch (gameType) {
            case MAU_MAU -> this.mauMauWins += 1;
            case SCHWIMMEN -> this.schwimmenWins += 1;
            case MAEXXLE -> this.maexxleWins += 1;
            default -> throw new IllegalStateException("Unexpected value: " + gameType);
        }
    }

    /**
     * Increase the amount of played games by one.
     */
    public void addPlayedGame() {
        gamesPlayed += 1;
    }


    // Getter & Setter


    /**
     * Gets maumau wins.
     *
     * @return the maumau wins
     */
    public int getMauMauWins() {
        return mauMauWins;
    }

    /**
     * Gets schwimmen wins.
     *
     * @return the schwimmen wins
     */
    public int getSchwimmenWins() {
        return schwimmenWins;
    }


    /**
     * Gets maexxle wins.
     *
     * @return the maexxle wins
     */
    public int getMaexxleWins() {
        return maexxleWins;
    }


    /**
     * Gets games played.
     *
     * @return the games played
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Sets games played.
     *
     * @param gamesPlayed the games played
     */
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    @Override
    public String toString() {
        return "Statistics " + userId + " {"
            + "maumauWins=" + mauMauWins
            + ", schwimmenWins=" + schwimmenWins
            + ", maexxleWins=" + maexxleWins
            + ", gamesPlayed=" + gamesPlayed
            + '}';
    }
}
