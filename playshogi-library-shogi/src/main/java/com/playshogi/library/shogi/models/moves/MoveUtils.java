package com.playshogi.library.shogi.models.moves;

public class MoveUtils {

    public static boolean isSilentMove(final Move move) {
        return move instanceof SpecialMove && ((SpecialMove) move).getSpecialMoveType() == SpecialMoveType.SILENT;
    }

}
