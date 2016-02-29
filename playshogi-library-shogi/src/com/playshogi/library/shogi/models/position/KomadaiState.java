package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.PieceType;

public class KomadaiState {
	private final int[] pieces = new int[PieceType.values().length];

	public void removePiece(final PieceType piece) {
		pieces[piece.ordinal()]--;
	}

	public void addPiece(final PieceType piece) {
		pieces[piece.ordinal()]++;
	}

	public void setPiecesOfType(final PieceType piece, final int i) {
		pieces[piece.ordinal()] = i;
	}

	public int getPiecesOfType(final PieceType piece) {
		return pieces[piece.ordinal()];
	}

	public int[] getPieces() {
		return pieces;
	}
}
