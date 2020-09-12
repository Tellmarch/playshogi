package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.shogi.models.Player;

public class SpecialMove extends ShogiMove {

    private final SpecialMoveType specialMoveType;

    @Deprecated
    public SpecialMove(final boolean senteMoving, final SpecialMoveType specialMoveType) {
        this(senteMoving ? Player.BLACK : Player.WHITE, specialMoveType);
    }

    public SpecialMove(final Player player, final SpecialMoveType specialMoveType) {
        super(player);
        this.specialMoveType = specialMoveType;
    }

    public SpecialMoveType getSpecialMoveType() {
        return specialMoveType;
    }

    @Override
    public boolean isEndMove() {
        return true;
    }
}
