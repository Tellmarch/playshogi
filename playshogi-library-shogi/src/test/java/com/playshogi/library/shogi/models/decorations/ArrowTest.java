package com.playshogi.library.shogi.models.decorations;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArrowTest {

    @Test
    public void toUsfString() {
        Arrow arrow = new Arrow(Square.of(2, 8), Square.of(2, 3), new Color(240, 5, 127, 255));
        assertEquals("ARROW,2h2c,3,0,0,(240,5,127,255),(240,5,127,255)", arrow.toUsfString());
        Arrow arrow2 = new Arrow(Piece.GOTE_BISHOP, Square.of(2, 3), new Color(240, 5, 127, 255));
        assertEquals("ARROW,b*2c,3,0,0,(240,5,127,255),(240,5,127,255)", arrow2.toUsfString());
    }

    @Test
    public void parseArrowObject() {
        Arrow arrow = Arrow.parseArrowObject("ARROW,2h2c,3,0,0,(240,5,127,255),(240,5,127,255)");
        assertEquals(Square.of(2, 8), arrow.getFrom());
        assertEquals(Square.of(2, 3), arrow.getTo());
        assertEquals("#f0057f", arrow.getColor().toString());
        assertEquals(255, arrow.getColor().getA());
    }

    @Test
    public void parseDropArrowObject() {
        Arrow arrow = Arrow.parseArrowObject("ARROW,b*2c,3,0,0,(240,5,127,255),(240,5,127,255)");
        assertEquals(Piece.GOTE_BISHOP, arrow.getFromKomadaiPiece());
        assertEquals(Square.of(2, 3), arrow.getTo());
        assertEquals("#f0057f", arrow.getColor().toString());
        assertEquals(255, arrow.getColor().getA());
    }
}