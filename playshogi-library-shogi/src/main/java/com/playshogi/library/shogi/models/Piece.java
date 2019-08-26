package com.playshogi.library.shogi.models;

public enum Piece {
    SENTE_PROMOTED_PAWN(true, PieceType.PAWN, true),
    SENTE_PROMOTED_LANCE(true, PieceType.LANCE, true),
    SENTE_PROMOTED_KNIGHT(true, PieceType.KNIGHT, true),
    SENTE_PROMOTED_SILVER(true, PieceType.SILVER, true),
    SENTE_PROMOTED_BISHOP(true, PieceType.BISHOP, true),
    SENTE_PROMOTED_ROOK(true, PieceType.ROOK, true),

    SENTE_PAWN(true, PieceType.PAWN, false, SENTE_PROMOTED_PAWN),
    SENTE_LANCE(true, PieceType.LANCE, false, SENTE_PROMOTED_LANCE),
    SENTE_KNIGHT(true, PieceType.KNIGHT, false, SENTE_PROMOTED_KNIGHT),
    SENTE_SILVER(true, PieceType.SILVER, false, SENTE_PROMOTED_SILVER),
    SENTE_GOLD(true, PieceType.GOLD, false),
    SENTE_BISHOP(true, PieceType.BISHOP, false, SENTE_PROMOTED_BISHOP),
    SENTE_ROOK(true, PieceType.ROOK, false, SENTE_PROMOTED_ROOK),
    SENTE_KING(true, PieceType.KING, false),

    GOTE_PROMOTED_PAWN(false, PieceType.PAWN, true),
    GOTE_PROMOTED_LANCE(false, PieceType.LANCE, true),
    GOTE_PROMOTED_KNIGHT(false, PieceType.KNIGHT, true),
    GOTE_PROMOTED_SILVER(false, PieceType.SILVER, true),
    GOTE_PROMOTED_BISHOP(false, PieceType.BISHOP, true),
    GOTE_PROMOTED_ROOK(false, PieceType.ROOK, true),

    GOTE_PAWN(false, PieceType.PAWN, false, GOTE_PROMOTED_PAWN),
    GOTE_LANCE(false, PieceType.LANCE, false, GOTE_PROMOTED_LANCE),
    GOTE_KNIGHT(false, PieceType.KNIGHT, false, GOTE_PROMOTED_KNIGHT),
    GOTE_SILVER(false, PieceType.SILVER, false, GOTE_PROMOTED_SILVER),
    GOTE_GOLD(false, PieceType.GOLD, false),
    GOTE_BISHOP(false, PieceType.BISHOP, false, GOTE_PROMOTED_BISHOP),
    GOTE_ROOK(false, PieceType.ROOK, false, GOTE_PROMOTED_ROOK),
    GOTE_KING(false, PieceType.KING, false);

    private final boolean sentePiece;
    private final PieceType pieceType;
    private final boolean promoted;
    private final Piece promotedPiece;

    Piece(final boolean sentePiece, final PieceType pieceType, final boolean promoted) {
        this(sentePiece, pieceType, promoted, null);
    }

    Piece(final boolean sentePiece, final PieceType pieceType, final boolean promoted, final Piece promotedPiece) {
        this.sentePiece = sentePiece;
        this.pieceType = pieceType;
        this.promoted = promoted;
        this.promotedPiece = promotedPiece;
    }

    public boolean isSentePiece() {
        return sentePiece;
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
        return getPiece(pieceType, sentePiece);
    }

    public Piece getSentePiece() {
        return getPiece(getPieceType(), true, isPromoted());
    }

    public boolean canPromote() {
        return promotedPiece != null;
    }

    public Piece opposite() {
        return getPiece(pieceType, !sentePiece, promoted);
    }

    @Override
    public String toString() {
        return pieceType.name() + (sentePiece ? ",sente" : ",gote") + (promoted ? ", promoted" : "");
    }

    private static final Piece[] allPieces = new Piece[PieceType.values().length * 4];

    static {
        for (Piece piece : Piece.values()) {
            allPieces[piece.getPieceType().ordinal() * 4 + (piece.isSentePiece() ? 2 : 0)
                    + (piece.isPromoted() ? 1 : 0)] = piece;
        }
    }

    public static Piece getPiece(final PieceType pieceType, final boolean sente, final boolean promoted) {
        return allPieces[pieceType.ordinal() * 4 + (sente ? 2 : 0) + (promoted ? 1 : 0)];
    }

    public static Piece getPiece(final PieceType pieceType, final boolean sente) {
        return sente ? allPieces[pieceType.ordinal() * 4 + 2] : allPieces[pieceType.ordinal() * 4];
    }

    public static Piece getOppositePiece(final Piece piece) {
        if (piece == null) {
            return null;
        } else {
            return piece.opposite();
        }
    }

}
