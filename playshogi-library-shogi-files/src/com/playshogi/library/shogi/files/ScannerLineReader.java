package com.playshogi.library.shogi.files;

import java.util.Scanner;

import com.playshogi.library.shogi.models.formats.sfen.LineReader;

public class ScannerLineReader implements LineReader {
	public static final String UTF8_BOM = "\uFEFF";

	private final Scanner scanner;
	private boolean firstLine = true;

	public ScannerLineReader(final Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public boolean hasNextLine() {
		return scanner.hasNextLine();
	}

	@Override
	public String nextLine() {
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
