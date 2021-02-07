package com.playshogi.library.shogi.models.formats.csa;

import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsaFormatTest {
    private static final String KIFU1 = "$START_TIME:2019-11-29T01:00:00.000Z\n" +
            "$END_TIME:2019-11-29T10:30:00.000Z\n" +
            "$RESULT:sente_win\n" +
            "N+渡辺　明 三冠\n" +
            "N-千葉幸生 七段\n" +
            "$OPENING:矢倉\n" +
            "$EVENT:王位戦\n" +
            "+7776FU\n" +
            "-8384FU\n" +
            "+7968GI\n" +
            "-3334FU\n" +
            "+6877GI\n" +
            "-7162GI\n" +
            "+2726FU\n" +
            "-3142GI\n" +
            "+2625FU\n" +
            "-4233GI\n" +
            "+3948GI\n" +
            "-4132KI\n" +
            "+6978KI\n" +
            "-5354FU\n" +
            "+5756FU\n" +
            "-6152KI\n" +
            "+5969OU\n" +
            "-7374FU\n" +
            "+4958KI\n" +
            "-8485FU\n" +
            "+3736FU\n" +
            "-5141OU\n" +
            "+8879KA\n" +
            "-2231KA\n" +
            "+6766FU\n" +
            "-4344FU\n" +
            "+4837GI\n" +
            "-5243KI\n" +
            "+5867KI\n" +
            "-3164KA\n" +
            "+7946KA\n" +
            "-6273GI\n" +
            "+6979OU\n" +
            "-4131OU\n" +
            "+7988OU\n" +
            "-3122OU\n" +
            "+1716FU\n" +
            "-1314FU\n" +
            "+9796FU\n" +
            "-9394FU\n" +
            "+4664KA\n" +
            "-6364FU\n" +
            "+3726GI\n" +
            "-6465FU\n" +
            "+6665FU\n" +
            "-9495FU\n" +
            "+9695FU\n" +
            "-7475FU\n" +
            "+7675FU\n" +
            "-0039KA\n" +
            "+2838HI\n" +
            "-3975UM\n" +
            "+1615FU\n" +
            "-7565UM\n" +
            "+1514FU\n" +
            "-8586FU\n" +
            "+8786FU\n" +
            "-0066FU\n" +
            "+6757KI\n" +
            "-0085FU\n" +
            "+8685FU\n" +
            "-6564UM\n" +
            "+0037KA\n" +
            "-5455FU\n" +
            "+0065FU\n" +
            "-6465UM\n" +
            "+5766KI\n" +
            "-6554UM\n" +
            "+5655FU\n" +
            "-5436UM\n" +
            "+5554FU\n" +
            "-3647UM\n" +
            "+3828HI\n" +
            "-0012FU\n" +
            "+0064FU\n" +
            "-8262HI\n" +
            "+2615GI\n" +
            "-0086FU\n" +
            "+7786GI\n" +
            "-7364GI\n" +
            "+2524FU\n" +
            "-2324FU\n" +
            "+0063FU\n" +
            "-6263HI\n" +
            "+0025FU\n" +
            "-4757UM\n" +
            "+0067FU\n" +
            "-0065FU\n" +
            "+2524FU\n" +
            "-0087FU\n" +
            "+7887KI\n" +
            "-6566FU\n" +
            "+3764KA\n" +
            "-6364HI\n" +
            "+0023GI\n" +
            "-2231OU\n" +
            "+2332NG\n" +
            "-3132OU\n" +
            "+2423TO\n" +
            "-3242OU\n" +
            "+2333TO\n" +
            "-4233OU\n" +
            "+2821RY\n" +
            "-4342KI\n" +
            "+0055GI\n" +
            "-0079KA\n" +
            "+8898OU\n" +
            "-9195KY\n" +
            "+0096FU\n" +
            "-5767UM\n" +
            "+0025KE\n" +
            "-3343OU\n" +
            "+0053KI\n" +
            "%TORYO";

    private final static String KIFU2 = "'----------棋譜ファイルの例\"example.csa\"-----------------\n" +
            "'バージョン\n" +
            "V2.2\n" +
            "'対局者名\n" +
            "N+NAKAHARA\n" +
            "N-YONENAGA\n" +
            "'棋譜情報\n" +
            "'棋戦名\n" +
            "$EVENT:13th World Computer Shogi Championship\n" +
            "'対局場所\n" +
            "$SITE:KAZUSA ARC\n" +
            "'開始日時\n" +
            "$START_TIME:2003/05/03 10:30:00\n" +
            "'終了日時\n" +
            "$END_TIME:2003/05/03 11:11:05\n" +
            "'持ち時間:25分、切れ負け\n" +
            "$TIME_LIMIT:00:25+00\n" +
            "'戦型:矢倉\n" +
            "$OPENING:YAGURA\n" +
            "'平手の局面\n" +
            "'先手番\n" +
            "+\n" +
            "'指し手と消費時間\n" +
            "+2726FU\n" +
            "T12\n" +
            "-3334FU\n" +
            "T6\n" +
            "%CHUDAN\n" +
            "'---------------------------------------------------------";

    @Test
    public void readKifu1() {
        List<GameRecord> gameRecords = CsaFormat.INSTANCE.read(KIFU1);
        assertEquals(1, gameRecords.size());
        assertEquals("USF:1.0\n" +
                "^b:7g7f8c8d7i6h3c3d6h7g7a6b2g2f3a4b2f2e4b3c3i4h4a3b6i7h5c5d5g5f6a5b5i6i7c7d4i5h8d8e3g3f5a4a8h7i2b3a" +
                "6g6f4c4d4h3g5b4c5h6g3a6d7i4f6b7c6i7i4a3a7i8h3a2b1g1f1c1d9g9f9c9d4f6d6c6d3g2f6d6e6f6e9d9e9f9e7d7e7f" +
                "7eb*3i2h3h3i7E1f1e7e6e1e1d8e8f8g8fp*6f6g5gp*8e8f8e6e6dB*3g5d5eP*6e6d6e5g6f6e5d5f5e5d3f5e5d3f4g3h2h" +
                "p*1bP*6d8b6b2f1ep*8f7g8f7c6d2e2d2c2dP*6c6b6cP*2e4g5gP*6gp*6e2e2dp*8g7h8g6e6f3g6d6c6dS*2c2b3a2c3B3a" +
                "3b2d2C3b4b2c3c4b3c2h2A4c4bS*5eb*7i8h9h9a9eP*9f5g6gN*2e3c4cG*5cRSGN\n" +
                "BN:渡辺　明 三冠\n" +
                "WN:千葉幸生 七段\n" +
                "GD:2019-11-29\n" +
                "GN:王位戦\n", UsfFormat.INSTANCE.write(gameRecords.get(0)));
    }

    @Test
    public void readKifu2() {
        List<GameRecord> gameRecords = CsaFormat.INSTANCE.read(KIFU2);
        assertEquals(1, gameRecords.size());
        assertEquals("USF:1.0\n" +
                "^*:2g2f3c3dBREK\n" +
                "BN:NAKAHARA\n" +
                "WN:YONENAGA\n" +
                "GD:2003/05/03\n" +
                "GN:13th World Computer Shogi Championship\n" +
                "GQ:KAZUSA ARC\n", UsfFormat.INSTANCE.write(gameRecords.get(0)));
    }
}