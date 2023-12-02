package com.pse.cardit.game.service.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.Maexxle;
import com.pse.cardit.game.model.botlogic.MaexxleBotLogic;
import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.model.rules.MaexxleRules;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.action.IMia;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MiaAdapter extends AGameAdapter implements IMia {
    private final Maexxle game;

    public MiaAdapter(Collection<APlayer> players, List<String> rules) {
        this.game = new Maexxle(new MaexxleRules(), new ArrayList<>(players));
    }

    @Override
    protected AGame getGame() {
        return game;
    }

    @Override
    public String getState() {

        String json;
        List<MiaAdapter.MiaState.PlayerState> players = new ArrayList<>();
        for (IPlayer player : game.getPlayerList()) {
            players.add(new MiaAdapter.MiaState.PlayerState(
                    String.valueOf(player.getUserID()), 0, player.getInvSize()));
        }
        String looser = "";
        if (game.hasEnded()) {
            looser = game.getLooser().getUserID() + "";
        }
        MiaAdapter.MiaState state = new MiaAdapter.MiaState(players, game.getCurrentPlayer().getUserID() + "", looser,
                game.hasEnded(), game.getRoundCounter());
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            json = writer.writeValueAsString(state);
        }
        catch (JsonProcessingException e) {
            throw new UnknownError(e.getMessage());
        }
        return json;
    }

    @Override
    public GameType getGameType() {
        return GameType.MAEXXLE;
    }

    @Override
    public Collection<IPlayer> getWinners() {
        if (game.getLooser() == null) {
            return new HashSet<>();
        }
        Collection<IPlayer> winners = getPlayers();
        winners.remove(game.getLooser());
        return winners;
    }

    @Override
    public String getNextAction() {
        Bot bot = (Bot) game.getCurrentPlayer();
        return MaexxleBotLogic.evaluateMove(game, bot);
    }

    @Override
    public boolean makeCall() {
        return game.checkForLie();
    }

    @Override
    public boolean putValue(int value) {
        return game.lie(value);
    }

    @Override
    public String rollDice() {
        return game.shakeAndPeak() + "";
    }

    @Data
    @AllArgsConstructor
    private static class MiaState {
        List<MiaAdapter.MiaState.PlayerState> players;
        String activePlayer;
        String looser;
        boolean gameFinished;
        int roundCounter;

        @Data
        @AllArgsConstructor
        private static class PlayerState {
            String username;
            int imageId;
            int value;
        }
    }
}
