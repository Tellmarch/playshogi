package com.playshogi.library.shogi.files;

import java.util.Scanner;

import com.playshogi.library.shogi.models.formats.sfen.LineReader;

public class ScannerLineReader implements LineReader {

	private final Scanner scanner;

	public ScannerLineReader(final Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public boolean hasNextLine() {
		return scanner.hasNextLine();
	}

	@Override
	public String nextLine() {
		return scanner.nextLine();
	}
}
