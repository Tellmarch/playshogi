package com.playshogi.library.database.collections.proset;

import com.playshogi.library.shogi.files.ScannerLineReader;
import com.playshogi.library.shogi.models.formats.csa.CsaFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.formats.util.GameRecordFormat;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.Collections.singletonList;

/**
 * WIP: import all kifus from zip file, while also checking for duplicates.
 */
public class ZipImporter {

    private static final File file = new File("/home/jean/shogi/bigdb/kifu.zip");
    private static final GameRecordFormat FORMAT = CsaFormat.INSTANCE;

    private int success = 0;
    private int errors = 0;
    private int total = 0;
    private int duplicate = 0;

    private HashMap<String, List<String>> previews = new HashMap<>();


    public static void main(String[] args) throws IOException {
        new ZipImporter().readZip();
    }

    private void readZip() throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));

        ZipEntry entry;

        List<GameRecord> records = new ArrayList<>();

        while ((entry = zipInputStream.getNextEntry()) != null) {
            try {
                List<GameRecord> gameRecords = readGameRecord(new Scanner(zipInputStream), entry.getName());
                for (GameRecord gameRecord : gameRecords) {
//                    records.add(gameRecord);
                    success++;
                    total++;
                    String previewString = UsfFormat.writePreviewString(gameRecord);
                    if (previews.containsKey(previewString)) {
                        duplicate++;
                        previews.get(previewString).add(entry.getName());
                        System.out.println("Duplicate: " + previews.get(previewString) + " - " + previewString);
                        return;
                    } else {
                        previews.put(previewString, new ArrayList<>(singletonList(entry.getName())));
                    }
                }
            } catch (Exception ex) {
                errors++;
                total++;
                System.err.println("Error reading kifu from file " + entry.getName());
                ex.printStackTrace();
                break;
            }
        }
        System.out.println("Read " + total + " kifus.");
        System.out.println("Success: " + success);
        System.out.println("Errors: " + errors);
        System.out.println("Unique: " + previews.size());
        System.out.println("Duplicates: " + duplicate);
    }

    private List<GameRecord> readGameRecord(final Scanner scanner, final String fileName) {
        if (fileName.endsWith("/")) {
            return Collections.emptyList();
        } else {
            System.out.println(total + " - Importing kifu: " + fileName);
            return FORMAT.read(new ScannerLineReader(scanner));
        }
    }
}
