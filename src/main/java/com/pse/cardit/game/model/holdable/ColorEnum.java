package com.pse.cardit.game.model.holdable;

/**
 * The enum containing the possible colors a Card can have.
 */
public enum ColorEnum {
    /**
     * Clubs color value.
     */
    CLUBS("C"),
    /**
     * Diamonds color value.
     */
    DIAMONDS("D"),
    /**
     * Hearts color value.
     */
    HEARTS("H"),
    /**
     * Spades color value.
     */
    SPADES("S");

    private final String codec;

    ColorEnum(String codec) {
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
    public static ColorEnum getEnumByCodec(String codec) {

        for (ColorEnum c: values()) {
            if (c.codec.equals(codec)) return c;
        }
        throw new IllegalArgumentException(codec);
    }
}
