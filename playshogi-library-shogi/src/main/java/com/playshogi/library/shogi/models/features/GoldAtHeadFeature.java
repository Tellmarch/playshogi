package com.playshogi.library.shogi.models.features;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Optional;

public enum GoldAtHeadFeature implements Feature {
    INSTANCE;

    @Override
    public boolean hasFeature(GameRecord gameRecord) {
        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameRecord.getGameTree());

        gameNavigation.moveToEndOfVariation();
        Move lastMove = gameNavigation.getCurrentMove();

        if (lastMove != null) {
            if (lastMove instanceof DropMove) {
                DropMove dropMove = (DropMove) lastMove;
                if (dropMove.getPieceType() == PieceType.GOLD) {
                    ShogiPosition position = gameNavigation.getPosition();
                    Optional<Square> aboveDropSquare = dropMove.getToSquare().above();
                    // add sente's piece requirement and condition for sente king too.
                    return aboveDropSquare.isPresent() && position.getPieceAt(aboveDropSquare.get()).orElse(null) == Piece.GOTE_KING;
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
