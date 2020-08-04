package com.playshogi.library.shogi.files;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class GameRecordFileReader {

    public static GameRecord read(final GameRecordFormat gameRecordFormat, final String fileName) throws IOException {
        return read(gameRecordFormat, new File(fileName));
    }

    public static GameRecord read(final GameRecordFormat gameRecordFormat, final File file) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            return gameRecordFormat.read(new ScannerLineReader(scanner));
        }
    }

    public static GameRecord read(final GameRecordFormat gameRecordFormat, final String fileName, final String encoding)
            throws IOException {
        return read(gameRecordFormat, new File(fileName), encoding);
    }

    public static GameRecord read(final GameRecordFormat gameRecordFormat, final File file, final String encoding)
            throws IOException {
        try (Scanner scanner = new Scanner(file, encoding)) {
            return gameRecordFormat.read(new ScannerLineReader(scanner));
        }
    }
}
