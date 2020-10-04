package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.models.Square;
import com.playshogi.library.models.record.*;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifUtils.PieceParsingResult;
import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.position.KomadaiState;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Arrays;
import java.util.List;

public enum KifFormat implements GameRecordFormat {
    INSTANCE;

    @Override
    public List<GameRecord> read(String string) {
        return read(new StringLineReader(string));
    }

    @Override
    public List<GameRecord> read(final LineReader lineReader) {
        String date = "1000-1-1";
        String tournament = "UNKNOWN";
        String opening = "UNKNOWN";
        String place = "UNKNOWN";
        String time = "UNKNOWN";
        String handicap = "UNKNOWN";
        String gote = "UNKNOWN";
        String sente = "UNKNOWN";

        ShogiPosition startingPosition = null;

        boolean goteToPlay = false;

        String l = lineReader.nextLine();
        while (!l.startsWith("手数")) {
            l = l.trim();
            if (l.isEmpty() || l.startsWith("#")) {
                l = lineReader.nextLine();
                continue;
            }

            if (l.equals("先手番")) {
                // sente to play
                l = lineReader.nextLine();
                continue;
            }

            if (l.equals("後手番")) {
                // gote to play
                goteToPlay = true;
                l = lineReader.nextLine();
                continue;
            }

            String[] sp = l.split("：", 2);
            if (sp.length < 2) {
                System.out.println("WARNING : unable to parse line " + l + " in file " + "???" + " , ignored.");
                l = lineReader.nextLine();
                continue;
            }
            String field = sp[0];
            String value = sp[1];
            if (field.equals("開始日時")) {
                date = value;
            } else if (field.equals("棋戦")) {
                tournament = value;
            } else if (field.equals("終了日時")) {
                // ending time? ignore.
            } else if (field.equals("戦型")) {
                opening = value;
            } else if (field.equals("場所")) {
                place = value;
            } else if (field.equals("持ち時間") || field.equals("対局日")) {
                time = value;
            } else if (field.equals("手合割")) {
                handicap = value;
                if (!value.startsWith("平手")) {
                    // TODO
                    // System.out.println("Handicap game, we ignore for
                    // now");
                    return null;
                }
            } else if (field.equals("後手") || field.equals("上手")) { // gote /
                // handicap
                // giver
                gote = value;
            } else if (field.equals("先手") || field.equals("下手")) { // sente /
                // handicap
                // receiver
                sente = value;
            } else if (field.equals("備考")) {
                // TODO : what is it?
            } else if (field.equals("作者")) {
                // TODO: author
            } else if (field.equals("作意")) {
                // TODO: conception
            } else if (field.equals("発表誌")) {
                // TODO: magazine
            } else if (field.equals("目安時間")) {
                // TODO: estimated time
            } else if (field.equals("思考時間")) {
                // TODO: think time
            } else if (field.equals("詰手数")) {
                // TODO: nr of moves
            } else if (field.equals("表題")) {
                // TODO : what is it?
            } else if (field.equals("消費時間")) {
                // Time used : ignored.
            } else if (field.equals("後手の持駒")) {
                // gote pieces in hand
                startingPosition = new ShogiPosition();
                KomadaiState komadai = startingPosition.getGoteKomadai();
                readPiecesInHand(value, komadai);
                lineReader.nextLine(); //  ９ ８ ７ ６ ５ ４ ３ ２ １
                lineReader.nextLine(); // +---------------------------+
                for (int row = 1; row <= 9; row++) {
                    l = lineReader.nextLine();
                    int pos = 1;
                    for (int column = 9; column >= 1; column--) {
                        PieceParsingResult pieceParsingResult = KifUtils.readPiece(l, pos);
                        pos = pieceParsingResult.nextPosition;
                        startingPosition.getShogiBoardState().setPieceAt(Square.of(column, row),
                                pieceParsingResult.piece);
                    }
                }
                lineReader.nextLine(); // +---------------------------+
            } else if (field.equals("先手の持駒")) {
                // sente pieces in hand
                KomadaiState komadai = startingPosition.getSenteKomadai();
                readPiecesInHand(value, komadai);
            } else {
                System.out.println("WARNING : unknown field " + l + " in file " + "???" + " , ignored !");
            }
            l = lineReader.nextLine();
        }
        // s.next();
        // s.useDelimiter("[ \r\n]");

        if (goteToPlay) {
            startingPosition.setPlayerToMove(Player.WHITE);
        }

        GameTree gameTree;
        if (startingPosition == null) {
            startingPosition = ShogiInitialPositionFactory.createInitialPosition();
            gameTree = new GameTree();
        } else {
            gameTree = new GameTree(startingPosition);
        }
        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameTree, startingPosition);

        GameResult gameResult = GameResult.UNKNOWN;

        Player player = Player.BLACK;
        ShogiMove curMove;
        ShogiMove prevMove = null;
        int moveNumber = 1;
        while (lineReader.hasNextLine()) {
            String line = lineReader.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("*")) {
                continue;
            }
            String[] ts = line.split("\\s+", 2);
            int i;
            try {
                i = new Integer(ts[0]);
            } catch (Exception ex) {
                break;
            }
            if (i != moveNumber || ts.length < 2) {
                throw new IllegalArgumentException("Error after move " + moveNumber);
            }
            moveNumber++;
            String move = ts[1];
            curMove = KifMoveConverter.fromKifString(move, gameNavigation.getPosition(), prevMove, player);

            if (curMove instanceof SpecialMove) {
                SpecialMove specialMove = (SpecialMove) curMove;
                if (specialMove.getSpecialMoveType().isLosingMove()) {
                    gameResult = player == Player.BLACK ? GameResult.WHITE_WIN : GameResult.BLACK_WIN;
                }
            }

            gameNavigation.addMove(curMove);
            player = player.opposite();
            prevMove = curMove;
        }

        gameNavigation.moveToStart();
        GameInformation gameInformation = new GameInformation();
        gameInformation.setSente(sente);
        gameInformation.setGote(gote);
        gameInformation.setVenue(place);
        gameInformation.setDate(date);
        return Arrays.asList(new GameRecord(gameInformation, gameTree, gameResult));
    }

    private void readPiecesInHand(final String value, final KomadaiState komadai) {
        if (value.equals("なし")) {
            // nothing in hand
            return;
        }
        String[] piecesInHandStrings = value.split("　");
        for (String pieceString : piecesInHandStrings) {
            PieceParsingResult pieceParsingResult = KifUtils.readPiece(pieceString, 0);
            int number;
            if (pieceString.length() == 1) {
                number = 1;
            } else if (pieceString.length() == 2) {
                number = KifUtils.getNumberFromJapanese(pieceString.charAt(1));
            } else if (pieceString.length() == 3 && pieceString.charAt(1) == '十') {
                number = 10 + KifUtils.getNumberFromJapanese(pieceString.charAt(2));
            } else {
                throw new IllegalArgumentException("Error reading pieces in hand: " + value);
            }
            komadai.setPiecesOfType(pieceParsingResult.piece.getPieceType(), number);
        }
    }

    @Override
    public String write(final GameRecord gameRecord) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String write(final GameTree gameTree) {
        throw new UnsupportedOperationException();
    }

}
