package com.pse.cardit.game.model;

import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.model.holdable.ColorEnum;
import com.pse.cardit.game.model.holdable.ValueEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CardTest {

    @ParameterizedTest
    @MethodSource("getCards")
    void newCard(String code, boolean expected) {
        if (expected) {
            assertDoesNotThrow(() -> new Card(code), "The code is valid");
        }
        else {
            assertThrows(IllegalArgumentException.class, () -> new Card(code), "The code is invalid");
        }
    }

    @ParameterizedTest
    @MethodSource("getCards")
    void regexTest(String code, boolean expected) {
        if (code == null) {
            return;
        }
        assertEquals(expected, code.matches(Card.REGEX_FORMAT), "Was expecting " + code + " to return "
                + expected);
    }


    static List<Arguments> getCards() {
        List<Arguments> result = legalCards();
        result.addAll(illegalCards());
        return result;
    }

    static List<Arguments> legalCards() {
        List<Arguments> result = new ArrayList<>();
        for (ColorEnum color : ColorEnum.values()) {
            for (ValueEnum value : ValueEnum.values()) {
                result.add(Arguments.of(color.getCodec() + ":" + value.getCodec(), true));
            }
        }
        return result;
    }

    static List<Arguments> illegalCards() {
        return List.of(
                Arguments.of("HerzensAcht", false),
                Arguments.of("Herzens:Acht", false),
                Arguments.of("C:1", false),
                Arguments.of("H:0", false),
                Arguments.of("D:D", false),
                Arguments.of("S:B", false),
                Arguments.of("Herz:8", false),
                Arguments.of("H:Queen", false),
                Arguments.of("", false),
                Arguments.of(null, false)

        );
    }

}