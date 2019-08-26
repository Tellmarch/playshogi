package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.ArrayList;
import java.util.List;

public class KnightMovement implements PieceMovement {

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<>(2);
        int toRow = from.getRow() - 2;
        if (toRow < ShogiBoardState.FIRST_ROW) {
            return result;
        }
        if (from.getColumn() != ShogiBoardState.FIRST_COLUMN) {
            Square square = Square.of(from.getColumn() - 1, toRow);
            if (boardState.isSquareEmptyOrGote(square)) {
                result.add(square);
            }
        }
        if (from.getColumn() != boardState.getWidth()) {
            Square square = Square.of(from.getColumn() + 1, toRow);
            if (boardState.isSquareEmptyOrGote(square)) {
                result.add(square);
            }
        }
        return result;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        return (from.getRow() == to.getRow() + 2)
                && (from.getColumn() == to.getColumn() + 1 || from.getColumn() == to.getColumn() - 1);
    }

    @Override
    public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
        return to.getRow() >= ShogiBoardState.FIRST_ROW + 2;
    }

}
