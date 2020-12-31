package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class BishopMovementTest {

    ShogiPosition position;
    BishopMovement bishopMovement;
    boolean[][] expected = new boolean[9][9];

    @Before
    public void setUp() {
        position = new ShogiPosition();
        bishopMovement = new BishopMovement();
        for (int r=1; r<=4; r++)
            expected[4-r][4-r] = expected[4-r][4+r] = expected[4+r][4-r] = expected[4+r][4+r] = true;
    }

    @Test
    public void testIsMoveDxDyValid() {
        boolean[][] actual = new boolean[9][9];
        Square from = Square.of(5, 5);
        for (Square to : position.getAllSquares())
            actual[to.getColumn() - 1][to.getRow() - 1] = bishopMovement.isMoveDxDyValid(position.getShogiBoardState(), from, to);
        assertArrayEquals(expected, actual);
    }
}