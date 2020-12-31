package com.playshogi.library.shogi.models.moves;

public interface Move {

    default boolean isEndMove() {
        return false;
    }

}
