package com.playshogi.library.shogi.models.moves;

public enum SpecialMoveType {
    RESIGN, SENNICHITE, JISHOGI, ILLEGAL_MOVE, BREAK, TIMEOUT, CHECKMATE, OTHER;

    public boolean isLosingMove() {
        return this == RESIGN || this == ILLEGAL_MOVE || this == TIMEOUT || this == CHECKMATE;
    }
}
