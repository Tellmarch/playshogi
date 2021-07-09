package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.util.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.util.LineReader;
import com.playshogi.library.shogi.models.formats.util.StringLineReader;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.moves.SpecialMoveType;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.ArrayList;
import java.util.List;

public enum PsnFormat implements GameRecordFormat {
    INSTANCE;

    @Override
    public List<GameRecord> read(String string) {
        return read(new StringLineReader(string));
    }

    @Override
    public List<GameRecord> read(final LineReader lineReader) {
        List<GameRecord> result = new ArrayList<>();
        GameRecord gameRecord;
        do {
            gameRecord = readGameRecord(lineReader);
            if (gameRecord != null) {
                result.add(gameRecord);
            }
        } while (gameRecord != null);

        return result;
    }

    private GameRecord readGameRecord(final LineReader lineReader) {

        GameInformation gameInformation = readGameInformation(lineReader);
        if (gameInformation == null) {
            return null;
        }

        return readGameMoves(lineReader, gameInformation);
    }

    private GameRecord readGameMoves(final LineReader lineReader, final GameInformation gameInformation) {

        GameTree gameTree = new GameTree();
        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

        while (lineReader.hasNextLine()) {
            String line = lineReader.nextLine();
            if (line.startsWith("--")) {
                // Ex: --Black Won-- as the last line
                gameNavigation.addMove(new SpecialMove(gameNavigation.getPosition().getPlayerToMove(),
                        SpecialMoveType.RESIGN));
            } else if (line.contains("$")) {
                // TODO tsume: read position, then solution
                // Ex: wK3a wN2a wL1a bB5b bP4c wN2c wP3d wN2d wP2e bBH1 bRH1 wPH15 wLH3 wNH1 wSH4 wGH4 wRH1 $
                // B5b-4a+ K3a-2b +B4a-3b
            } else if (line.startsWith("{") && line.endsWith("}")) {
                //TODO comment
            } else if (line.trim().isEmpty()) {
                // Empty line: separation between records
                break;
            } else {
                String[] split = removeComments(line).split("\\s+");
                int nestedParenthesis = 0;
                for (String token : split) {
                    if ("(".equals(token)) {
                        nestedParenthesis++;
                        continue;
                    } else if (")".equals(token)) {
                        nestedParenthesis--;
                        continue;
                    }

                    if (nestedParenthesis == 0 && !token.isEmpty()) {
                        ShogiMove move = PsnMoveConverter.fromPsnString(token, gameNavigation.getPosition());
                        gameNavigation.addMove(move);
                    }
                }
            }
        }

        GameResult gameResult;
        Move currentMove = gameNavigation.getCurrentMove();
        if (currentMove != null) {
            if (((ShogiMove) currentMove).getPlayer() == Player.BLACK) {
                gameResult = GameResult.WHITE_WIN;
            } else {
                gameResult = GameResult.BLACK_WIN;
            }
        } else {
            gameResult = GameResult.UNKNOWN;
        }

        gameNavigation.moveToStart();

        return new GameRecord(gameInformation, gameTree, gameResult);
    }

    private String removeComments(final String line) {
        return line.replaceAll("\\s*\\{[^}]*}\\s*", " ");
    }

    private GameInformation readGameInformation(final LineReader lineReader) {
        GameInformation gameInformation = new GameInformation();
        boolean found = false;

        while (lineReader.hasNextLine()) {
            String nextLine = lineReader.peekNextLine();
            if (!nextLine.startsWith("[")) {
                return gameInformation;
            }
            String line = lineReader.nextLine();
            if (line.startsWith("[")) {
                found = true;
                String[] split = line.split(" ", 2);
                String key = split[0].substring(1);
                String value = split[1].substring(1, split[1].length() - 2);

                if ("Black".equalsIgnoreCase(key) || "Sente".equalsIgnoreCase(key)) {
                    gameInformation.setBlack(value);
                } else if ("White".equalsIgnoreCase(key) || "Gote".equalsIgnoreCase(key)) {
                    gameInformation.setWhite(value);
                } else if ("Date".equalsIgnoreCase(key)) {
                    gameInformation.setDate(value);
                } else if ("Event".equalsIgnoreCase(key)) {
                    gameInformation.setEvent(value);
                }
            }
        }

        return found ? gameInformation : null;
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
