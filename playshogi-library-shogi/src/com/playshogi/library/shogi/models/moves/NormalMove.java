package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

public class NormalMove extends ShogiMove {

	private final Piece piece;
	private final Square fromSquare;
	private final Square toSquare;
	private boolean promote;

	public NormalMove(final Piece piece, final Square fromSquare, final Square toSquare, final boolean promote) {
		super(piece.isSentePiece());
		this.piece = piece;
		this.fromSquare = fromSquare;
		this.toSquare = toSquare;
		this.promote = promote;
	}

	public Piece getPiece() {
		return piece;
	}

	public Square getFromSquare() {
		return fromSquare;
	}

	public Square getToSquare() {
		return toSquare;
	}

	public boolean isPromote() {
		return promote;
	}

	public void setPromote(final boolean promote) {
		this.promote = promote;

	}

}
