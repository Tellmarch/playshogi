package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.files.GameRecordFileReader;
import com.playshogi.library.shogi.models.formats.psn.PsnFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
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
        String usf = "    USF:1.0\n" +
                "^*:7g7f3c3d6g6f8c8d7i7h7a6b7h7g5c5d5g5f6a5b1g1f1c1d6i6h8d8e6h5g3a4b2h6h4b5c5i4h5a4b4h3h4b3b3h2h7c7d3i" +
                "3h9c9d9g9f4a4b4g4f6b7c6f6e6c6d6e6d7c6d5g6f8b6bP*6e6d7c7f7e7d7e6f7ep*7d7e8ep*6d6e6d5c6dP" +
                "*7e6d6e7e7d7c6" +
                "d5f5e2b5e7g6f6e6f8h6f5e6f6h6fb*5e6f6ip*6g6i6gs*7f6g6i7f8eB*8d6b8bS*7ep*6cP" +
                "*6e6d7e8d7e8e7d7e8f5e9I6e6d" +
                "l*6e6i7ip*7e6d6C5b6c8f7g9i7g8i7g6e6H7i8ib*6g8i9i8b8GP*6d6c5cS*6c8g8h6c7D8h9i6d6C6g4I6c5c4i3i2h1h" +
                "s*1gRSGN\n";
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
        ExtractedProblem problem = new ExtractedProblem(ExtractedProblem.ProblemType.ESCAPE_MATE, "lnSg4l/k2" +
                "+B5/pp1+B3pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN1KG2+rL w 2N2Prs2p", "6a6b 7a6B b*4g 6i7h 2i5i G*6i " +
                "s*6g 7h6g g*5f 6g7h 4g6I 6h6i g*6g 7h8h 5i6i ");
        String usf = ProblemExtractor.problemToUSF(problem);
        assertEquals("USF:1.0\n" +
                "^*lnSg4l/k2+B5/pp1+B3pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN1KG2+rL w " +
                "2N2Prs2p:6a6b7a6Bb*4g6i7h2i5iG*6is*6g7h6gg*5f6g7h4g6I6h6ig*6g7h8h5i6i", usf);
    }

    @Test
    public void problemsToUSF() {
        ExtractedProblem problem = new ExtractedProblem(ExtractedProblem.ProblemType.ESCAPE_MATE, "lnSg4l/k2" +
                "+B5/pp1+B3pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN1KG2+rL w 2N2Prs2p", "6a6b 7a6B b*4g 6i7h 2i5i G*6i " +
                "s*6g 7h6g g*5f 6g7h 4g6I 6h6i g*6g 7h8h 5i6i ");
        String usf = ProblemExtractor.problemsToUSF(Arrays.asList(problem, problem));
        assertEquals("USF:1.0\n" +
                "^*lnSg4l/k2+B5/pp1+B3pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN1KG2+rL w " +
                "2N2Prs2p:6a6b7a6Bb*4g6i7h2i5iG*6is*6g7h6gg*5f6g7h4g6I6h6ig*6g7h8h5i6i\n" +
                "^*lnSg4l/k2+B5/pp1+B3pp/2pp3g1/7s1/2PP4P/PPS1P4/3G1pP2/LN1KG2+rL w " +
                "2N2Prs2p:6a6b7a6Bb*4g6i7h2i5iG*6is*6g7h6gg*5f6g7h4g6I6h6ig*6g7h8h5i6i\n", usf);
    }
}