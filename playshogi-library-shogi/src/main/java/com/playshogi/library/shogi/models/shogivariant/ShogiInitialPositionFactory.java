package com.playshogi.library.shogi.models.shogivariant;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.position.MutableShogiBoardState;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class ShogiInitialPositionFactory {

    public static final ReadOnlyShogiPosition READ_ONLY_INITIAL_POSITION = createInitialPosition();

    public static ShogiPosition createInitialPosition() {
        return createInitialPosition(Handicap.EVEN);
    }

    public static ShogiPosition createInitialPosition(final Handicap handicap) {
        ShogiPosition shogiPosition = new ShogiPosition(ShogiVariant.NORMAL_SHOGI);
        MutableShogiBoardState shogiBoardState = shogiPosition.getMutableShogiBoardState();

        setWhitePieces(shogiBoardState, handicap);
        setBlackPieces(shogiBoardState);

        if (handicap == Handicap.THREE_PAWNS) {
            shogiPosition.getMutableGoteKomadai().addPiece(PieceType.PAWN);
            shogiPosition.getMutableGoteKomadai().addPiece(PieceType.PAWN);
            shogiPosition.getMutableGoteKomadai().addPiece(PieceType.PAWN);
        }

        if (handicap != Handicap.EVEN && handicap != Handicap.SENTE) {
            shogiPosition.setPlayerToMove(Player.WHITE);
        }

        return shogiPosition;
    }

    private static void setWhitePieces(final MutableShogiBoardState shogiBoardState, final Handicap handicap) {
        shogiBoardState.setPieceAt(5, 1, Piece.GOTE_KING);

        if (handicap == Handicap.NAKED_KING || handicap == Handicap.THREE_PAWNS) {
            return;
        }

        shogiBoardState.setPieceAt(1, 1, Piece.GOTE_LANCE);
        shogiBoardState.setPieceAt(2, 1, Piece.GOTE_KNIGHT);
        shogiBoardState.setPieceAt(3, 1, Piece.GOTE_SILVER);
        shogiBoardState.setPieceAt(4, 1, Piece.GOTE_GOLD);
        shogiBoardState.setPieceAt(6, 1, Piece.GOTE_GOLD);
        shogiBoardState.setPieceAt(7, 1, Piece.GOTE_SILVER);
        shogiBoardState.setPieceAt(8, 1, Piece.GOTE_KNIGHT);
        shogiBoardState.setPieceAt(9, 1, Piece.GOTE_LANCE);

        shogiBoardState.setPieceAt(2, 2, Piece.GOTE_BISHOP);
        shogiBoardState.setPieceAt(8, 2, Piece.GOTE_ROOK);

        for (int i = 1; i <= 9; i++) {
            shogiBoardState.setPieceAt(i, 3, Piece.GOTE_PAWN);
        }

        if (handicap != Handicap.EVEN) {
            switch (handicap) {
                case SENTE:
                    break;
                case LANCE:
                    shogiBoardState.setPieceAt(1, 1, null);
                    break;
                case BISHOP:
                    shogiBoardState.setPieceAt(2, 2, null);
                    break;
                case ROOK:
                    shogiBoardState.setPieceAt(8, 2, null);
                    break;
                case ROOK_LANCE:
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    break;
                case TWO_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    break;
                case FOUR_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(9, 1, null);
                    break;
                case SIX_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(9, 1, null);
                    shogiBoardState.setPieceAt(2, 1, null);
                    shogiBoardState.setPieceAt(8, 1, null);
                    break;
                case EIGHT_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(9, 1, null);
                    shogiBoardState.setPieceAt(2, 1, null);
                    shogiBoardState.setPieceAt(8, 1, null);
                    shogiBoardState.setPieceAt(3, 1, null);
                    shogiBoardState.setPieceAt(7, 1, null);
                    break;
                case NINE_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(9, 1, null);
                    shogiBoardState.setPieceAt(2, 1, null);
                    shogiBoardState.setPieceAt(8, 1, null);
                    shogiBoardState.setPieceAt(3, 1, null);
                    shogiBoardState.setPieceAt(7, 1, null);
                    shogiBoardState.setPieceAt(4, 1, null);
                    break;
                case TEN_PIECES:
                    shogiBoardState.setPieceAt(2, 2, null);
                    shogiBoardState.setPieceAt(8, 2, null);
                    shogiBoardState.setPieceAt(1, 1, null);
                    shogiBoardState.setPieceAt(9, 1, null);
                    shogiBoardState.setPieceAt(2, 1, null);
                    shogiBoardState.setPieceAt(8, 1, null);
                    shogiBoardState.setPieceAt(3, 1, null);
                    shogiBoardState.setPieceAt(7, 1, null);
                    shogiBoardState.setPieceAt(4, 1, null);
                    shogiBoardState.setPieceAt(6, 1, null);
                    break;
                default:
                    throw new IllegalStateException("Unexpected handicap: " + handicap);
            }
        }
    }

    private static void setBlackPieces(final MutableShogiBoardState shogiBoardState) {
        shogiBoardState.setPieceAt(1, 9, Piece.SENTE_LANCE);
        shogiBoardState.setPieceAt(2, 9, Piece.SENTE_KNIGHT);
        shogiBoardState.setPieceAt(3, 9, Piece.SENTE_SILVER);
        shogiBoardState.setPieceAt(4, 9, Piece.SENTE_GOLD);
        shogiBoardState.setPieceAt(5, 9, Piece.SENTE_KING);
        shogiBoardState.setPieceAt(6, 9, Piece.SENTE_GOLD);
        shogiBoardState.setPieceAt(7, 9, Piece.SENTE_SILVER);
        shogiBoardState.setPieceAt(8, 9, Piece.SENTE_KNIGHT);
        shogiBoardState.setPieceAt(9, 9, Piece.SENTE_LANCE);

        shogiBoardState.setPieceAt(8, 8, Piece.SENTE_BISHOP);
        shogiBoardState.setPieceAt(2, 8, Piece.SENTE_ROOK);

        for (int i = 1; i <= 9; i++) {
            shogiBoardState.setPieceAt(i, 7, Piece.SENTE_PAWN);
        }
    }

    public static ShogiPosition createEmptyTsumePosition(final boolean withSenteKing) {
        ShogiPosition position = new ShogiPosition();
        position.getMutableShogiBoardState().setPieceAt(5, 1, Piece.GOTE_KING);
        if (withSenteKing) {
            position.getMutableShogiBoardState().setPieceAt(5, 9, Piece.SENTE_KING);
        }
        position.fillGoteKomadaiWithMissingPieces();
        return position;

    }
}
