package com.playshogi.website.gwt.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface PlayMessages extends Messages {
    @DefaultMessage("Even")
    String handicapEven();

    @DefaultMessage("Sente")
    String handicapSente();

    @DefaultMessage("Lance")
    String handicapLance();

    @DefaultMessage("Bishop")
    String handicapBishop();

    @DefaultMessage("Rook")
    String handicapRook();

    @DefaultMessage("Rook + Lance")
    String handicapRookLance();

    @DefaultMessage("Two pieces")
    String handicapTwoPieces();

    @DefaultMessage("Four pieces")
    String handicapFourPieces();

    @DefaultMessage("Six pieces")
    String handicapSixPieces();

    @DefaultMessage("Eight pieces")
    String handicapEightPieces();

    @DefaultMessage("Nine pieces")
    String handicapNinePieces();

    @DefaultMessage("Ten pieces")
    String handicapTenPieces();

    @DefaultMessage("Three Pawns")
    String handicapThreePawns();

    @DefaultMessage("Naked King")
    String handicapNakedKing();

    @DefaultMessage("Level")
    String handicapLevel();

}
