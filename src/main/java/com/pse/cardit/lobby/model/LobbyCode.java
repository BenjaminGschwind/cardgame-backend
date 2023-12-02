package com.pse.cardit.lobby.model;


import java.util.Random;

public record LobbyCode(String identifier) {
    private static final String LEGAL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int AMOUNT_CHARACTERS = 6;
    private static final String REGEX_FORMAT = "[" + LEGAL_CHARACTERS + "]{" + AMOUNT_CHARACTERS + "}";
    private static Random random = null;

    public LobbyCode {
        if (!identifier.matches(REGEX_FORMAT)) {
            throw new IllegalArgumentException();
        }
    }

    public static LobbyCode generateRandom() {
        Random rnd = getRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < AMOUNT_CHARACTERS; i++) {
            builder.append(LEGAL_CHARACTERS.charAt(rnd.nextInt(LEGAL_CHARACTERS.length())));
        }
        return new LobbyCode(builder.toString());
    }

    public boolean matchesFormat(String string) {
        return string.matches(REGEX_FORMAT);
    }

    @Override
    public String toString() {
        return identifier;
    }

    private static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }
}
