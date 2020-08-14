package com.playshogi.library.database.collections.tsume1;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.ProblemSetRepository;
import com.playshogi.library.database.models.PersistentProblem;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.GameRecordFileReader;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TsumeImporter {

    private final static int[] MOVES = {3, 5, 7, 9, 11, 13};

    private enum PATH {
        THREE_MOVES("/home/jean/shogi/tsume/3/03", 1, 1000, 4),
        FIVE_MOVES("/home/jean/shogi/tsume/5/kif", 1001, 2000, 4),
        SEVEN_MOVES("/home/jean/shogi/tsume/7/tsume_07_", 100, 900, 3),
        NINE_MOVES("/home/jean/shogi/tsume/9/09", 1, 900, 4),
        ELEVEN_MOVES("/home/jean/shogi/tsume/11/11", 1, 500, 4),
        THIRTEEN_MOVES("/home/jean/shogi/tsume/13/13", 1, 500, 4);

        String path;
        int min;
        int max;
        int padding;

        PATH(String path, int min, int max, int padding) {
            this.path = path;
            this.min = min;
            this.max = max;
            this.padding = padding;
        }
    }

    public static void main(String[] args) {
        ProblemSetRepository rep = new ProblemSetRepository(new DbConnection());

        int setId = 1;

        int ok = 0;
        int total = 0;
        int error = 0;

        Set<String> errors = new HashSet<>();

        for (PATH path : PATH.values()) {
            System.out.println("Processing path " + path.path);
            for (int i = path.min; i <= path.max; i++) {
                String fileName =  String.format("%0" + path.padding + "d", i) + ".kif";
                File file = new File(path.path, fileName);
                System.out.println("Trying file " + file);
                if (file.exists() && file.length() > 10) {
                    total++;
                    System.out.println("Processing file " + file + " of length " + file.length());
                    try {
                        processTsume(file, rep, setId, i);
                        ok++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        error++;
                        errors.add(fileName);
                        break;
                    }
                } else {
                    System.out.println("Not found: " + file);
                }
            }
        }



        System.out.println("Import result");
        System.out.println("=============");
        System.out.println("File processed: " + total);
        System.out.println("Import successfully: " + ok);
        System.out.println("Errors during import: " + error);
        System.out.println("=============");
        System.out.println("List of errors: " + errors);

    }

    private static void processTsume(final File file, final ProblemSetRepository repository, final int setId,
                                     final int kifuId)
            throws IOException {
        GameRecord gameRecord = GameRecordFileReader.read(KifFormat.INSTANCE, file);
        repository.addProblemToProblemSet(gameRecord, setId, "Tsume #" + kifuId, 1, 1000,
                PersistentProblem.ProblemType.TSUME);
        // System.out.println(UsfFormat.INSTANCE.write(gameRecord));
    }
}
