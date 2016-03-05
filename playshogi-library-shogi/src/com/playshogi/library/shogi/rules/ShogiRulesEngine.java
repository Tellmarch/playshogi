package com.playshogi.library.shogi.rules;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.games.GameRulesEngine;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.movements.PawnMovement;
import com.playshogi.library.shogi.rules.movements.PieceMovement;

public class ShogiRulesEngine implements GameRulesEngine<ShogiPosition> {

	private static final EnumMap<PieceType, PieceMovement> PIECE_MOVEMENTS = new EnumMap<>(PieceType.class);
	static {
		PIECE_MOVEMENTS.put(PieceType.PAWN, new PawnMovement());
	}

	@Override
	public void playMoveInPosition(final ShogiPosition position, final Move move) {
		if (move instanceof CaptureMove) {
			playCaptureMove(position, (CaptureMove) move);
		} else if (move instanceof DropMove) {
			playDropMove(position, (DropMove) move);
		} else if (move instanceof NormalMove) {
			playNormalMove(position, (NormalMove) move);
		}
	}

	private void playCaptureMove(final ShogiPosition position, final CaptureMove move) {
		position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
		position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
	}

	private void playDropMove(final ShogiPosition position, final DropMove move) {
		position.getShogiBoardState().setPieceAt(move.getToSquare(),
				Piece.getPiece(move.getPieceType(), move.isSenteMoving()));
	}

	private void playNormalMove(final ShogiPosition position, final NormalMove move) {
		position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
		position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
	}

	@Override
	public void undoMoveInPosition(final ShogiPosition position, final Move move) {
		// TODO Auto-generated method stub

	}

	public List<Square> getPossibleTargetSquaresFromSquareInPosition(final ShogiPosition position, final Square from) {
		Piece piece = position.getPieceAt(from);
		if (piece == null) {
			return Collections.emptyList();
		}
		PieceMovement pieceMovement = PIECE_MOVEMENTS.get(piece.getPieceType());
		if (piece.isSentePiece()) {
			return pieceMovement.getPossibleMoves(position.getShogiBoardState(), from);
		} else {
			return Square.opposite(
					pieceMovement.getPossibleMoves(position.getShogiBoardState().opposite(), from.opposite()));
		}

	}

	@Override
	public boolean isMoveLegalInPosition(final ShogiPosition position, final Move move) {
		if (move instanceof CaptureMove) {
			return isCaptureMoveLegalInPosition(position, (CaptureMove) move);
		} else if (move instanceof DropMove) {
			return isDropMoveLegalInPosition(position, (DropMove) move);
		} else if (move instanceof NormalMove) {
			return isNormalMoveLegalInPosition(position, (NormalMove) move);
		} else {
			return false;
		}
	}

	// todo validate that the piece is us, that we have piece to drop, that we
	// don't capture our piece

	private boolean isNormalMoveLegalInPosition(final ShogiPosition position, final NormalMove move) {
		PieceMovement pieceMovement = PIECE_MOVEMENTS.get(move.getPiece().getPieceType());
		if (move.isSenteMoving()) {
			return pieceMovement.isMoveDxDyValid(position.getShogiBoardState(), move.getFromSquare(), move.getToSquare());
		} else {
			return pieceMovement.isMoveDxDyValid(position.getShogiBoardState().opposite(), move.getFromSquare().opposite(),
					move.getToSquare().opposite());
		}
	}

	private boolean isDropMoveLegalInPosition(final ShogiPosition position, final DropMove move) {
		PieceMovement pieceMovement = PIECE_MOVEMENTS.get(move.getPieceType());
		if (move.isSenteMoving()) {
			return pieceMovement.isDropValid(position.getShogiBoardState(), move.getToSquare());
		} else {
			return pieceMovement.isDropValid(position.getShogiBoardState().opposite(), move.getToSquare().opposite());
		}
	}

	private boolean isCaptureMoveLegalInPosition(final ShogiPosition position, final CaptureMove move) {
		Piece capturedPiece = position.getPieceAt(move.getToSquare());
		if (capturedPiece == null || capturedPiece.isSentePiece() == move.isSenteMoving()) {
			return false;
		} else {
			return isNormalMoveLegalInPosition(position, move);
		}
	}

}
