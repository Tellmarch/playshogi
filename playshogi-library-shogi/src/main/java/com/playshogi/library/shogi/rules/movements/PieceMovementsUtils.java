package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PieceMovementsUtils {
    public static boolean isAlongDirection(final ShogiBoardState boardState, final Square from, final Square to) {
        // If destination square is occupied by a friendly (sente) piece, destination square is invalid
        if (boardState.getPieceAt(to).filter(Piece::isSentePiece).isPresent()) {
            return false;
        }
        int dRow = Integer.compare(to.getRow(), from.getRow());
        int dColumn = Integer.compare(to.getColumn(), from.getColumn());
        return addSquaresAlongDirection(boardState, from, dColumn, dRow, new ArrayList<>()).contains(to);
    }

    /**
     * Appends squares along the given direction to the given square list
     * @param boardState Board which may have occupied squares
     * @param from Origin square (not to be appended)
     * @param dCol Horizontal step size
     * @param dRow Vertical step size
     * @param result Target list for append operation
     * @return List of squares
     */
    public static List<Square> addSquaresAlongDirection(final ShogiBoardState boardState, final Square from, final int dCol,
                                                final int dRow, final List<Square> result) {
        if (dCol != 0 || dRow != 0) {
            int row = from.getRow();
            int col = from.getColumn();
            Optional<Square> square;

            while ((square = getSquare(boardState, col += dCol, row += dRow)).isPresent()) {
                Optional<Piece> piece = boardState.getPieceAt(col, row);
                if (piece.filter(Piece::isSentePiece).isPresent()) {
                    break;
                }
                result.add(square.get());
                if (piece.isPresent() && !piece.get().isSentePiece()) {
                    break;
                }
            }
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
