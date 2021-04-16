package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.files.GameRecordFileReader;
import com.playshogi.library.shogi.models.formats.psn.PsnFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.library.shogi.models.record.GameRecord;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class ProblemExtractorTest {

    @Test
    public void extractProblems() {
        String usf = "USF:1.0\n" +
                "^*:7g7f3c3d2g2f2b8H7i8h4a3b4i5h3a4bB" +
                "*4e7a6b4e3d4c4d6g6f4b4c3d7h6c6d8h7g6b6c2f2e3b3c3i3h4c3d4g4f8b4b3h4g4d4e4f4e3d4e2e2dp*4f4g3h3c2dP" +
                "*4h2a3c6i6h5a6b3h2g6b7b1g1f4e3d5i6ib*4g4h4g4f4G5h5ip*3hB*3a4b4f2g3f4g3g2i3g4f3f2h3hs*4gP" +
                "*3i4g3H3i3h3f2f3a5C2f2I7h5f7c7dP*2e3c2e3g2e3d2eN*5en*5a5e6C5a6c5f4en*7aS*5d7b8b5d6C7a6c4e6Cp*4hS" +
                "*7a8b9b5c6b6a6b6c6b2i5i6i5ir*4i5i5hb*3fP*4gg*5i5h6g3f4e5g5fRSGN\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                "GD:21/07/2013\n" +
                "GQ:ESC/WOSC 2013\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
        List<ExtractedProblem> problems = new ProblemExtractor().extractProblems(gameRecord);
        System.out.println(problems);
    }

    @Test
    public void extractProblemsFromESCTemplate() throws IOException {
        String path = "/home/jean/shogi/collections/games/";
        ArrayList<ExtractedProblem> extractedProblems = new ArrayList<>();

        for (int i = 1; i <= 43; i++) {
            File f = new File(path + (i < 10 ? "0" + i : i) + ".txt");
            System.out.println(f);
            List<GameRecord> records = GameRecordFileReader.read(PsnFormat.INSTANCE, f);
            System.out.println(records.get(0));
            List<ExtractedProblem> problems = new ProblemExtractor().extractProblems(records.get(0));
            System.out.println(problems);
            extractedProblems.addAll(problems);
        }

        System.out.println(ProblemExtractor.problemsToUSF(extractedProblems));


    }

    @Test
    public void problemToUSF() {
        GameInformation gameInformation = new GameInformation();
        gameInformation.setBlack("AAA aaa");
        gameInformation.setWhite("BBB bbb");

        ExtractedProblem problem = new ExtractedProblem(ExtractedProblem.ProblemType.WINNING_OR_LOSING, "lnS5l/k2+B5" +
                "/pp5pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN2K3L w RG2N2Prbgs2p", "r*2i 5i4h p*4g 4h4g 2i4I R*4h b*3f " +
                "4g5f s*4g 5f6g 3f4e G*5f 4g5F 5g5f 4i4h 7f7e r*5i 6g7f 4e5d N*6e ", "6i5i", gameInformation);
        String usf = ProblemExtractor.problemToUSF(problem);
        assertEquals("^*lnS5l/k2+B5/pp5pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN2K3L w " +
                "RG2N2Prbgs2p:r*2i5i4hp*4g4h4g2i4IR*4hb*3f4g5fs*4g5f6g3f4eG*5f4g5F5g5f4i4h7f7er*5i6g7f4e5dN*6e\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                ".0\n" +
                "X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_LOSING\n" +
                "X:PLAYSHOGI:PREVIOUSMOVE:6i5i\n", usf);
    }

    @Test
    public void problemsToUSF() {
        GameInformation gameInformation = new GameInformation();
        gameInformation.setBlack("AAA aaa");
        gameInformation.setWhite("BBB bbb");

        ExtractedProblem problem = new ExtractedProblem(ExtractedProblem.ProblemType.WINNING_OR_LOSING, "lnS5l/k2+B5" +
                "/pp5pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN2K3L w RG2N2Prbgs2p", "r*2i 5i4h p*4g 4h4g 2i4I R*4h b*3f " +
                "4g5f s*4g 5f6g 3f4e G*5f 4g5F 5g5f 4i4h 7f7e r*5i 6g7f 4e5d N*6e ", "6i5i", gameInformation);
        String usf = ProblemExtractor.problemsToUSF(Arrays.asList(problem, problem));
        assertEquals("USF:1.0\n" +
                "^*lnS5l/k2+B5/pp5pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN2K3L w " +
                "RG2N2Prbgs2p:r*2i5i4hp*4g4h4g2i4IR*4hb*3f4g5fs*4g5f6g3f4eG*5f4g5F5g5f4i4h7f7er*5i6g7f4e5dN*6e\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                ".0\n" +
                "X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_LOSING\n" +
                "X:PLAYSHOGI:PREVIOUSMOVE:6i5i\n" +
                "\n" +
                "^*lnS5l/k2+B5/pp5pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN2K3L w " +
                "RG2N2Prbgs2p:r*2i5i4hp*4g4h4g2i4IR*4hb*3f4g5fs*4g5f6g3f4eG*5f4g5F5g5f4i4h7f7er*5i6g7f4e5dN*6e\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                ".0\n" +
                "X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_LOSING\n" +
                "X:PLAYSHOGI:PREVIOUSMOVE:6i5i\n" +
                "\n", usf);
    }
}