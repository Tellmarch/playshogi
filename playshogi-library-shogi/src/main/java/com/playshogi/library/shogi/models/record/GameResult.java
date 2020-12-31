package com.playshogi.library.shogi.models.record;

public enum GameResult {
    BLACK_WIN, // Black may be sente or the handicap receiver
    WHITE_WIN, // White may be gote or the handicap giver
    OTHER,
    UNKNOWN
}
