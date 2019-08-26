package com.playshogi.library.models;

public interface Position<P extends Position<?>> {

    P clonePosition();

}
