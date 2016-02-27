package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

public class DropMove {

	private final Piece piece;
	private final Square toSquare;

	public DropMove(final Piece piece, final Square toSquare) {
		this.piece = piece;
		this.toSquare = toSquare;
	}

	public Piece getPiece() {
		return piece;
	}

	public Square getToSquare() {
		return toSquare;
	}

}
