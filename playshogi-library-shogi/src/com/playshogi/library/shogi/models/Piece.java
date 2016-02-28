package com.playshogi.library.shogi.models;

public enum Piece {
	SENTE_PAWN(true, PieceType.PAWN, false), SENTE_LANCE(true, PieceType.LANCE, false), SENTE_KNIGHT(true,
			PieceType.KNIGHT, false), SENTE_SILVER(true, PieceType.SILVER, false), SENTE_GOLD(true, PieceType.GOLD,
					false), SENTE_BISHOP(true, PieceType.BISHOP,
							false), SENTE_ROOK(true, PieceType.ROOK, false), SENTE_KING(true, PieceType.KING, false),

	SENTE_PROMOTED_PAWN(true, PieceType.PAWN, true), SENTE_PROMOTED_LANCE(true, PieceType.LANCE,
			true), SENTE_PROMOTED_KNIGHT(true, PieceType.KNIGHT, true), SENTE_PROMOTED_SILVER(true, PieceType.SILVER,
					true), SENTE_PROMOTED_BISHOP(true, PieceType.BISHOP,
							true), SENTE_PROMOTED_ROOK(true, PieceType.ROOK, true),

	GOTE_PAWN(false, PieceType.PAWN, false), GOTE_LANCE(false, PieceType.LANCE, false), GOTE_KNIGHT(false,
			PieceType.KNIGHT, false), GOTE_SILVER(false, PieceType.SILVER, false), GOTE_GOLD(false, PieceType.GOLD,
					false), GOTE_BISHOP(false, PieceType.BISHOP,
							false), GOTE_ROOK(false, PieceType.ROOK, false), GOTE_KING(false, PieceType.KING, false),

	GOTE_PROMOTED_PAWN(false, PieceType.PAWN, true), GOTE_PROMOTED_LANCE(false, PieceType.LANCE,
			true), GOTE_PROMOTED_KNIGHT(false, PieceType.KNIGHT, true), GOTE_PROMOTED_SILVER(false, PieceType.SILVER,
					true), GOTE_PROMOTED_BISHOP(false, PieceType.BISHOP,
							true), GOTE_PROMOTED_ROOK(false, PieceType.ROOK, true);

	private final boolean sentePiece;
	private final PieceType pieceType;
	private final boolean promoted;

	Piece(final boolean sentePiece, final PieceType pieceType, final boolean promoted) {
		this.sentePiece = sentePiece;
		this.pieceType = pieceType;
		this.promoted = promoted;
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

	@Override
	public String toString() {
		return pieceType.name() + (sentePiece ? ",sente" : ",gote") + (promoted ? ", promoted" : "");
	}

}
