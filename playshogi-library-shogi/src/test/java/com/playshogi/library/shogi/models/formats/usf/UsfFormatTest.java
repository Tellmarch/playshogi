package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.models.record.GameRecord;
import org.junit.Assert;
import org.junit.Test;

public class UsfFormatTest {

    @Test
    public void read() {
        String usfGame = "USF:1.0\n^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b " +
                "-:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame);
        Assert.assertNotNull(gameRecord.getGameTree());
    }

    @Test
    public void readWrite() {
        String usfGame = "USF:1.0\n^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b " +
                "-:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame);
        String result = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
        Assert.assertEquals(usfGame, result);
    }
}
