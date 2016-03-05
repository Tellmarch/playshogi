package com.playshogi.library.shogi.rules.movements;

import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public interface PieceMovement {

	List<Square> getPossibleMoves(ShogiBoardState boardState, Square from);

	boolean isMoveValid(ShogiBoardState boardState, Square from, Square to);

	boolean isDropValid(ShogiBoardState boardState, Square to);

}
