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

    public final static String PATH = "/home/jean/shogi/tsume/5/";

    public static void main(String[] args) {
        ProblemSetRepository rep = new ProblemSetRepository(new DbConnection());

        // int setId = rep.saveGameSet("57k Pro Games");
        int setId = 1;

        int ok = 0;
        int total = 0;
        int error = 0;

        Set<String> errors = new HashSet<>();

        for (int i = 51; i < 75; i++) {
            String fileName = PATH + "kif" + (1000 + i) + ".kif";
            File file = new File(fileName);
            if (file.exists() && file.length() > 10) {
                total++;
                System.out.println("Processing file " + fileName + " of length " + file.length());
                try {
                    processTsume(fileName, rep, setId, i);
                    ok++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error++;
                    errors.add(fileName);
                    break;
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

    private static void processTsume(final String fileName, final ProblemSetRepository repository, final int setId, final int kifuId)
            throws IOException {
        GameRecord gameRecord = GameRecordFileReader.read(KifFormat.INSTANCE, fileName);
        repository.addProblemToProblemSet(gameRecord, setId, "Tsume #" + kifuId, 1, 1000, PersistentProblem.ProblemType.TSUME);
        // System.out.println(UsfFormat.INSTANCE.write(gameRecord));
    }
}
