package com.playshogi.library.models;

public interface Move {

    default boolean isEndMove() {
        return false;
    }

}
