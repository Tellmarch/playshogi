package com.playshogi.library.shogi.models.formats.wiki;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class WikiConverterTest {

    @Test
    public void toWikiDiagramHandicap() {
        String diagram = WikiConverter.toWikiDiagram(ShogiInitialPositionFactory.createInitialPosition(Handicap.SIX_PIECES), "6-Piece Handicap", "");
        assertEquals( "{{shogi diagram \n" +
                "|  \n" +
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
                "| Insert Comment Here!\n" +
                "}}", diagram);
    }

    @Test
    public void toWikiDiagramPiecesInHand() {
        String diagram = WikiConverter.toWikiDiagram(SfenConverter.fromSFEN("1+B7/1K+B6/1S+P3+R2/P2P1P2G/1+r2g2l1/9/5+p+p2/4g2+sl/7+lk b S2NL9Pgs2n3p 1"), "Kimura vs Toyoshima 2019", " After 285 moves.");
        assertEquals( "{{shogi diagram \n" +
                "|  \n" +
                "| '''Kimura vs Toyoshima 2019'''\n" +
                "| 金<sub>1</sub> 銀<sub>1</sub> 桂<sub>2</sub> 歩<sub>3</sub>\n" +
                "|    | hs |    |    |    |    |    |    |\n" +
                "|    | ks | hs |    |    |    |    |    |\n" +
                "|    | ss | ts |    |    |    | ds |    |\n" +
                "| ps |    |    | ps |    | ps |    |    | gs \n" +
                "|    | dg |    |    | gg |    |    | lg |\n" +
                "|    |    |    |    |    |    |    |    |\n" +
                "|    |    |    |    |    | tg | tg |    |\n" +
                "|    |    |    |    | gg |    |    | psg| lg \n" +
                "|    |    |    |    |    |    |    | plg| kg \n" +
                "| 銀<sub>1</sub> 桂<sub>2</sub> 香<sub>1</sub> 歩<sub>9</sub>\n" +
                "| After 285 moves.\n" +
                "}}", diagram);
    }
}