package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

public class CaptureMove extends NormalMove {

	private final Piece capturedPiece;

	public CaptureMove(final boolean senteMoving, final Piece piece, final Square fromSquare, final Square toSquare,
			final boolean promote, final Piece capturedPiece) {
		super(senteMoving, piece, fromSquare, toSquare, promote);
		this.capturedPiece = capturedPiece;
	}

	public Piece getCapturedPiece() {
		return capturedPiece;
	}

}
