package com.playshogi.library.shogi.files;

import java.io.IOException;

import com.playshogi.library.shogi.models.formats.kif.KifFormat;

public class GameRecordFileReaderTest {

	public static void main(final String[] args) throws IOException {
		GameRecordFileReader.read(KifFormat.INSTANCE, "/home/jean/051806.kif");
	}
}
