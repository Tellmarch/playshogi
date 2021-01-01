package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifUtils.PieceParsingResult;
import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.position.MutableKomadaiState;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.*;
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
                if (lineReader.hasNextLine()) {
                    l = lineReader.nextLine();
                    continue;
                } else {
                    break;
                }
            }

            if (l.equals("先手番")) {
                // sente to play
                if (lineReader.hasNextLine()) {
                    l = lineReader.nextLine();
                    continue;
                } else {
                    break;
                }
            }

            if (l.equals("後手番")) {
                // gote to play
                goteToPlay = true;
                if (lineReader.hasNextLine()) {
                    l = lineReader.nextLine();
                    continue;
                } else {
                    break;
                }
            }

            String[] sp = l.split("：", 2);
            if (sp.length < 2) {
                System.out.println("WARNING : unable to parse line " + l + " in file " + "???" + " , ignored.");
                if (lineReader.hasNextLine()) {
                    l = lineReader.nextLine();
                    continue;
                } else {
                    break;
                }
            }
            String field = sp[0];
            String value = sp[1];
            switch (field) {
                case "開始日時":
                    date = value;
                    break;
                case "棋戦":
                    tournament = value;
                    break;
                case "終了日時":
                    // ending time? ignore.
                    break;
                case "戦型":
                    opening = value;
                    break;
                case "場所":
                    place = value;
                    break;
                case "持ち時間":
                case "対局日":
                    time = value;
                    break;
                case "手合割":
                    handicap = value;
                    if (!value.startsWith("平手")) {
                        // TODO
                        // System.out.println("Handicap game, we ignore for
                        // now");
                        return null;
                    }
                    break;
                case "後手":
                case "上手":  // gote /
                    // handicap
                    // giver
                    gote = value;
                    break;
                case "先手":
                case "下手":  // sente /
                    // handicap
                    // receiver
                    sente = value;
                    break;
                case "備考":
                    // TODO : what is it?
                    break;
                case "作者":
                    // TODO: author
                    break;
                case "作意":
                    // TODO: conception
                    break;
                case "発表誌":
                    // TODO: magazine
                    break;
                case "目安時間":
                    // TODO: estimated time
                    break;
                case "思考時間":
                    // TODO: think time
                    break;
                case "詰手数":
                    // TODO: nr of moves
                    break;
                case "表題":
                    // TODO : what is it?
                    break;
                case "消費時間":
                    // Time used : ignored.
                    break;
                case "後手の持駒": {
                    // gote pieces in hand
                    if (startingPosition == null) {
                        startingPosition = new ShogiPosition();
                    }
                    MutableKomadaiState komadai = startingPosition.getMutableGoteKomadai();
                    readPiecesInHand(value, komadai);
                    lineReader.nextLine(); //  ９ ８ ７ ６ ５ ４ ３ ２ １

                    lineReader.nextLine(); // +---------------------------+

                    for (int row = 1; row <= 9; row++) {
                        l = lineReader.nextLine();
                        int pos = 1;
                        for (int column = 9; column >= 1; column--) {
                            PieceParsingResult pieceParsingResult = KifUtils.readPiece(l, pos);
                            pos = pieceParsingResult.nextPosition;
                            startingPosition.getMutableShogiBoardState().setPieceAt(Square.of(column, row),
                                    pieceParsingResult.piece);
                        }
                    }
                    lineReader.nextLine(); // +---------------------------+

                    break;
                }
                case "先手の持駒": {
                    // sente pieces in hand
                    if (startingPosition == null) {
                        startingPosition = new ShogiPosition();
                    }
                    MutableKomadaiState komadai = startingPosition.getMutableSenteKomadai();
                    readPiecesInHand(value, komadai);
                    break;
                }
                default:
                    System.out.println("WARNING : unknown field " + l + " in file " + "???" + " , ignored !");
                    break;
            }

            if (lineReader.hasNextLine()) {
                l = lineReader.nextLine();
            } else {
                break;
            }
        }
        // s.next();
        // s.useDelimiter("[ \r\n]");

        if (goteToPlay) {
            startingPosition.setPlayerToMove(Player.WHITE);
        }

        GameTree gameTree;
        if (startingPosition == null) {
            gameTree = new GameTree();
        } else {
            gameTree = new GameTree(startingPosition);
        }
        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

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

    private void readPiecesInHand(final String value, final MutableKomadaiState komadai) {
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

    public ShogiPosition readPosition(final String position) {
        List<GameRecord> gameRecords = read(position);
        if (gameRecords.size() == 0) {
            return null;
        }

        GameRecord record = gameRecords.get(0);
        return record.getInitialPosition().clonePosition();
    }
}
