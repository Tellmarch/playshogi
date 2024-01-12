package com.playshogi.library.shogi.models.formats.wiki;

import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class WikiConverterTest {

    @Test
    public void toWikiDiagram() {
        String diagram = WikiConverter.toWikiDiagram(ShogiInitialPositionFactory.createInitialPosition(Handicap.SIX_PIECES), "6-Piece Handicap");
        assertEquals( "{{shogi diagram \n" +
                "| floatright\n" +
                "| '''6-Piece Handicap'''\n" +
                "| –\n" +
                "|    |    | sg | gg | kg | gg | sg |    |\n" +
                "|    |    |    |    |    |    |    |    |\n" +
                "| pg | pg | pg | pg | pg | pg | pg | pg | pg \n" +
                "|    |    |    |    |    |    |    |    |\n" +
                "|    |    |    |    |    |    |    |    |\n" +
                "|    |    |    |    |    |    |    |    |\n" +
                "| ps | ps | ps | ps | ps | ps | ps | ps | ps \n" +
                "|    | bs |    |    |    |    |    | rs |\n" +
                "| ls | ns | ss | gs | ks | gs | ss | ns | ls \n" +
                "| –\n" +
                "|\n" +
                "}}", diagram);
    }
}