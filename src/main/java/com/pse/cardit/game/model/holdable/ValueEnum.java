package com.pse.cardit.game.model.holdable;

/**
 * The enum containing the possible values a Card can have.
 */
public enum ValueEnum {
    /**
     * Two value.
     */
    TWO("2"),
    /**
     * Three value.
     */
    THREE("3"),
    /**
     * Four value
     */
    FOUR("4"),
    /**
     * Five value.
     */
    FIVE("5"),
    /**
     * Six value.
     */
    SIX("6"),
    /**
     * Seven value.
     */
    SEVEN("7"),
    /**
     * Eight value.
     */
    EIGHT("8"),
    /**
     * Nine value.
     */
    NINE("9"),
    /**
     * Ten value.
     */
    TEN("10"),
    /**
     * Jack value.
     */
    JACK("J"),
    /**
     * Queen value.
     */
    QUEEN("Q"),
    /**
     * King value.
     */
    KING("K"),
    /**
     * Ace value.
     */
    ACE("A");

    private final String codec;

    ValueEnum(String codec) {
        this.codec = codec;
    }

    /**
     * Gets codec.
     *
     * @return the codec
     */
    public String getCodec() {
        return codec;
    }

    /**
     * Gets enum by codec.
     *
     * @param codec the codec
     * @return the enum by codec
     */
    public static ValueEnum getEnumByCodec(String codec) {

        for (ValueEnum c: values()) {
            if (c.codec.equals(codec)) return c;
        }
        throw new IllegalArgumentException(codec);
    }
}
