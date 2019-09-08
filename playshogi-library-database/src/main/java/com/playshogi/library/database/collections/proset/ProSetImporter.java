package com.playshogi.library.database.collections.proset;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.GameSetRepository;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.GameRecordFileReader;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProSetImporter {

    public final static String PATH = "/home/jean/shogi/kifus/";

    public static void main(final String[] args) {
        GameSetRepository rep = new GameSetRepository(new DbConnection());

        // int setId = rep.saveGameSet("57k Pro Games");
        int setId = 1;
        int venueId = 1;

        int ok = 0;
        int total = 0;
        int error = 0;

        Set<String> errors = new HashSet<>();

        for (int i = 26; i < 27; i++) {
            String fileName = PATH + "kif" + i + ".kif";
            File file = new File(fileName);
            if (file.exists() && file.length() > 10) {
                total++;
                System.out.println("Processing file " + fileName + " of length " + file.length());
                try {
                    processKifu(fileName, rep, setId, i, venueId);
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

    private static void processKifu(final String fileName, final GameSetRepository repository, final int setId,
                                    final int kifuId, final int venueId)
            throws IOException {
        GameRecord gameRecord = GameRecordFileReader.read(KifFormat.INSTANCE, fileName, "windows-932");
        repository.addGameToGameSet(gameRecord, setId, venueId, "Pro Classic Games #" + kifuId, 1);
        // System.out.println(UsfFormat.INSTANCE.write(gameRecord));
    }
}
