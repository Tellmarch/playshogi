package com.playshogi.library.models.games;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Position;

public interface GameRulesEngine<P extends Position> {

	public void playMoveInPosition(P position, Move move);

	public void undoMoveInPosition(P position, Move move);

	public boolean isMoveLegalInPosition(P position, Move move);

}
