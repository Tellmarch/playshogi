package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.GameRecordUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PsnFormatTest {

    private static final String EXAMPLE_GAME = "[Event \"ESC/WOSC 2013\"]\n" +
            "[Round \"1\"]\n" +
            "[Black \"AAA aaa\"]\n" +
            "[White \"BBB bbb\"]\n" +
            "[Date \"19/07/2013\"]\n" +
            "[Result \"1\"]\n" +
            "1.P7g-7f\n" +
            "2.P8c-8d\n" +
            "3.S7i-6h\n" +
            "4.P8d-8e\n" +
            "5.B8h-7g\n" +
            "6.K5a-4b\n" +
            "7.R2h-5h\n" +
            "8.G6a-5b\n" +
            "9.K5i-4h\n" +
            "10.S7a-6b\n" +
            "11.K4h-3h\n" +
            "12.P7c-7d\n" +
            "13.P6g-6f\n" +
            "14.S6b-7c\n" +
            "15.S6h-6g\n" +
            "16.P9c-9d\n" +
            "17.P9g-9f\n" +
            "18.S7c-8d\n" +
            "19.S3i-4h\n" +
            "20.P7d-7e\n" +
            "21.R5h-7h\n" +
            "22.R8b-7b\n" +
            "23.B7g-6h\n" +
            "24.P3c-3d\n" +
            "25.L9i-9h\n" +
            "26.P7ex7f\n" +
            "27.S6gx7f\n" +
            "28.B2bx6f\n" +
            "29.S7f-6g\n" +
            "30.R7bx7h+\n" +
            "31.S6gx7h\n" +
            "32.R*8b\n" +
            "33.B6h-7g\n" +
            "34.B6fx7g+\n" +
            "35.N8ix7g\n" +
            "36.B*3c\n" +
            "37.B*4f\n" +
            "38.S8d-7c\n" +
            "39.R*6a\n" +
            "40.S7c-6d\n" +
            "41.P5g-5f\n" +
            "42.P8e-8f\n" +
            "43.R6a-7a+\n" +
            "44.R8b-8c\n" +
            "45.P8gx8f\n" +
            "46.R8cx8f\n" +
            "47.P*6b\n" +
            "48.R8f-8h+\n" +
            "49.P6b-6a+\n" +
            "50.P*7f\n" +
            "51.+P6a-6b\n" +
            "52.G5bx6b\n" +
            "53.+R7ax6b\n" +
            "54.G4a-5b\n" +
            "55.+R6b-6a\n" +
            "56.G5b-5a\n" +
            "57.+R6a-7b\n" +
            "58.G5a-5b\n" +
            "59.B4fx6d\n" +
            "60.+R8hx7h\n" +
            "61.G6ix7h\n" +
            "62.P7fx7g+\n" +
            "63.R*8b\n" +
            "64.N*6b\n" +
            "65.+R7bx6b\n" +
            "66.G5bx6b\n" +
            "67.R8bx6b+\n" +
            "68.S*5b\n" +
            "69.B6dx5c+\n" +
            "70.K4b-3b\n" +
            "71.+R6bx5b\n" +
            "72.R*4b\n" +
            "73.S*4a\n" +
            "74.K3b-2b\n" +
            "75.+B5cx4b\n" +
            "76.S3ax4b\n" +
            "77.R*3b\n" +
            "--Black Won--";

    @Test
    public void read() {
        GameRecord record = PsnFormat.INSTANCE.read(EXAMPLE_GAME);
        List<Move> moves = GameRecordUtils.getMainVariationMoves(record);
        System.out.println(moves);
        assertEquals(78, moves.size());
    }
}