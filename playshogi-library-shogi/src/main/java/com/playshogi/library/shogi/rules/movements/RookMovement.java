package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.ArrayList;
import java.util.List;

public class RookMovement implements PieceMovement {

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<Square>();
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, -1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, +1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, 0, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, 0, result);
        return result;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {

        if (to.getColumn() == from.getColumn() && to.getRow() < from.getRow()) {
            for (int row = from.getRow() - 1; row > to.getRow(); row--) {
                if (boardState.getPieceAt(from.getColumn(), row) != null) {
                    return false;
                }
            }
            return true;
        } else if (to.getColumn() == from.getColumn() && to.getRow() > from.getRow()) {
            for (int row = from.getRow() + 1; row < to.getRow(); row++) {
                if (boardState.getPieceAt(from.getColumn(), row) != null) {
                    return false;
                }
            }
            return true;
        } else if (to.getColumn() < from.getColumn() && to.getRow() == from.getRow()) {
            for (int col = from.getColumn() - 1; col > to.getColumn(); col--) {
                if (boardState.getPieceAt(col, from.getRow()) != null) {
                    return false;
                }
            }
            return true;
        } else if (to.getColumn() > from.getColumn() && to.getRow() == from.getRow()) {
            for (int col = from.getColumn() + 1; col < to.getColumn(); col++) {
                if (boardState.getPieceAt(col, from.getRow()) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;

    }

    @Override
    public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
        return true;
    }

}
