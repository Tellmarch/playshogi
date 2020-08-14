package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PawnMovement implements PieceMovement {

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState position, final Square from) {
        if (from.getRow() != ShogiBoardState.FIRST_ROW) {
            Optional<Piece> piece = position.getPieceAt(from.getColumn(), from.getRow() - 1);
            if (!piece.isPresent() || !piece.get().isSentePiece()) {
                return Collections.singletonList(Square.of(from.getColumn(), from.getRow() - 1));
            }
        }
        return Collections.emptyList();

    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState position, final Square from, final Square to) {
        return from.getColumn() == to.getColumn() && from.getRow() == to.getRow() + 1;
    }

    @Override
    public boolean isDropValid(final ShogiBoardState position, final Square to) {
        return to.getRow() != ShogiBoardState.FIRST_ROW && !position.hasPlayerPawnOnColumn(true, to.getColumn());
    }

    @Override
    public boolean isUnpromoteValid(final ShogiBoardState position, final Square to) {
        return to.getRow() != ShogiBoardState.FIRST_ROW;
    }

}
