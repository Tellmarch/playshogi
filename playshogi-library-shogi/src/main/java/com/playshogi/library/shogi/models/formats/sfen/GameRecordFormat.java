package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameTree;

import java.util.List;

public interface GameRecordFormat {

    List<GameRecord> read(String string);

    List<GameRecord> read(LineReader lineReader);

    String write(GameRecord gameRecord);

    String write(GameTree gameTree);

}
