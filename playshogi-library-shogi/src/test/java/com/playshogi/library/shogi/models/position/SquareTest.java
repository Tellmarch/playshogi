package com.playshogi.library.shogi.models.position;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SquareTest {

    // We rely on the canonical toString method in various places
    @Test
    public void testToString() {
        assertEquals("1a", Square.of(1, 1).toString());
        assertEquals("9i", Square.of(9, 9).toString());
        assertEquals("3d", Square.of(3, 4).toString());
    }
}