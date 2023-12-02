package com.pse.cardit.game.model.holdable;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * CardDeck is a concrete class the represents a collection of Cards with the access logic of a Stack.
 */
public class CardDeck {
    private final LinkedList<Card> cardStack;

    /**
     * Instantiates a new Card deck.
     */
    public CardDeck() {
        this.cardStack = new LinkedList<>();
    }

    /**
     * Shuffles all the cards in the cardStack so they are in random order.
     */
    public void shuffle() {
        Collections.shuffle(cardStack);
    }

    /**
     * Pushes the given card to the top of the cardStack.
     *
     * @param card The card to push.
     */
    public void pushCard(Card card) {
        cardStack.addFirst(card);
    }

    /**
     * Removes and returns the top Card of the cardStack.
     *
     * @return The top Card.
     */
    public Card popCard() {
        return cardStack.removeFirst();
    }

    /**
     * Returns the top Card of the cardStack without removing the card from the cardStack.
     *
     * @return The top card.
     */
    public Card peekCard() {
        return cardStack.peekFirst();
    }

    /**
     * Returns the size of cardStack.
     *
     * @return The size.
     */
    public int getDeckSize() {
        return cardStack.size();
    }

    /**
     * Gets card stack.
     *
     * @return The card stack.
     */
    public Deque<Card> getCardStack() {
        return cardStack;
    }
}
