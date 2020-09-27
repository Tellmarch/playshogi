package com.playshogi.library.shogi.models;

import static com.playshogi.library.shogi.models.Player.BLACK;
import static com.playshogi.library.shogi.models.Player.WHITE;

public enum Piece {
    SENTE_PROMOTED_PAWN(BLACK, PieceType.PAWN, true),
    SENTE_PROMOTED_LANCE(BLACK, PieceType.LANCE, true),
    SENTE_PROMOTED_KNIGHT(BLACK, PieceType.KNIGHT, true),
    SENTE_PROMOTED_SILVER(BLACK, PieceType.SILVER, true),
    SENTE_PROMOTED_BISHOP(BLACK, PieceType.BISHOP, true),
    SENTE_PROMOTED_ROOK(BLACK, PieceType.ROOK, true),

    SENTE_PAWN(BLACK, PieceType.PAWN, false, SENTE_PROMOTED_PAWN),
    SENTE_LANCE(BLACK, PieceType.LANCE, false, SENTE_PROMOTED_LANCE),
    SENTE_KNIGHT(BLACK, PieceType.KNIGHT, false, SENTE_PROMOTED_KNIGHT),
    SENTE_SILVER(BLACK, PieceType.SILVER, false, SENTE_PROMOTED_SILVER),
    SENTE_GOLD(BLACK, PieceType.GOLD, false),
    SENTE_BISHOP(BLACK, PieceType.BISHOP, false, SENTE_PROMOTED_BISHOP),
    SENTE_ROOK(BLACK, PieceType.ROOK, false, SENTE_PROMOTED_ROOK),
    SENTE_KING(BLACK, PieceType.KING, false),

    GOTE_PROMOTED_PAWN(WHITE, PieceType.PAWN, true),
    GOTE_PROMOTED_LANCE(WHITE, PieceType.LANCE, true),
    GOTE_PROMOTED_KNIGHT(WHITE, PieceType.KNIGHT, true),
    GOTE_PROMOTED_SILVER(WHITE, PieceType.SILVER, true),
    GOTE_PROMOTED_BISHOP(WHITE, PieceType.BISHOP, true),
    GOTE_PROMOTED_ROOK(WHITE, PieceType.ROOK, true),

    GOTE_PAWN(WHITE, PieceType.PAWN, false, GOTE_PROMOTED_PAWN),
    GOTE_LANCE(WHITE, PieceType.LANCE, false, GOTE_PROMOTED_LANCE),
    GOTE_KNIGHT(WHITE, PieceType.KNIGHT, false, GOTE_PROMOTED_KNIGHT),
    GOTE_SILVER(WHITE, PieceType.SILVER, false, GOTE_PROMOTED_SILVER),
    GOTE_GOLD(WHITE, PieceType.GOLD, false),
    GOTE_BISHOP(WHITE, PieceType.BISHOP, false, GOTE_PROMOTED_BISHOP),
    GOTE_ROOK(WHITE, PieceType.ROOK, false, GOTE_PROMOTED_ROOK),
    GOTE_KING(WHITE, PieceType.KING, false);

    private final Player player;
    private final PieceType pieceType;
    private final boolean promoted;
    private final Piece promotedPiece;

    Piece(final Player player, final PieceType pieceType, final boolean promoted) {
        this(player, pieceType, promoted, null);
    }

    Piece(final Player player, final PieceType pieceType, final boolean promoted, final Piece promotedPiece) {
        this.player = player;
        this.pieceType = pieceType;
        this.promoted = promoted;
        this.promotedPiece = promotedPiece;
    }

    public Player getOwner() {
        return player;
    }

    public boolean isBlackPiece() {
        return player == BLACK;
    }

    public boolean isWhitePiece() {
        return player == WHITE;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    public Piece getUnpromotedPiece() {
        return getPiece(pieceType, player);
    }

    public Piece getSentePiece() {
        return getPiece(getPieceType(), BLACK, isPromoted());
    }

    public boolean canPromote() {
        return promotedPiece != null;
    }

    public Piece opposite() {
        return getPiece(pieceType, player.opposite(), promoted);
    }

    @Override
    public String toString() {
        return pieceType.name() + ", " + player + (promoted ? ", promoted" : "");
    }

    private static final Piece[] allPieces = new Piece[PieceType.values().length * 4];

    static {
        for (Piece piece : Piece.values()) {
            allPieces[piece.getPieceType().ordinal() * 4 + (piece.isBlackPiece() ? 2 : 0)
                    + (piece.isPromoted() ? 1 : 0)] = piece;
        }
    }

    public static Piece getPiece(final PieceType pieceType, final Player player, final boolean promoted) {
        return allPieces[pieceType.ordinal() * 4 + (player == BLACK ? 2 : 0) + (promoted ? 1 : 0)];
    }

    public static Piece getPiece(final PieceType pieceType, final Player player) {
        return player == BLACK ? allPieces[pieceType.ordinal() * 4 + 2] : allPieces[pieceType.ordinal() * 4];
    }

    public static Piece getOppositePiece(final Piece piece) {
        if (piece == null) {
            return null;
        } else {
            return piece.opposite();
        }
    }

}
