package com.playshogi.library.shogi.files;

import com.playshogi.library.shogi.models.formats.util.GameRecordFormat;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GameRecordFileReader {

    public static List<GameRecord> read(final GameRecordFormat gameRecordFormat, final File file) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            return gameRecordFormat.read(new ScannerLineReader(scanner));
        }
    }

    public static List<GameRecord> read(final GameRecordFormat gameRecordFormat, final File file, final String encoding)
            throws IOException {
        try (Scanner scanner = new Scanner(file, encoding)) {
            return gameRecordFormat.read(new ScannerLineReader(scanner));
        }
    }
}
