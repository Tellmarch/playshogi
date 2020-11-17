package com.playshogi.library.shogi.models.formats.usi;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UsiMoveConverterTest {

    private static final String goteSfen = "lnsgkgsnl/1r5b1/pppppp1pp/6p2/9/2P4P1/PP1PPPP1P/1B5R1/LNSGKGSNL w -";
    private static final String goteSfen2 = "lnsgkgsnl/1r7/pppppp1pp/6p2/9/2P4P1/PP1PPPP1P/1S5R1/LN1GKGSNL w Bb";
    private static final String senteSfen = "lnsgkg1nl/1r5s1/pppppp1pp/6p2/9/2P4P1/PP1PPPP1P/1S5R1/LN1GKGSNL b Bb";

    @Test
    public void fromPsnString() {
        assertEquals("2b8H", UsiMoveConverter.fromPsnString("2b8h+", SfenConverter.fromSFEN(goteSfen)).toString());
        assertEquals("2b8h", UsiMoveConverter.fromPsnString("2b8h", SfenConverter.fromSFEN(goteSfen)).toString());
        assertEquals("2b7G", UsiMoveConverter.fromPsnString("2b7g+", SfenConverter.fromSFEN(goteSfen)).toString());
        assertEquals("2b7g", UsiMoveConverter.fromPsnString("2b7g", SfenConverter.fromSFEN(goteSfen)).toString());
        assertEquals("b*7g", UsiMoveConverter.fromPsnString("B*7g", SfenConverter.fromSFEN(goteSfen2)).toString());
        assertEquals("B*3c", UsiMoveConverter.fromPsnString("B*3c", SfenConverter.fromSFEN(senteSfen)).toString());
    }

    @Test
    public void fromPsnToUsfSTring() {
        assertEquals("2b8H", UsiMoveConverter.fromPsnToUsfSTring("2b8h+", goteSfen));
        assertEquals("2b8h", UsiMoveConverter.fromPsnToUsfSTring("2b8h", goteSfen));
        assertEquals("2b7G", UsiMoveConverter.fromPsnToUsfSTring("2b7g+", goteSfen));
        assertEquals("2b7g", UsiMoveConverter.fromPsnToUsfSTring("2b7g", goteSfen));
        assertEquals("b*7g", UsiMoveConverter.fromPsnToUsfSTring("B*7g", goteSfen2));
        assertEquals("B*3c", UsiMoveConverter.fromPsnToUsfSTring("B*3c", senteSfen));
    }
}