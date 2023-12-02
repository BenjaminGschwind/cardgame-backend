package com.pse.cardit.leaderboard.service;

import com.pse.cardit.leaderboard.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Statistics repository.
 */
@Repository
public interface IStatisticsRepository extends JpaRepository<Statistics, Long> {

    /**
     * Find statistics by user id optional.
     *
     * @param userId the user id
     * @return the optional
     */
    Optional<Statistics> findStatisticsByUserId(long userId);

    List<Statistics> findAll();

    /**
     * Find top 25 by maumau wins greater than order by maumau wins desc list.
     *
     * @param mauMauWins the mau mau wins
     * @return the list
     */
    List<Statistics> findTop25ByMauMauWinsGreaterThanOrderByMauMauWinsDesc(int mauMauWins);


}
