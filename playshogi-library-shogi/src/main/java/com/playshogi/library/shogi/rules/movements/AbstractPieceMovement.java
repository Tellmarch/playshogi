package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractPieceMovement implements PieceMovement {

    private final int[][] allowedDColDRow;

    public AbstractPieceMovement(final int[][] allowedDColDRow) {
        this.allowedDColDRow = allowedDColDRow;
    }

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<>(allowedDColDRow.length);

        for (int[] dColRow : allowedDColDRow) {
            Optional<Square> square = PieceMovementsUtils.getSquare(boardState, from.getColumn() + dColRow[0],
                    from.getRow() + dColRow[1]);
            if (square.isPresent() && boardState.isSquareEmptyOrGote(square.get())) {
                result.add(square.get());
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
