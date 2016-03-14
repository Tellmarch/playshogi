package com.playshogi.library.shogi.rules;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.games.GameRulesEngine;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.movements.BishopMovement;
import com.playshogi.library.shogi.rules.movements.GoldMovement;
import com.playshogi.library.shogi.rules.movements.KingMovement;
import com.playshogi.library.shogi.rules.movements.KnightMovement;
import com.playshogi.library.shogi.rules.movements.LanceMovement;
import com.playshogi.library.shogi.rules.movements.PawnMovement;
import com.playshogi.library.shogi.rules.movements.PieceMovement;
import com.playshogi.library.shogi.rules.movements.RookMovement;
import com.playshogi.library.shogi.rules.movements.SilverMovement;

public class ShogiRulesEngine implements GameRulesEngine<ShogiPosition> {

	private static final EnumMap<PieceType, PieceMovement> PIECE_MOVEMENTS = new EnumMap<>(PieceType.class);
	static {
		PIECE_MOVEMENTS.put(PieceType.PAWN, new PawnMovement());
		PIECE_MOVEMENTS.put(PieceType.LANCE, new LanceMovement());
		PIECE_MOVEMENTS.put(PieceType.KNIGHT, new KnightMovement());
		PIECE_MOVEMENTS.put(PieceType.SILVER, new SilverMovement());
		PIECE_MOVEMENTS.put(PieceType.GOLD, new GoldMovement());
		PIECE_MOVEMENTS.put(PieceType.KING, new KingMovement());
		PIECE_MOVEMENTS.put(PieceType.ROOK, new RookMovement());
		PIECE_MOVEMENTS.put(PieceType.BISHOP, new BishopMovement());
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
		position.setSenteToPlay(!position.isSenteToPlay());
	}

	private void playCaptureMove(final ShogiPosition position, final CaptureMove move) {
		if (move.isSenteMoving()) {
			position.getSenteKomadai().addPiece(move.getCapturedPiece().getPieceType());
		} else {
			position.getGoteKomadai().addPiece(move.getCapturedPiece().getPieceType());
		}
		if (move.isPromote()) {
			position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
		} else {
			position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
		}
		position.getShogiBoardState().setPieceAt(move.getFromSquare(), null);
	}

	private void playDropMove(final ShogiPosition position, final DropMove move) {
		position.getShogiBoardState().setPieceAt(move.getToSquare(),
				Piece.getPiece(move.getPieceType(), move.isSenteMoving()));
		if (move.isSenteMoving()) {
			position.getSenteKomadai().removePiece(move.getPieceType());
		} else {
			position.getGoteKomadai().removePiece(move.getPieceType());
		}
	}

	private void playNormalMove(final ShogiPosition position, final NormalMove move) {
		if (move.isPromote()) {
			position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
		} else {
			position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
		}
		position.getShogiBoardState().setPieceAt(move.getFromSquare(), null);
	}

	@Override
	public void undoMoveInPosition(final ShogiPosition position, final Move move) {
		Objects.requireNonNull(move);
		Objects.requireNonNull(position);
		if (move instanceof CaptureMove) {
			undoCaptureMove(position, (CaptureMove) move);
		} else if (move instanceof DropMove) {
			undoDropMove(position, (DropMove) move);
		} else if (move instanceof NormalMove) {
			undoNormalMove(position, (NormalMove) move);
		}
		position.setSenteToPlay(!position.isSenteToPlay());
	}

	private void undoNormalMove(final ShogiPosition position, final NormalMove move) {
		if (move.isPromote()) {
			position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
		} else {
			position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
		}
		position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
	}

	private void undoDropMove(final ShogiPosition position, final DropMove move) {
		if (move.isSenteMoving()) {
			position.getSenteKomadai().addPiece(move.getPieceType());
		} else {
			position.getGoteKomadai().addPiece(move.getPieceType());
		}
		position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
	}

	private void undoCaptureMove(final ShogiPosition position, final CaptureMove move) {
		if (move.isSenteMoving()) {
			position.getSenteKomadai().removePiece(move.getCapturedPiece().getPieceType());
		} else {
			position.getGoteKomadai().removePiece(move.getCapturedPiece().getPieceType());
		}
		if (move.isPromote()) {
			position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
		} else {
			position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
		}
		position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getCapturedPiece());
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
			return pieceMovement.isMoveDxDyValid(position.getShogiBoardState(), move.getFromSquare(),
					move.getToSquare());
		} else {
			return pieceMovement.isMoveDxDyValid(position.getShogiBoardState().opposite(),
					move.getFromSquare().opposite(), move.getToSquare().opposite());
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
