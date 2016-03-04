package com.playshogi.library.shogi.rules.movements;

import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public interface PieceMovement {

	List<Square> getPossibleMoves(ShogiBoardState position, Square from);

	boolean isMoveValid(ShogiBoardState position, Square from, Square to);

	boolean isDropValid(ShogiBoardState position, Square to);

}
