package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
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
            "{Some comment with spaces}\n" +
            "77.R*3b\n" +
            "--Black Won--";

    private static final String OLD_FORMAT = "[Name \"AAA\"]\n" +
            "[Email \"aaa@bbb.com\"]\n" +
            "[Country \"Japan\"]\n" +
            "[Sente \"Takeshi Fujii\"]\n" +
            "[Gote \"Yoshiharu Habu\"]\n" +
            "[Black_grade \"Grade\"]\n" +
            "[White_grade \"Meijin\"]\n" +
            "[Result \"1-0\"]\n" +
            "[Comment \"Opening: Shikenbisha\"]\n" +
            "[Source \"Patrick Davin's Shogi Nexus website\"]\n" +
            "[Event \"Shinjin'o vs, Meijin\"]\n" +
            "[Date \"19700101\"]\n" +
            "[Round \"\"]\n" +
            "[Venue \"\"]\n" +
            "[Proam \"Professional\"]\n" +
            "P7g-7f P8c-8d R2h-6h S7a-6b P6g-6f P5c-5d P1g-1f P1c-1d K5i-4h K5a-4b S3i-3h K4b-3b S7i-7h P3c-3d S7h-6g" +
            " G6a-5b G6i-5h P8d-8e B8h-7g P7c-7d K4h-3i S3a-4b K3i-2h S4b-5c P5g-5f P6c-6d P4g-4f P6d-6e G5h-4g " +
            "P7d-7e P7fx7e P6ex6f S6g-7f P8e-8f P8gx8f P6f-6g+ B7gx2b+ K3bx2b S7fx6g B'3c P5f-5e B3cx5e B'7g B5ex7g+ " +
            "N8ix7g P'7f S6gx7f R8bx8f P'6c S6b-5a N7g-6e S5c-4d N6e-5c+ R8fx7f +N5cx5b G4ax5b P6c-6b+ G5bx6b P'5b " +
            "R7f-7i+ P5bx5a+ +R7ix6h +P5a-4a S4d-3c B'3a K2b-1b P1f-1e S'2b P1ex1d P'1g L1ix1g P'1f L1gx1f P'1e G'3b " +
            "S2bx3a +P4ax3a P1ex1f S'1c\n";

    private static final String OLD_FORMAT_MULTI = "[Name \"AAA\"]\n" +
            "[Email \"\"]\n" +
            "[Country \"Belgium\"]\n" +
            "[Sente \"BBB\"]\n" +
            "[Gote \"CCC\"]\n" +
            "[Black_grade \"3dan\"]\n" +
            "[White_grade \"4dan\"]\n" +
            "[Result \"1-0\"]\n" +
            "[Comment \"\"]\n" +
            "[Source \"Scoresheet\"]\n" +
            "[Event \"European Championship 2002\"]\n" +
            "[Date \"20020824\"]\n" +
            "[Round \"5\"]\n" +
            "[Venue \"\"]\n" +
            "[Proam \"Amateur\"]\n" +
            "P7g-7f P3c-3d P6g-6f S7a-6b R2h-6h P5c-5d P1g-1f P1c-1d K5i-4h K5a-4b K4h-3h K4b-3b K3h-2h G6a-5b S3i-3h" +
            " S6b-5c P6f-6e P5d-5e G6i-5h R8b-6b S7i-7h P6c-6d P6ex6d S5cx6d S7h-6g P7c-7d P5g-5f P'6e L9i-9h S3a-4b " +
            "P4g-4f S4b-5c G5h-4g S5c-5d P3g-3f P5ex5f B8hx2b+ K3bx2b S6gx5f P'5e S5f-4e S5dx4e P4fx4e B'7i R6h-5h " +
            "S'6g R5h-5i B7i-2d+ P4e-4d +B2d-3c P4dx4c+ G5bx4c G4i-4h P6e-6f P'4d G4cx4d N2i-3g S6g-6h+ B'9e +S6hx5i " +
            "B9ex6b+ G4d-4c +B6b-6c +B3c-4b R'6b S6d-5c R6b-6a+ R'3a +R6ax8a P6f-6g+ +R8ax9a +P6g-5h P'4d S5cx4d P'4e" +
            " +P5hx4h G4gx4h G4c-5c P4ex4d G5cx6c L'4c P'4g G4hx4g P'4f G4gx4f G'4h N3g-2e +S5i-5h L4cx4b+ B'3i " +
            "K2h-3g G4hx3h K3gx3h B3i-4h+ K3h-2i G4ax4b +R9ax3a K2bx3a R'6a P'4a N'4c G4bx4c G'3b K3ax3b P4dx4c+ " +
            "K3bx4c R6ax6c+ G'5c B'5d\n" +
            "\n" +
            "[Name \"DDD\"]\n" +
            "[Email \"\"]\n" +
            "[Country \"Belgium\"]\n" +
            "[Sente \"EEEE\"]\n" +
            "[Gote \"FFF\"]\n" +
            "[Black_grade \"3dan\"]\n" +
            "[White_grade \"4dan\"]\n" +
            "[Result \"0-1\"]\n" +
            "[Comment \"\"]\n" +
            "[Source \"Scoresheet\"]\n" +
            "[Event \"European Championship 2002\"]\n" +
            "[Date \"20020824\"]\n" +
            "[Round \"4\"]\n" +
            "[Venue \"\"]\n" +
            "[Proam \"Amateur\"]\n" +
            "P7g-7f P8c-8d S7i-6h P3c-3d S6h-7g P5c-5d P5g-5f R8b-5b R2h-5h S7a-6b G6i-7h S6b-5c S3i-4h K5a-4b P4g-4f" +
            " S5c-6d S4h-4g K4b-3b P3g-3f P7c-7d S7g-6f N8a-7c K5i-4h S6d-6e S6f-7g P5d-5e P5fx5e R5bx5e P'5f R5e-5b " +
            "K4h-3i G6a-5a P6g-6f S6e-5d K3i-2h P6c-6d G4i-3h P1c-1d P1g-1f S3a-4b S7g-6h S4b-5c S6h-6g G5a-4b N2i-3g" +
            " P6d-6e R5h-5i R5b-6b P6fx6e B2bx8h+ G7hx8h B'4d B'7g S5dx6e B7gx4d S5cx4d P4f-4e S4d-3c B'4f B'8b " +
            "N8i-7g P'6f N7gx6e R6bx6e S6gx6f R6ex6f G8h-7g R6f-6b P'6c R6bx6c P'6d N7c-6e P6dx6c+ B8bx4f S4gx4f B'6h" +
            " R5i-6i N6ex7g+ R6ix6h +N7gx6h +P6c-5c G4bx5c R'6b G5c-5b R6bx6h+ R'4i S'3i R4ix4f+ B'7c +R4f-4i B7cx9a+" +
            " P1d-1e G3h-4h S'1g L1ix1g S'1i K2h-1h +R4ix3i G4h-3h S'2i\n";

    @Test
    public void read() {
        GameRecord record = PsnFormat.INSTANCE.read(EXAMPLE_GAME).get(0);
        assertEquals("USF:1.0\n" +
                "^b:7g7f8c8d7i6h8d8e8h7g5a4b2h5h6a5b5i4h7a6b4h3h7c7d6g6f6b7c6h6g9c9d9g9f7c8d3i4h7d7e5h7h8b7b7g6h3c3d" +
                "9i9h7e7f6g7f2b6f7f6g7b7H6g7hr*8b6h7g6f7G8i7gb*3cB*4f8d7cR*6a7c6d5g5f8e8f6a7A8b8c8g8f8c8fP*6b8f8H6b6A" +
                "p*7f6a6b5b6b7a6b4a5b6b6a5b5a6a7b5a5b4f6d8h7h6i7h7f7GR*8bn*6b7b6b5b6b8b6Bs*5b6d5C4b3b6b5br*4bS*4a3b2b" +
                "5c4b3a4bR*3bRSGN\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                "GD:19/07/2013\n" +
                "GQ:ESC/WOSC 2013", UsfFormat.INSTANCE.write(record));
    }

    @Test
    public void readOld() {
        GameRecord record = PsnFormat.INSTANCE.read(OLD_FORMAT).get(0);
        assertEquals("USF:1.0\n" +
                "^w:7g7f8c8d2h6h7a6b6g6f5c5d1g1f1c1d5i4h5a4b3i3h4b3b7i7h3c3d7h6g6a5b6i5h8d8e8h7g7c7d4h3i3a4b3i2h4b5c" +
                "5g5f6c6d4g4f6d6e5h4g7d7e7f7e6e6f6g7f8e8f8g8f6f6G7g2B3b2b7f6gb*3c5f5e3c5eB*7g5e7G8i7gp*7f6g7f8b8fP*6c" +
                "6b5a7g6e5c4d6e5C8f7f5c5b4a5b6c6B5b6bP*5b7f7I5b5A7i6h5a4a4d3cB*3a2b1b1f1es*2b1e1dp*1g1i1gp*1f1g1fp*1e" +
                "G*3b2b3a4a3a1e1fS*1c\n" +
                "BN:Takeshi Fujii\n" +
                "WN:Yoshiharu Habu\n" +
                "GD:19700101\n" +
                "GQ:Shinjin'o vs, Meijin", UsfFormat.INSTANCE.write(record));
    }

    @Test
    public void readOldMulti() {
        List<GameRecord> records = PsnFormat.INSTANCE.read(OLD_FORMAT_MULTI);
        assertEquals(2, records.size());
        assertEquals("USF:1.0\n" +
                "^w:7g7f3c3d6g6f7a6b2h6h5c5d1g1f1c1d5i4h5a4b4h3h4b3b3h2h6a5b3i3h6b5c6f6e5d5e6i5h8b6b7i7h6c6d6e6d5c6d" +
                "7h6g7c7d5g5fp*6e9i9h3a4b4g4f4b5c5h4g5c5d3g3f5e5f8h2B3b2b6g5fp*5e5f4e5d4e4f4eb*7i6h5hs*6g5h5i7i2D4e4" +
                "d2d3c4d4C5b4c4i4h6e6fP*4d4c4d2i3g6g6HB*9e6h5i9e6B4d4c6b6c3c4bR*6b6d5c6b6Ar*3a6a8a6f6G8a9a6g5hP*4d5c" +
                "4dP*4e5h4h4g4h4c5c4e4d5c6cL*4cp*4g4h4gp*4f4g4fg*4h3g2e5i5h4c4Bb*3i2h3g4h3h3g3h3i4H3h2i4a4b9a3a2b3a" +
                "R*6ap*4aN*4c4b4cG*3b3a3b4d4C3b4c6a6Cg*5cB*5d\n" +
                "BN:BBB\n" +
                "WN:CCC\n" +
                "GD:20020824\n" +
                "GQ:European Championship 2002", UsfFormat.INSTANCE.write(records.get(0)));
        assertEquals("USF:1.0\n" +
                "^b:7g7f8c8d7i6h3c3d6h7g5c5d5g5f8b5b2h5h7a6b6i7h6b5c3i4h5a4b4g4f5c6d4h4g4b3b3g3f7c7d7g6f8a7c5i4h6d" +
                "6e6f7g5d5e5f5e5b5eP*5f5e5b4h3i6a5a6g6f6e5d3i2h6c6d4i3h1c1d1g1f3a4b7g6h4b5c6h6g5a4b2i3g6d6e5h5i5b6b" +
                "6f6e2b8H7h8hb*4dB*7g5d6e7g4d5c4d4f4e4d3cB*4fb*8b8i7gp*6f7g6e6b6e6g6f6e6f8h7g6f6bP*6c6b6cP*6d7c6e6d" +
                "6C8b4f4g4fb*6h5i6i6e7G6i6h7g6h6c5c4b5cR*6b5c5b6b6Hr*4iS*3i4i4FB*7c4f4i7c9A1d1e3h4hs*1g1i1gs*1i2h1h" +
                "4i3i4h3hs*2i\n" +
                "BN:EEEE\n" +
                "WN:FFF\n" +
                "GD:20020824\n" +
                "GQ:European Championship 2002", UsfFormat.INSTANCE.write(records.get(1)));
    }
}