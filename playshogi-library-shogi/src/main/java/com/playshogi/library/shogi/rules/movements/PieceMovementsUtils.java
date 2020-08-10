package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.List;
import java.util.Optional;

public class PieceMovementsUtils {
    public static List<Square> addSquaresAlongDirection(final ShogiBoardState boardState, final Square from, final int dCol,
                                                final int dRow, final List<Square> result) {
        int row = from.getRow() + dRow;
        int col = from.getColumn() + dCol;
        Optional<Square> square = getSquare(boardState, col, row);

        while (square.isPresent()) {
            Optional<Piece> piece = boardState.getPieceAt(col, row);

            if (piece.isPresent() && piece.get().isSentePiece()) {
                break;
            }

            result.add(square.get());

            if (piece.isPresent() && !piece.get().isSentePiece()) {
                break;
            }

            row += dRow;
            col += dCol;
            square = getSquare(boardState, col, row);
        }
        return result;
    }

    public static Optional<Square> getSquare(final ShogiBoardState boardState, final int column, final int row) {
        if (column >= ShogiBoardState.FIRST_COLUMN && column <= boardState.getWidth()
                && row >= ShogiBoardState.FIRST_ROW && row <= boardState.getHeight()) {
            return Optional.of(Square.of(column, row));
        } else {
            return Optional.empty();
        }
    }

}
