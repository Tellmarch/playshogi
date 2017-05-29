package com.playshogi.library.shogi.files;

import java.io.IOException;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.GameRecordUtils;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;

public class GameRecordFileReaderTest {

	public static void main(final String[] args) throws IOException {
		GameRecordUtils.print(getExampleTsumeGameRecord());

		// GameRecordUtils.print(getExampleGameRecord());
	}

	public static GameRecord getExampleGameRecord() throws IOException {
		return GameRecordFileReader.read(KifFormat.INSTANCE, "/home/jean/shogi/kifus/kif74471.kif", "windows-932");
	}

	public static GameRecord getExampleTsumeGameRecord() throws IOException {
		return GameRecordFileReader.read(KifFormat.INSTANCE, "/home/jean/shogi/tsume/5/kif1806.kif");
	}
}
