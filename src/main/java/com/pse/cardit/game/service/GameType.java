package com.pse.cardit.game.service;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.service.executor.AExecutor;
import com.pse.cardit.game.service.executor.MauMauExecutor;
import com.pse.cardit.game.service.executor.MiaExecutor;

import java.util.Collection;
import java.util.List;

/**
 * Das Enum stellt die Spiel Typen zur Verfügung. Außerdem garantiert es durch abstrakte Funktionen, dass jeder Spiel
 * Typ auch zumindest grundlegend implementiert sein muss.
 */
public enum GameType {
    MAU_MAU {
        @Override
        AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules) {
            return new MauMauExecutor(gameId, players, rules);
        }
    },
    SCHWIMMEN {
        @Override
        AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules) {
            return null;
        }
    },
    MAEXXLE {
        @Override
        AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules) {
            return new MiaExecutor(gameId, players, rules);
        }
    },
    RUMMY {
        @Override
        AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules) {
            return null;
        }
    },
    UNO {
        @Override
        AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules) {
            return null;
        }
    };

    abstract AExecutor getExecutor(String gameId, Collection<APlayer> players, List<String> rules);
}
