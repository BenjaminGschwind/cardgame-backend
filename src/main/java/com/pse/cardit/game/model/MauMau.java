package com.pse.cardit.game.model;

import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.model.holdable.CardDeck;
import com.pse.cardit.game.model.holdable.CardDeckFactory;
import com.pse.cardit.game.model.holdable.CardSpecEnum;
import com.pse.cardit.game.model.holdable.ColorEnum;
import com.pse.cardit.game.model.holdable.ValueEnum;
import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.rules.MauMauRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MauMau is a concrete class that extends the AGame.
 * It contains methods and attributes needed to play a MauMau game.
 */
public class MauMau extends AGame {

    private static final int MINIMUM_AMOUNT_PLAYERS = 2;
    private static final int MAXIMUM_AMOUNT_PLAYERS = 8;


    private CardDeck drawDeck;
    private final CardDeck placeDeck;

    private final MauMauRules mauMauRule;

    private boolean hasDrawn;
    private boolean hasPut;
    private int sevenCounter;
    private boolean playable;
    private APlayer winner;


    /**
     * Instantiates a new MauMau game with the given players and rule.
     *
     * @param rule       The rule.
     * @param playerList The list of APlayer participating.
     */
    public MauMau(MauMauRules rule, List<APlayer> playerList) {
        super(playerList);
        if (playerList.size() > 4) {
            this.drawDeck = CardDeckFactory.createCardDeck(CardSpecEnum.THIRTY_TWO_TWICE);
        }
        else {
            this.drawDeck = CardDeckFactory.createCardDeck(CardSpecEnum.THIRTY_TWO_PIQUET);
        }
        this.placeDeck = CardDeckFactory.createCardDeck(CardSpecEnum.EMPTY);
        this.hasDrawn = false;
        this.hasPut = false;
        this.mauMauRule = rule;
        drawDeck.shuffle();
        initialiseRound();
    }


    /**
     * Draws a card and adds it to the inventory of the current player. Returns a List of all Cards collected.
     *
     * @return The list of the collected Cards
     */
    public List<Card> drawCard() {
        ArrayList<Card> output = new ArrayList<>();
        if (!playable) {
            return output;
        }
        if (mauMauRule.checkDraw(hasDrawn)) {
            return output;
        }
        if (drawDeck.getDeckSize() < 2) {
            reshuffleDeck();
        }
        if (sevenCounter != 0) {
            if (drawDeck.getDeckSize() < sevenCounter * 2) {
                reshuffleDeck();
            }
            if (drawDeck.getDeckSize() == 0) return output;
            //Potentielle Sonderregel, x statt 2 ziehen.
            for (int i = 0; i < sevenCounter * 2; i++) {
                Card drawn = drawDeck.popCard();
                getCurrentPlayer().putInInventory(drawn);
                output.add(drawn);
            }

            sevenCounter = 0;

        }
        else {
            if (drawDeck.getDeckSize() == 0) return output;
            Card drawn = drawDeck.popCard();
            getCurrentPlayer().putInInventory(drawn);
            output.add(drawn);

        }
        hasDrawn = true;
        return output;
    }


    /**
     * Put a given card on top of the placeDeck.
     *
     * @param cardToPut The card to put on the placeDeck.
     * @param colorWish The color wished if the Card is a Jack.
     * @return True if card was placed on the placeDeck.
     */
    public boolean putCard(Card cardToPut, Optional<ColorEnum> colorWish) {
        if (!playable) {
            return false;
        }
        if (!mauMauRule.checkPut(cardToPut, colorWish)) {
            return false;
        }
        else {

            try {

                if (cardToPut.getValue() != ValueEnum.SEVEN && sevenCounter != 0) {
                    return false;
                }
                //Spieler zug ist valide, versuche Karte aus Spieler Inventar zu ziehen und auf stapel abzulegen.
                Card card = (Card) getCurrentPlayer().takeFromInventory(cardToPut);
                placeDeck.pushCard(card);

                switch (card.getValue()) {
                    case SEVEN -> {
                        sevenCounter++;
                        mauMauRule.setValidColor(card.getColor());
                        mauMauRule.setValidNumber(card.getValue());
                        winner = evaluateWinner();
                        hasPut = true;
                        selectNextPlayer();
                    }
                    case JACK -> {
                        if (colorWish.isEmpty()) {
                            throw new AssertionError("If jack was played, there must be a wish.");
                        }
                        mauMauRule.setValidColor(colorWish.get());
                        mauMauRule.setValidNumber(ValueEnum.JACK);
                        winner = evaluateWinner();
                        hasPut = true;
                        selectNextPlayer();
                    }
                    // Potentielle Sonderregel
                    case ACE -> {
                        mauMauRule.setValidColor(card.getColor());
                        mauMauRule.setValidNumber(card.getValue());
                        winner = evaluateWinner();
                        hasPut = true;
                        selectNextPlayer();
                    }
                    case EIGHT -> {
                        mauMauRule.setValidColor(card.getColor());
                        mauMauRule.setValidNumber(card.getValue());
                        winner = evaluateWinner();
                        hasPut = true;
                        selectNextPlayer();
                        hasPut = true;
                        selectNextPlayer();
                    }
                    default -> {
                        mauMauRule.setValidColor(card.getColor());
                        mauMauRule.setValidNumber(card.getValue());
                        winner = evaluateWinner();
                        hasPut = true;
                        selectNextPlayer();
                    }
                }

                return true;

            }
            catch (IndexOutOfBoundsException e) {
                // Crasht mit IndexOutOfBounds da .get(-1) da Spieler Inventar karte nicht hat und index somit -1
                return false;
            }
        }
    }


    private void reshuffleDeck() {
        CardDeck temp = CardDeckFactory.createCardDeck(CardSpecEnum.EMPTY);
        Card topCard = placeDeck.popCard();
        while (placeDeck.getDeckSize() != 0) {
            temp.pushCard(placeDeck.popCard());
        }
        temp.shuffle();
        while (drawDeck.getDeckSize() > 0) {
            temp.pushCard(drawDeck.popCard());
        }
        placeDeck.pushCard(topCard);
        drawDeck = temp;
    }


    private APlayer evaluateWinner() {
        if (winner != null) {
            return winner;
        }
        if (getCurrentPlayer().getInvSize() == 0) {
            playable = false;
            //TODO for multi rounds this is different!
            gameFinished = true;
            return getCurrentPlayer();
        }
        return null;

    }

    /**
     * Reset round.
     */
    public void resetRound() {
        for (APlayer p : getPlayerList()) {
            p.clearInventory();
        }
        if (getPlayerList().size() > 4) {
            this.drawDeck = CardDeckFactory.createCardDeck(CardSpecEnum.THIRTY_TWO_TWICE);
        }
        else {
            this.drawDeck = CardDeckFactory.createCardDeck(CardSpecEnum.THIRTY_TWO_PIQUET);
        }
        drawDeck.shuffle();
        mauMauRule.setValidNumber(null);
        mauMauRule.setValidColor(null);
        initialiseRound();
        roundCounter++;
    }

    //Karten werden verteilt und auf den stapel gelegt.
    private void initialiseRound() {
        for (APlayer p : getPlayerList()) {
            for (int i = 0; i < 5; i++) {
                p.putInInventory(drawDeck.popCard());
            }
        }
        Card firstCard = drawDeck.popCard();
        placeDeck.pushCard(firstCard);

        mauMauRule.setValidNumber(firstCard.getValue());
        mauMauRule.setValidColor(firstCard.getColor());

        playable = true;
        winner = null;
        hasDrawn = false;
        hasPut = false;
    }


    /**
     * Gets draw deck.
     *
     * @return the draw deck
     */
    public CardDeck getDrawDeck() {
        return drawDeck;
    }

    /**
     * Gets place deck.
     *
     * @return the place deck
     */
    public CardDeck getPlaceDeck() {
        return placeDeck;
    }

    /**
     * Is has drawn boolean.
     *
     * @return the boolean
     */
    public boolean isHasDrawn() {
        return hasDrawn;
    }

    /**
     * Is playable boolean.
     *
     * @return the boolean
     */
    public boolean isPlayable() {
        return playable;
    }

    /**
     * Gets winner.
     *
     * @return the winner
     */
    public APlayer getWinner() {
        return winner;
    }

    /**
     * Gets mau mau rule.
     *
     * @return the mau mau rule
     */
    public MauMauRules getMauMauRule() {
        return mauMauRule;
    }

    @Override
    public boolean selectNextPlayer() {
        if (hasDrawn || hasPut || drawDeck.getDeckSize() == 0) {
            hasDrawn = false;
            hasPut = false;
            if (drawDeck.getDeckSize() < 2) {
                reshuffleDeck();
            }
            return super.selectNextPlayer();
        }
        return false;
    }

    @Override
    public int getMinPlayer() {
        return MINIMUM_AMOUNT_PLAYERS;
    }

    @Override
    public int getMaxPlayer() {
        return MAXIMUM_AMOUNT_PLAYERS;
    }
}
