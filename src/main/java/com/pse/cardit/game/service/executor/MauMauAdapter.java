package com.pse.cardit.game.service.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.MauMau;
import com.pse.cardit.game.model.botlogic.MauMauBotLogic;
import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.model.holdable.ColorEnum;
import com.pse.cardit.game.model.holdable.IHoldable;
import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.model.rules.MauMauRules;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.action.IMauMau;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MauMauAdapter extends AGameAdapter implements IMauMau {

    private static final String OUTPUT_SEPARATOR = ";";
    private static final int ACTION_MAX_CARDS = 1;
    private static final int ACTION_MIN_CARDS = 1;

    private final MauMau game;

    public MauMauAdapter(Collection<APlayer> players, List<String> rules) {
        this.game = new MauMau(new MauMauRules(), new ArrayList<>(players));
    }

    @Override
    protected AGame getGame() {
        return game;
    }

    @Override
    public String getState() {
        String json;
        String placeCard = "";
        if (game.getPlaceDeck().getDeckSize() > 0) {
            placeCard = game.getPlaceDeck().peekCard().toString();
        }
        List<MauMauState.PlayerState> players = new ArrayList<>();
        for (IPlayer player : game.getPlayerList()) {
            players.add(new MauMauState.PlayerState(String.valueOf(player.getUserID()), 0, player.getInvSize()));
        }
        IPlayer activePlayer = game.getCurrentPlayer();
        if (game.hasEnded()) {
            activePlayer = game.getWinner();
        }
        MauMauState state = new MauMauState(players, activePlayer.getUserID() + "",
                game.getMauMauRule().getValidColor().toString(), game.getMauMauRule().getValidNumber().toString(),
                game.hasEnded(), game.getRoundCounter(), game.getDrawDeck().getDeckSize(), game.getPlaceDeck().getDeckSize(),
                placeCard);
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
        return GameType.MAU_MAU;
    }

    @Override
    public IPlayer getActivePlayer() {
        IPlayer activePlayer = game.getWinner();
        if (activePlayer == null) {
            return game.getCurrentPlayer();
        }
        return activePlayer;
    }

    @Override
    public Collection<IPlayer> getWinners() {
        if (game.getWinner() == null) {
            return new HashSet<>();
        }
        return Set.of(game.getWinner());
    }

    @Override
    public String getNextAction() {
        Bot bot = (Bot) game.getCurrentPlayer();
        return MauMauBotLogic.evaluateMove(game, bot);
    }

    @Override
    public boolean discard(List<String> cards) {
        if (incorrectAmountOfCards(cards.size())) {
            return false;
        }
        Card decodedCard = stringsToCards(cards).get(0);
        return game.putCard(decodedCard, Optional.empty());
    }

    @Override
    public String drawFromDraw(int amount) {
        if (amount != 1) {
            Logger.getGlobal().log(new LogRecord(Level.WARNING, "It should not be possible to interact with Mau Mau "
                    + "and draw more than a single card! Why did this still pass? Oh maybe because, you have to draw "
                    + "multiple after a seven? Then this argument is useless!"));
        }
        List<Card> cards = game.drawCard();
        if (cards.isEmpty()) {
            return "";
        }
        return cards.toString();
    }

    @Override
    public String getHoldable() {
        return getHoldable(game.getCurrentPlayer());
    }

    public String getHoldable(IPlayer player) {
        List<IHoldable> inventory = player.getInventory();
        StringBuilder builder = new StringBuilder();
        for (IHoldable card : inventory) {
            builder.append(card.toString());
            builder.append(OUTPUT_SEPARATOR);
        }
        if (builder.length() > OUTPUT_SEPARATOR.length()) {
            builder.setLength(builder.length() - OUTPUT_SEPARATOR.length());
        }
        return builder.toString();
    }

    @Override
    public boolean discard(List<String> args, String arg) {
        if (incorrectAmountOfCards(args.size())) {
            return false;
        }
        Card decodedCard = stringsToCards(args).get(0);
        ColorEnum val = null;
        for (ColorEnum value : ColorEnum.values()) {
            if (value.getCodec().equals(arg)) {
                val = value;
            }
        }
        if (val == null) {
            return false;
        }
        return game.putCard(decodedCard, Optional.of(val));
    }

    private List<Card> stringsToCards(List<String> strings) {
        List<Card> cards = new ArrayList<>();
        for (String s : strings) {
            cards.add(new Card(s));
        }
        return cards;
    }

    private boolean incorrectAmountOfCards(int actual) {
        //TODO discuss, should this be here?
        return actual < ACTION_MIN_CARDS || actual > ACTION_MAX_CARDS;
    }

    @Override
    public boolean pass() {
        return game.selectNextPlayer();
    }


    @Data
    @AllArgsConstructor
    private static class MauMauState {
        List<PlayerState> players;
        String activePlayer;
        String currentValidColor;
        String currentValidValue;
        boolean gameFinished;
        int roundCounter;
        int amountDrawDeck;
        int amountDiscardDeck;
        String topCardDiscardDeck;

        @Data
        @AllArgsConstructor
        private static class PlayerState {
            String username;
            int imageId;
            int cardCount;
        }
    }
}
