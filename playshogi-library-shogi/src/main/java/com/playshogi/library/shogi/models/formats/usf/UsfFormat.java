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
import java.util.Optional;

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
        StringBuilder builder = new StringBuilder("USF:1.0\n");

        boolean needNodes = writePreviewString(gameRecord, builder);
        writeGameTags(gameRecord, builder);
        if (needNodes) {
            writeNodes(gameRecord, builder);
        }

        return builder.toString();
    }

    private static void writeGameTags(final GameRecord gameRecord, final StringBuilder builder) {
        GameInformation gameInformation = gameRecord.getGameInformation();
        if (gameInformation != null) {
            String sente = gameInformation.getBlack();
            if (sente != null && !sente.isEmpty()) {
                builder.append("BN:").append(sente).append('\n');
            }

            String gote = gameInformation.getWhite();
            if (gote != null && !gote.isEmpty()) {
                builder.append("WN:").append(gote).append('\n');
            }

            String date = gameInformation.getDate();
            if (date != null && !date.isEmpty()) {
                builder.append("GD:").append(date).append('\n');
            }

            String event = gameInformation.getEvent();
            if (event != null && !event.isEmpty()) {
                builder.append("GN:").append(event).append('\n');
            }

            String location = gameInformation.getLocation();
            if (location != null && !location.isEmpty()) {
                builder.append("GQ:").append(location).append('\n');
            }
        }
    }

    public static String writePreviewString(final GameRecord gameRecord) {
        StringBuilder builder = new StringBuilder();
        writePreviewString(gameRecord, builder);
        return builder.toString();
    }

    private static boolean writePreviewString(final GameRecord gameRecord, final StringBuilder builder) {
        boolean needNodesSection = false;
        GameTree gameTree = gameRecord.getGameTree();
        builder.append("^");
        builder.append(getResultChar(gameRecord.getGameResult()));
        Node n = gameTree.getRootNode();
        if (n.getComment().isPresent() || n.getObjects().isPresent()) {
            needNodesSection = true;
        }
        if (n.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) n.getMove();
            String sfen = SfenConverter.toSFEN(editMove.getPosition());
            if (!SfenConverter.INITIAL_POSITION_SFEN.equals(sfen)) {
                builder.append(sfen);
            }
        }
        builder.append(":");
        while (n.hasChildren()) {
            List<Node> children = n.getChildren();
            if (children.size() > 1) {
                needNodesSection = true;
            }
            n = children.get(0);
            builder.append(UsfMoveConverter.toUsfString((ShogiMove) n.getMove()));
            if (n.getComment().isPresent() || n.getObjects().isPresent()) {
                needNodesSection = true;
            }
        }
        builder.append('\n');
        return needNodesSection;
    }

    private static void writeNodes(final GameRecord gameRecord, final StringBuilder builder) {
        Node node = gameRecord.getGameTree().getRootNode();

        int nodeCount = 0;
        builder.append(".0\n");
        writeNodeTags(node, builder);

        while (node.hasChildren()) {
            node = node.getChildren().get(0);
            builder.append(".\n");
            writeNodeTags(node, builder);
            nodeCount++;
        }

        while (true) {

            Node next = getNextSibling(node);
            while (node.getParent() != null && next == null) {
                nodeCount--;
                node = node.getParent();
                next = getNextSibling(node);
            }

            if (next == null) {
                break;
            }

            builder.append('.');
            builder.append(nodeCount - 1); // We go back to the parent node to insert the new sibling
            builder.append('\n');

            node = next;

            builder.append('.');
            builder.append(node.getMove().toString());
            builder.append('\n');
            writeNodeTags(node, builder);


            while (node.hasChildren()) {
                node = node.getChildren().get(0);
                builder.append('.');
                builder.append(node.getMove().toString());
                builder.append('\n');
                writeNodeTags(node, builder);
                nodeCount++;
            }
        }

    }

    private static Node getNextSibling(final Node node) {
        if (node.getParent() == null) {
            return null;
        }
        List<Node> siblings = node.getParent().getChildren();
        if (node.getParentIndex() < siblings.size() - 1) {
            return siblings.get(node.getParentIndex() + 1);
        } else {
            return null;
        }
    }

    private static void writeNodeTags(final Node node, final StringBuilder builder) {
        Optional<String> comment = node.getComment();
        if (comment.isPresent()) {
            String[] lines = comment.get().split("\n");
            for (String line : lines) {
                builder.append('#');
                builder.append(line);
                builder.append('\n');
            }
        }
        Optional<String> objects = node.getObjects();
        if (objects.isPresent()) {
            String[] lines = objects.get().split("\n");
            for (String line : lines) {
                builder.append('~');
                builder.append(line);
                builder.append('\n');
            }
        }
        Optional<String> additionalTags = node.getAdditionalTags();
        if (additionalTags.isPresent()) {
            String[] lines = additionalTags.get().split("\n");
            for (String line : lines) {
                builder.append(line);
                builder.append('\n');
            }
        }
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
}
