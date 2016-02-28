package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.games.GameRulesEngine;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class ShogiRulesEngine implements GameRulesEngine<ShogiPosition> {

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
		position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
	}

	private void playNormalMove(final ShogiPosition position, final NormalMove move) {
		position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
		position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
	}

	@Override
	public void undoMoveInPosition(final ShogiPosition position, final Move move) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMoveLegalInPosition(final ShogiPosition position, final Move move) {
		// TODO Auto-generated method stub
		return false;
	}

}
