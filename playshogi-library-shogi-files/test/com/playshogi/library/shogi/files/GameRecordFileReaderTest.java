package com.playshogi.library.shogi.files;

import java.io.IOException;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.GameRecordUtils;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;

public class GameRecordFileReaderTest {

	public static void main(final String[] args) throws IOException {
		GameRecord gameRecord = GameRecordFileReader.read(KifFormat.INSTANCE, "/home/jean/051806.kif");
		GameRecordUtils.print(gameRecord);
	}
}
