package com.playshogi.library.shogi.models.moves;

public class MoveUtils {

    public static boolean isSilentMove(final Move move) {
        return move instanceof SpecialMove && ((SpecialMove) move).getSpecialMoveType() == SpecialMoveType.SILENT;
    }

    public static ShogiMove opposite(final ShogiMove move) {
        if (move instanceof SpecialMove) {
            return move;
        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;
            return new DropMove(dropMove.getPlayer().opposite(), dropMove.getPieceType(),
                    dropMove.getToSquare().opposite());
        } else if (move instanceof CaptureMove) {
            CaptureMove captureMove = (CaptureMove) move;
            return new CaptureMove(captureMove.getPiece().opposite(), captureMove.getFromSquare().opposite(),
                    captureMove.getToSquare().opposite(), captureMove.getCapturedPiece().opposite(),
                    captureMove.isPromote());
        } else if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;
            return new NormalMove(normalMove.getPiece().opposite(), normalMove.getFromSquare().opposite(),
                    normalMove.getToSquare().opposite(), normalMove.isPromote());
        } else {
            throw new IllegalStateException("Unrecognized move: " + move);
        }
    }
}
