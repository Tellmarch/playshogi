package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.decorations.Arrow;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameNavigationTest {

    @Test
    public void parseArrowObject() {
        Arrow arrow = GameNavigation.parseArrowObject("ARROW,2h2c,3,0,0,(240,5,127,255),(240,0,0,255)");
        assertEquals(Square.of(2, 8), arrow.getFrom());
        assertEquals(Square.of(2, 3), arrow.getTo());
        assertEquals("#f0057f", arrow.getColor().toString());
        assertEquals(255, arrow.getColor().getA());
    }
}