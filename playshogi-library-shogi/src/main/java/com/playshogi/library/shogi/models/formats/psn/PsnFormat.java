package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.models.record.*;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public enum PsnFormat implements GameRecordFormat {
    INSTANCE;

    @Override
    public GameRecord read(String string) {
        return read(new StringLineReader(string));
    }

    @Override
    public GameRecord read(final LineReader lineReader) {
        GameTree gameTree = new GameTree();
        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameTree, ShogiInitialPositionFactory.createInitialPosition());

        GameInformation gameInformation = new GameInformation();

        while (lineReader.hasNextLine()) {
            String line = lineReader.nextLine();
            if (line.startsWith("[")) {
                String[] split = line.split(" ", 2);
                String key = split[0].substring(1);
                String value = split[1].substring(1, split[1].length() - 2);

                if ("Black".equalsIgnoreCase(key) || "Sente".equalsIgnoreCase(key)) {
                    gameInformation.setSente(value);
                } else if ("White".equalsIgnoreCase(key) || "Gote".equalsIgnoreCase(key)) {
                    gameInformation.setGote(value);
                } else if ("Date".equalsIgnoreCase(key)) {
                    gameInformation.setDate(value);
                } else if ("Event".equalsIgnoreCase(key)) {
                    gameInformation.setVenue(value);
                }
            } else if (line.startsWith("{")) {
                //TODO comment
            } else {
                ShogiMove move = PsnMoveConverter.fromKifString(line, gameNavigation.getPosition());
                gameNavigation.addMove(move);
            }
        }

        GameResult gameResult;
        if (((ShogiMove) gameNavigation.getCurrentMove()).getPlayer() == Player.BLACK) {
            gameResult = GameResult.GOTE_WIN;
        } else {
            gameResult = GameResult.SENTE_WIN;
        }

        gameNavigation.moveToStart();

        return new GameRecord(gameInformation, gameTree, gameResult);
    }

    @Override
    public String write(final GameRecord gameRecord) {
        return null;
    }

    @Override
    public String write(final GameTree gameTree) {
        return null;
    }
}
