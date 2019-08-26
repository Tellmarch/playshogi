package com.playshogi.library.shogi.models.moves;

public class SpecialMove extends ShogiMove {

    private final SpecialMoveType specialMoveType;

    public SpecialMove(final boolean senteMoving, final SpecialMoveType specialMoveType) {
        super(senteMoving);
        this.specialMoveType = specialMoveType;
    }

    public SpecialMoveType getSpecialMoveType() {
        return specialMoveType;
    }
}
