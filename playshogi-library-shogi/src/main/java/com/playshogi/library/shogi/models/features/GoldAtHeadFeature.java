package com.playshogi.library.shogi.models.features;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.ShogiUtils;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public enum GoldAtHeadFeature implements Feature {
    INSTANCE;

    @Override
    public boolean hasFeature(GameRecord gameRecord) {
        GameNavigation<ShogiPosition> gameNavigation = ShogiUtils.getNavigation(gameRecord);

        gameNavigation.moveToEndOfVariation();
        Move lastMove = gameNavigation.getCurrentMove();

        if (lastMove != null) {
            if (lastMove instanceof DropMove) {
                DropMove dropMove = (DropMove) lastMove;
                if (dropMove.getPieceType() == PieceType.GOLD) {
                    ShogiPosition position = gameNavigation.getPosition();
                    Square dropSquare = dropMove.getToSquare();
                    return dropSquare.getRow() > 1 && position.getPieceAt(Square.of(dropSquare.getColumn(),
                            dropSquare.getRow() - 1)) == Piece.GOTE_KING; //add sente's piece requirement and condition for sente king too.
                }
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "Gold at head";
    }

    @Override
    public String getDescription() {
        return "Ends with a mate by a gold at the head of the king";
    }
}
