package com.pse.cardit.game.model.holdable;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Card is concrete class implementing the IHoldable interface that provides
 * the representation of a card consisting of a color and a value.
 */
public class Card implements IHoldable {

    private static final String SEPARATOR = ":";
    public static final String REGEX_FORMAT = "[HSCD]" + SEPARATOR + "([2-9]|10|[JQKA])";
    private static final String ILLEGAL_FORMAT_ERROR = "Error - Cards should follow the COLOR:VALUE format!";
    private static final String NULL_POINTER_ERROR = "Error - Cards should not be null!";

    private final ColorEnum color;
    private final ValueEnum value;

    /**
     * Instantiates a new Card with given Color and Value.
     *
     * @param color The color.
     * @param value The value.
     */
    public Card(ColorEnum color, ValueEnum value) {
        this.color = color;
        this.value = value;
    }

    /**
     * Instantiates a new Card with a given String codec of color and value.
     *
     * @param codec The codec.
     */
    public Card(String codec) {
        if (codec == null) {
            throw new IllegalArgumentException(NULL_POINTER_ERROR);
        }
        String[] split =  codec.split(SEPARATOR);
        if (split.length != StringUtils.countOccurrencesOf(REGEX_FORMAT, SEPARATOR) + 1) {
            throw new IllegalArgumentException(ILLEGAL_FORMAT_ERROR);
        }
        String colorCodec = split[0];
        String valueCodec = split[1];
        this.color = ColorEnum.getEnumByCodec(colorCodec);
        this.value = ValueEnum.getEnumByCodec(valueCodec);
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public ColorEnum getColor() {
        return color;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public ValueEnum getValue() {
        return value;
    }

    @Override
    public String toString() {
        return color.getCodec() + ":" + value.getCodec();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return color == card.color && value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }
}

