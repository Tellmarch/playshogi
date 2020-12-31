package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.Player;

import java.util.Optional;

public interface ShogiBoardState {
    int FIRST_ROW = 1;
    int FIRST_COLUMN = 1;

    Optional<Piece> getPieceAt(int column, int row);

    Optional<Piece> getPieceAt(Square square);

    int getWidth();

    int getHeight();

    ShogiBoardState opposite();

    boolean hasPlayerPawnOnColumn(Player player, int column);

    boolean isSquareEmptyOrGote(Square square);

    int getLastRow();

    int getLastColumn();
}
