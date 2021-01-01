package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.record.*;

import java.util.ArrayList;
import java.util.List;

public enum UsfFormat implements GameRecordFormat {
    INSTANCE;

    public GameRecord readSingle(final String string) {
        List<GameRecord> gameRecords = read(string);
        if (gameRecords.size() != 1) {
            throw new IllegalStateException("Multiple or no game record, size=" + gameRecords.size());
        }
        return gameRecords.get(0);
    }

    @Override
    public List<GameRecord> read(final String string) {
        return read(new StringLineReader(string));
    }

    @Override
    public List<GameRecord> read(final LineReader lineReader) {
        String l = lineReader.nextLine();
        if (!l.contains("USF:")) {
            throw (new IllegalArgumentException("Not a recognized USF File. Maybe wrong encoding?"));
        }

        ArrayList<GameRecord> games = new ArrayList<>(1);

        goToNextGame(lineReader);
        while (isAtNextGame(lineReader)) {
            games.add(new UsfGameParser(lineReader).readGameRecord());
        }
        return games;
    }

    private void goToNextGame(final LineReader lineReader) {
        while (!isAtNextGame(lineReader)) {
            lineReader.nextLine();
        }
    }

    private boolean isAtNextGame(final LineReader lineReader) {
        return lineReader.hasNextLine() &&
                !lineReader.peekNextLine().isEmpty() &&
                lineReader.peekNextLine().charAt(0) == '^';
    }

    @Override
    public String write(final GameRecord gameRecord) {
        return toUSFString(gameRecord);
    }

    @Override
    public String write(final GameTree gameTree) {
        return write(new GameRecord(null, gameTree, null));
    }

    private static String toUSFString(final GameRecord gameRecord) {
        GameTree gameTree = gameRecord.getGameTree();
        GameInformation gameInformation = gameRecord.getGameInformation();

        StringBuilder builder = new StringBuilder("USF:1.0\n");
        builder.append("^");
        builder.append(getResultChar(gameRecord.getGameResult()));
        Node n = gameTree.getRootNode();
        if (n.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) n.getMove();
            String sfen = SfenConverter.toSFEN(editMove.getPosition());
            if (!SfenConverter.INITIAL_POSITION_SFEN.equals(sfen)) {
                builder.append(sfen);
            }
        }
        builder.append(":");
        while (n.hasChildren()) {
            n = n.getChildren().get(0);
            builder.append(UsfMoveConverter.toUsfString((ShogiMove) n.getMove()));
        }
        if (gameInformation != null) {
            String sente = gameInformation.getSente();
            if (sente != null && !sente.isEmpty()) {
                builder.append("\nBN:").append(sente);
            }

            String gote = gameInformation.getGote();
            if (gote != null && !gote.isEmpty()) {
                builder.append("\nWN:").append(gote);
            }

            String date = gameInformation.getDate();
            if (date != null && !date.isEmpty()) {
                builder.append("\nGD:").append(date);
            }

            String venue = gameInformation.getVenue();
            if (venue != null && !venue.isEmpty()) {
                builder.append("\nGQ:").append(venue);
            }

        }
        return builder.toString();
    }

    private static char getResultChar(final GameResult gameResult) {
        if (gameResult == null) {
            return '*';
        }
        switch (gameResult) {
            case BLACK_WIN:
                return 'b';
            case WHITE_WIN:
                return 'w';
            case OTHER:
                return 'd';
            case UNKNOWN:
                return '*';
            default:
                throw (new IllegalArgumentException("Unknown result type: " + gameResult));
        }
    }

//    /**
//     * Gives the USF string representing the whole tree.
//     *
//     * @return
//     */
    // public String toUSFString(final GameTree gameTree) {
    // String endline = System.getProperty("line.separator");
    // String res = "^*:";
    // List<Integer> var = mainLine();
    // // First, preview string (main line)
    // res += varToUSFString(var) + endline;
    //
    // // Then, for every variation
    // while (true) {
    // List<Integer> var2 = nextVariation(var);
    // if (var2 == null) {
    // // no more variation? end of loop
    // break;
    // }
    // // We write the USF string of this variation
    // res += varToUSFString(var2, firstDiff(var, var2)) + endline;
    // var = var2;
    // }
    // return res;
    // }

}
