package com.playshogi.library.shogi.models;

public class Piece {

	private boolean sentePiece;
	private final PieceType pieceType;
	private boolean promoted;

	public Piece(final boolean sentePiece, final PieceType pieceType, final boolean promoted) {
		this.sentePiece = sentePiece;
		this.pieceType = pieceType;
		this.promoted = promoted;
	}

	public boolean isSentePiece() {
		return sentePiece;
	}

	public void setSentePiece(final boolean sentePiece) {
		this.sentePiece = sentePiece;
	}

	public boolean isPromoted() {
		return promoted;
	}

	public void setPromoted(final boolean promoted) {
		this.promoted = promoted;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

}
