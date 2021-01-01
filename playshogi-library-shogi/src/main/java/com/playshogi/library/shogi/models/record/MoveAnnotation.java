package com.playshogi.library.shogi.models.record;

public enum MoveAnnotation {
    NONE(""),
    GOOD("!"),
    BRILLIANT("!!"),
    MISTAKE("?"),
    BLUNDER("??"),
    INTERESTING("!?"),
    DUBIOUS("?!"),
    ;

    private final String shortString;

    MoveAnnotation(final String shortString) {
        this.shortString = shortString;
    }

    public String getShortString() {
        return shortString;
    }
}
