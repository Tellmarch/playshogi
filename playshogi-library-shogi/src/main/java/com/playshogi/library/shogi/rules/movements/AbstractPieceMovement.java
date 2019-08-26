package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPieceMovement implements PieceMovement {

    private final int[][] allowedDColDRow;

    public AbstractPieceMovement(final int[][] allowedDColDRow) {
        this.allowedDColDRow = allowedDColDRow;
    }

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<>(allowedDColDRow.length);

        for (int[] dColRow : allowedDColDRow) {
            Square square = PieceMovementsUtils.getSquare(boardState, from.getColumn() + dColRow[0],
                    from.getRow() + dColRow[1]);
            if (square != null && boardState.isSquareEmptyOrGote(square)) {
                result.add(square);
            }
        }

        return result;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        int dRow = to.getRow() - from.getRow();
        int dCol = to.getColumn() - from.getColumn();

        for (int[] dColRow : allowedDColDRow) {
            if (dCol == dColRow[0] && dRow == dColRow[1]) {
                return true;
            }
        }
        return false;
    }

}
