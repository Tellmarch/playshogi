package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifUtils.PieceParsingResult;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.util.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.util.LineReader;
import com.playshogi.library.shogi.models.formats.util.StringLineReader;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.position.MutableKomadaiState;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Collections;
import java.util.List;

public enum KifFormat implements GameRecordFormat {
    INSTANCE;

    private static final String GOTE_TO_PLAY = "後手番";
    private static final String SENTE_TO_PLAY = "先手番";
    private static final String START_OF_MOVES_SECTION = "手数-";
    private static final String START_DATE_AND_TIME = "開始日時";
    private static final String TOURNAMENT = "棋戦";
    private static final String END_DATE_AND_TIME = "終了日時";
    private static final String OPENING = "戦型";
    private static final String PLACE = "場所";
    private static final String TIME_CONTROL = "持ち時間";
    private static final String GAME_DAY = "対局日";
    private static final String HANDICAP = "手合割";
    private static final String HIRATE = "平手";
    private static final String OTHER = "その他"; // Other handicap
    private static final String GOTE = "後手";
    private static final String HANDICAP_GIVER = "上手";
    private static final String SENTE = "先手";
    private static final String HANDICAP_RECEIVER = "下手";
    private static final String REFERENCE = "備考";
    private static final String AUTHOR = "作者";
    private static final String PUBLICATION = "発表誌";
    private static final String ESTIMATED_TIME = "目安時間";
    private static final String THINKING_TIME = "思考時間";
    private static final String NUMBER_OF_MOVES = "詰手数";
    private static final String NUMBER_OF_MOVES_ALT = "手数";
    private static final String HEADING = "表題";
    private static final String TIME_SPENT = "消費時間";
    private static final String GOTE_PIECES_IN_HAND = "後手の持駒";
    private static final String HANDICAP_GIVER_PIECES_IN_HAND = "上手の持駒";
    private static final String SENTE_PIECES_IN_HAND = "先手の持駒";
    private static final String HANDICAP_RECEIVER_PIECES_IN_HAND = "下手の持駒";
    private static final String NONE = "なし"; // For pieces in hand
    private static final String HANDICAP_GIVER_TO_PLAY = "上手番";
    private static final String HANDICAP_RECEIVER_TO_PLAY = "下手番";
    private static final String SENTE_CASTLE = "先手の囲い";
    private static final String GOTE_CASTLE = "後手の囲い";
    private static final String SENTE_REMARKS = "先手の備考";
    private static final String GOTE_REMARKS = "後手の備考";
    private static final String SOURCE = "出典";
    private static final String PROBLEM_NUMBER = "作品番号";
    private static final String PUBLICATION_DATE = "発表年月";
    private static final String STATUS = "完全性";
    private static final String CLASSIFICATION = "分類";

    @Override
    public List<GameRecord> read(String string) {
//        return read(new DebugLineReader(new StringLineReader(string)));
        return read(new StringLineReader(string));
    }

    @Override
    public List<GameRecord> read(final LineReader lineReader) {
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameResult(GameResult.UNKNOWN);

        readHeader(lineReader, gameRecord);
        readMoves(lineReader, gameRecord);

        if (gameRecord.isEmpty()) {
            throw new IllegalStateException("Couldn't parse the game record");
        }

        return Collections.singletonList(gameRecord);
    }

    private void readHeader(final LineReader lineReader, final GameRecord gameRecord) {
        while (true) {
            if (!lineReader.hasNextLine()) {
                break;
            }

            String l = lineReader.peekNextLine().trim();

            if (l.startsWith(START_OF_MOVES_SECTION)) { // Reached the moves section
                lineReader.nextLine();
                break;
            }

            if (l.startsWith("1") || l.startsWith("*")) { // Reached the first move
                break;
            }

            if (l.startsWith(GOTE_PIECES_IN_HAND) || l.startsWith(HANDICAP_GIVER_PIECES_IN_HAND)) {
                readStartingPosition(lineReader, gameRecord);
                continue;
            }

            lineReader.nextLine();

            readHeaderLine(gameRecord, l);
        }

        gameRecord.getGameTree().cleanUpInitialPosition();
    }

    private void readHeaderLine(final GameRecord gameRecord, final String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }

        if (line.equals(SENTE_TO_PLAY) || line.equals(HANDICAP_RECEIVER_TO_PLAY)) {
            return;
        }

        if (line.equals(GOTE_TO_PLAY) || line.equals(HANDICAP_GIVER_TO_PLAY)) {
            ShogiPosition position = gameRecord.getInitialPosition().clonePosition();
            position.setPlayerToMove(Player.WHITE);
            gameRecord.getGameTree().setInitialPosition(position);
            return;
        }

        if (line.startsWith("手数＝")) { // number of moves played so far
            return;
        }

        String[] sp = line.split("：", 2);
        if (sp.length < 2) {
            System.out.println("WARNING : unable to parse line " + line + ", ignored.");
            return;
        }
        String field = sp[0];
        String value = sp[1];
        switch (field) {
            case START_DATE_AND_TIME:
            case GAME_DAY:
                gameRecord.getGameInformation().setDate(value);
                break;
            case TOURNAMENT:
                gameRecord.getGameInformation().setEvent(value);
                break;
            case OPENING:
                gameRecord.getGameInformation().setOpening(value);
                break;
            case PLACE:
                gameRecord.getGameInformation().setLocation(value);
                break;
            case HANDICAP:
                if (!(value.startsWith(HIRATE) || value.startsWith(OTHER))) {
                    boolean found = false;
                    for (Handicap handicap : Handicap.values()) {
                        if (handicap.getJapanese().equals(value)) {
                            found = true;
                            gameRecord.getGameTree().setInitialPosition(
                                    ShogiInitialPositionFactory.createInitialPosition(handicap));
                        }
                    }

                    if (!found) {
                        throw new IllegalArgumentException("Unknown handicap type: " + value);
                    }
                }
                break;
            case GOTE:
            case HANDICAP_GIVER:
                gameRecord.getGameInformation().setWhite(value);
                break;
            case SENTE:
            case HANDICAP_RECEIVER:
                gameRecord.getGameInformation().setBlack(value);
                break;
            case TIME_CONTROL:
                gameRecord.getGameInformation().setTimeControl(value);
                break;
            case "作意":
                // TODO: conception
                break;
            case GOTE_PIECES_IN_HAND:
            case HANDICAP_GIVER_PIECES_IN_HAND:
            case SENTE_PIECES_IN_HAND:
            case HANDICAP_RECEIVER_PIECES_IN_HAND:
                throw new IllegalStateException("Should have processed pieces in hand while reading position");
            case END_DATE_AND_TIME:
            case PUBLICATION:
            case ESTIMATED_TIME:
            case THINKING_TIME:
            case NUMBER_OF_MOVES:
            case NUMBER_OF_MOVES_ALT:
            case HEADING:
            case TIME_SPENT:
            case REFERENCE:
            case AUTHOR:
            case SENTE_CASTLE:
            case GOTE_CASTLE:
            case SENTE_REMARKS:
            case GOTE_REMARKS:
            case SOURCE:
            case PUBLICATION_DATE:
            case PROBLEM_NUMBER:
            case STATUS:
            case CLASSIFICATION:
                break;
            default:
                System.out.println("WARNING : unknown field " + line + " when parsing kifu, ignored !");
                break;
        }
    }

    // Example position:
    //    後手の持駒： 金三　歩十二　
    //      ９ ８ ７ ６ ５ ４ ３ ２ １
    //    +---------------------------+
    //    | ・v歩 ・ ・ ・ ・ ・v桂 銀|一
    //    | ・ ・ ・v銀 ・v桂v玉 ・ ・|二
    //    | 香 桂 ・ ・v香 ・v歩 飛v銀|三
    //    | ・ ・ ・ 桂 ・ 金 ・ ・ ・|四
    //    | ・ ・ ・ ・ ・ ・ 銀 ・ ・|五
    //    | ・ ・ 歩 歩 ・ 歩 ・ ・ ・|六
    //    | ・ ・ ・ 馬 ・ ・ ・v飛 ・|七
    //    | ・ 香 ・ ・ ・ ・ ・ 香 ・|八
    //    | ・ ・ ・ ・ ・ ・ 馬 ・ ・|九
    //    +---------------------------+
    //    先手の持駒：歩　
    private void readStartingPosition(final LineReader lineReader, final GameRecord gameRecord) {
        ShogiPosition position = gameRecord.getInitialPosition().clonePosition();

        String goteKomadaiLine = lineReader.nextLine().trim();

        if (!goteKomadaiLine.equals(GOTE_PIECES_IN_HAND) && !goteKomadaiLine.equals(HANDICAP_GIVER_PIECES_IN_HAND)) {
            String[] goteSplit = goteKomadaiLine.split("：", 2);
            if (goteSplit.length < 2 ||
                    (!goteSplit[0].equals(GOTE_PIECES_IN_HAND) && !goteSplit[0].equals(HANDICAP_GIVER_PIECES_IN_HAND))) {
                throw new IllegalArgumentException("ERROR : unable to parse gote komadai line " + goteKomadaiLine);
            }

            MutableKomadaiState komadai = position.getMutableGoteKomadai();
            readPiecesInHand(goteSplit[1], komadai);
        }

        lineReader.nextLine(); //  ９ ８ ７ ６ ５ ４ ３ ２ １
        lineReader.nextLine(); // +---------------------------+

        for (int row = 1; row <= 9; row++) {
            String l = lineReader.nextLine();
            int pos = 1;
            for (int column = 9; column >= 1; column--) {
                PieceParsingResult pieceParsingResult = KifUtils.readPiece(l, pos);
                pos = pieceParsingResult.nextPosition;
                position.getMutableShogiBoardState().setPieceAt(Square.of(column, row),
                        pieceParsingResult.piece);
            }
        }
        lineReader.nextLine(); // +---------------------------+

        String senteKomadaiLine = lineReader.nextLine().trim();
        if (!senteKomadaiLine.equals(SENTE_PIECES_IN_HAND) && !senteKomadaiLine.equals(HANDICAP_RECEIVER_PIECES_IN_HAND)) {
            String[] senteSplit = senteKomadaiLine.split("：", 2);
            if (senteSplit.length < 2 ||
                    (!senteSplit[0].equals(SENTE_PIECES_IN_HAND) &&
                            !senteSplit[0].equals(HANDICAP_RECEIVER_PIECES_IN_HAND))) {
                throw new IllegalArgumentException("ERROR : unable to parse sente komadai line " + senteKomadaiLine);
            }

            MutableKomadaiState senteKomadai = position.getMutableSenteKomadai();
            readPiecesInHand(senteSplit[1], senteKomadai);
        }

        gameRecord.getGameTree().setInitialPosition(position);
    }

    private void readPiecesInHand(final String value, final MutableKomadaiState komadai) {
        if (NONE.equals(value) || "".equals(value)) {
            // nothing in hand
            return;
        }
        String[] piecesInHandStrings = value.trim().split("[ 　]", 0);

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
                throw new IllegalArgumentException("Error reading pieces in hand: " + value + " at " + pieceString);
            }
            komadai.setPiecesOfType(pieceParsingResult.piece.getPieceType(), number);
        }
    }

    private void readMoves(final LineReader lineReader, final GameRecord gameRecord) {
        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameRecord.getGameTree());

        ShogiMove curMove;
        ShogiMove prevMove = null;

        String currentComment = "";

        while (lineReader.hasNextLine()) {
            String line = lineReader.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("&")) {
                continue;
            }

            if (line.startsWith("*")) {
                if (currentComment.isEmpty()) {
                    currentComment = line.substring(1);
                } else {
                    currentComment = currentComment + "\n" + line.substring(1);
                }
                continue;
            } else if (!currentComment.isEmpty()) {
                gameNavigation.getCurrentNode().setComment(currentComment);
                currentComment = "";
            }

            int moveNumber = gameNavigation.getPosition().getMoveCount() + 1;

            if (line.startsWith(String.valueOf(moveNumber))) {
                // Read the next move
                String[] ts = line.split("\\s+", 2);
                int i = Integer.parseInt(ts[0]);
                if (i != moveNumber || ts.length < 2) {
                    throw new IllegalArgumentException("Error after move " + moveNumber);
                }
                String move = ts[1].replaceAll(" ", "");
                curMove = KifMoveConverter.fromKifString(move, gameNavigation.getPosition(), prevMove);

                MoveTiming moveTiming = readMoveTiming(moveNumber, move);

                if (curMove instanceof SpecialMove) {
                    SpecialMove specialMove = (SpecialMove) curMove;
                    if (specialMove.getSpecialMoveType().isLosingMove()) {
                        gameRecord.setGameResult(gameNavigation.getPosition().getPlayerToMove() == Player.BLACK ?
                                GameResult.WHITE_WIN : GameResult.BLACK_WIN);
                    }
                }

                gameNavigation.addMove(curMove, moveTiming);
                prevMove = curMove;
            } else if (line.startsWith("変化")) {
                // Variation
                String[] ts = line.split("：", 2);
                if (ts.length < 2 || !ts[1].endsWith("手")) {
                    throw new IllegalArgumentException("Error reading variation line " + line);
                }
                int i = Integer.parseInt(ts[1].substring(0, ts[1].indexOf('手')));
                gameNavigation.goToNodeUSF(i - 1);
            } else if (line.startsWith("まで")) {
                // Game result: Ignore in this form (we read it as a move)
            } else {
                throw new IllegalArgumentException("Unexpected line at move " + moveNumber + ": " + line);
            }
        }

        if (!currentComment.isEmpty()) {
            gameNavigation.getCurrentNode().setComment(currentComment);
        }

        gameNavigation.moveToStart();
    }

    private MoveTiming readMoveTiming(final int moveNumber, final String move) {
        if (!move.contains("/")) {
            return null;
        }
        String time = move.substring(move.lastIndexOf('(') + 1);
        String[] split = time.split("[:/)]");
        if (split.length < 5) {
            throw new IllegalArgumentException("Error parsing time for move " + moveNumber + ": " + time);
        }
        int moveSeconds = Integer.parseInt(split[1]) + 60 * Integer.parseInt(split[0]);
        int gameSeconds =
                Integer.parseInt(split[4]) + 60 * Integer.parseInt(split[3]) + 3600 * Integer.parseInt(split[2]);

        return new MoveTiming(moveSeconds, gameSeconds);
    }

    @Override
    public String write(final GameRecord gameRecord) {
        StringBuilder builder = new StringBuilder();
        writeInformation(gameRecord, builder);
        builder.append(write(gameRecord.getGameTree()));
        return builder.toString();
    }

    private void writeInformation(final GameRecord gameRecord, final StringBuilder builder) {
        GameInformation gameInformation = gameRecord.getGameInformation();
        builder.append("# ---- PlayShogi Kif Export").append("\n");

        if (gameInformation.getDate() != null && !gameInformation.getDate().isEmpty()) {
            builder.append(START_DATE_AND_TIME).append("：").append(gameInformation.getDate()).append("\n");
        }

        if (gameInformation.getLocation() != null && !gameInformation.getLocation().isEmpty()) {
            builder.append(PLACE).append("：").append(gameInformation.getLocation()).append("\n");
        }

        if (gameInformation.getEvent() != null && !gameInformation.getEvent().isEmpty()) {
            builder.append(TOURNAMENT).append("：").append(gameInformation.getEvent()).append("\n");
        }

        if (gameInformation.getOpening() != null && !gameInformation.getOpening().isEmpty()) {
            builder.append(OPENING).append("：").append(gameInformation.getOpening()).append("\n");
        }

        if (gameInformation.getTimeControl() != null && !gameInformation.getTimeControl().isEmpty()) {
            builder.append(TIME_CONTROL).append("：").append(gameInformation.getTimeControl()).append("\n");
        }

        //TODO handicap
        builder.append("手合割：平手").append("\n");

        if (gameInformation.getBlack() != null && !gameInformation.getBlack().isEmpty()) {
            builder.append(SENTE).append("：").append(gameInformation.getBlack()).append("\n");
        }

        if (gameInformation.getWhite() != null && !gameInformation.getWhite().isEmpty()) {
            builder.append(GOTE).append("：").append(gameInformation.getWhite()).append("\n");
        }

        builder.append("手数----指手---------消費時間--").append("\n");

    }

    @Override
    public String write(final GameTree gameTree) {
        StringBuilder builder = new StringBuilder();

        Node n = gameTree.getRootNode();
        if (n.getComment().isPresent() || n.getObjects().isPresent() || n.getAdditionalTags().isPresent()) {
            // TODO
        }

        if (n.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) n.getMove();
            String sfen = SfenConverter.toSFEN(editMove.getPosition());
            if (!SfenConverter.INITIAL_POSITION_SFEN.equals(sfen)) {
                // TODO
            }
        }

        ShogiMove previousMove = null;

        int moveNumber = 1;
        while (n.hasChildren()) {
            List<Node> children = n.getChildren();
            if (children.size() > 1) {
                // TODO
            }
            n = children.get(0);
            if (n.getMove() instanceof EditMove) {
                // TODO
            } else if (n.getMove() instanceof ShogiMove) {
                ShogiMove newMove = (ShogiMove) n.getMove();
                builder.append(moveNumber).append(" ").append(KifMoveConverter.toKifString(newMove, previousMove));
                previousMove = newMove;

                if (n.getTiming() != null) {
                    builder.append("   ").append(n.getTiming().toKifString());
                }

                if (n.getComment().isPresent() || n.getObjects().isPresent() || n.getAdditionalTags().isPresent()) {
                    // TODO
                }

                builder.append("\n");
            } else {
                throw new IllegalStateException("Unknown move class: " + n);
            }
            moveNumber++;
        }
        builder.append('\n');
        return builder.toString();
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
