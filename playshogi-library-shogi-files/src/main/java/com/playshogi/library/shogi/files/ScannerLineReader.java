package com.playshogi.library.shogi.files;

import com.playshogi.library.shogi.models.formats.sfen.LineReader;

import java.util.Scanner;

public class ScannerLineReader implements LineReader {
    public static final String UTF8_BOM = "\uFEFF";

    private final Scanner scanner;
    private boolean firstLine = true;
    private String nextLine;

    public ScannerLineReader(final Scanner scanner) {
        this.scanner = scanner;
        nextLine = readNextLine();
    }

    @Override
    public boolean hasNextLine() {
        return nextLine != null;
    }

    @Override
    public String nextLine() {
        String current = nextLine;
        nextLine = readNextLine();
        return current;
    }

    @Override
    public String peekNextLine() {
        return nextLine;
    }

    private String readNextLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }

        if (firstLine) {
            firstLine = false;
            return removeUTF8BOM(scanner.nextLine());
        } else {
            return scanner.nextLine();
        }
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
}
