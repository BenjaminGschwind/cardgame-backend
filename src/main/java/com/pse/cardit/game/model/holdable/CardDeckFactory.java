package com.pse.cardit.game.model.holdable;


/**
 * CardDeckFactory is a concrete class that provides a static method that generates a CardDeck,
 * thus realising a factory method pattern.
 */
public final class CardDeckFactory {

    private CardDeckFactory() {

    }

    /**
     * Generates and returns a CardDeck based on the given specification.
     *
     * @param cardSpec The specification for the CardDeck.
     * @return The CardDeck object.
     */
    public static CardDeck createCardDeck(CardSpecEnum cardSpec) {
        CardDeck cardDeck = new CardDeck();
        switch (cardSpec) {
            case THIRTY_TWO_PIQUET -> {
                return thirtyTwoPiquet();
            }
            case THIRTY_TWO_TWICE -> {
                return thirtyTwoTwice();
            }
            case FIFTY_TWO_CARD -> {
                return fiftyTwoCard();
            }
            default -> {
                return cardDeck;
            }
        }
    }

    private static CardDeck fiftyTwoCard() {
        CardDeck cardDeck = new CardDeck();
        for (ColorEnum color : ColorEnum.values()) {
            for (ValueEnum value : ValueEnum.values()) {
                cardDeck.pushCard(new Card(color, value));
            }
        }
        return cardDeck;
    }

    private static CardDeck thirtyTwoTwice() {
        CardDeck cardDeck = new CardDeck();
        for (int d = 0; d < 2; d++) {
            for (ColorEnum color : ColorEnum.values()) {
                for (int i = 5; i < ValueEnum.values().length; i++) {
                    cardDeck.pushCard(new Card(color, ValueEnum.values()[i]));
                }
            }
        }
        return cardDeck;
    }

    private static CardDeck thirtyTwoPiquet() {
        CardDeck cardDeck = new CardDeck();
        for (ColorEnum color : ColorEnum.values()) {
            for (int i = 5; i < ValueEnum.values().length; i++) {
                cardDeck.pushCard(new Card(color, ValueEnum.values()[i]));
            }
        }
        return cardDeck;
    }
}

