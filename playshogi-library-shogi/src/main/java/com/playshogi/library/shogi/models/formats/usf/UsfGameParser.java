package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.util.LineReader;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads one single game from an USF file
 * Not reusable - create a new instance for each game to parse (mostly because it is not thread-safe)
 */
class UsfGameParser {

    private static final Logger LOGGER = Logger.getLogger(UsfGameParser.class.getName());

    private final LineReader lineReader;
    private GameInformation gameInformation;
    private GameResult gameResult;
    private GameTree gameTree;
    private GameNavigation gameNavigation;

    UsfGameParser(final LineReader lineReader) {
        this.lineReader = lineReader;
    }

    // When we call this method, the next line should start with ^ (The game preview tag)
    GameRecord readGameRecord() {
        gameInformation = new GameInformation();

        readPreviewLine();
        readGameTags();

        while (isAtNextNode()) {
            readNode();
        }

        gameNavigation.moveToStart();

        return new GameRecord(gameInformation, gameTree, gameResult);
    }

    private void readGameTags() {
        while (lineReader.hasNextLine() && !isAtNextGame() && !isAtNextNode()) {
            readGameTag();
        }
    }

    private void readNode() {
        readDotLine(lineReader.nextLine());
        while (lineReader.hasNextLine() && !isAtNextGame() && !isAtNextNode()) {
            readNodeTag();
        }
    }

    private void readGameTag() {
        String line = lineReader.nextLine();
        if (line.isEmpty()) {
            return;
        }

        if (line.startsWith("BN:")) {
            String blackName = line.substring(3);
            gameInformation.setBlack(blackName);
        } else if (line.startsWith("WN:")) {
            String whiteName = line.substring(3);
            gameInformation.setWhite(whiteName);
        } else if (line.startsWith("GD:")) {
            String gameDate = line.substring(3);
            gameInformation.setDate(gameDate);
        } else if (line.startsWith("GN:")) {
            String gameEvent = line.substring(3);
            gameInformation.setEvent(gameEvent);
        } else if (line.startsWith("GQ:")) {
            String gameSource = line.substring(3);
            gameInformation.setLocation(gameSource);
        } else {
            System.out.println("Ignoring game tag: " + line);
        }
    }


    private void readNodeTag() {
        String line = lineReader.nextLine();
        if (line.isEmpty()) {
            return;
        }

        // A line starting with # is a comment
        if (line.charAt(0) == '#') {
            readComment(line);
        }

        if (line.startsWith("SFEN:")) {
            readSfenTag(line);
        }

        // A line starting with ~ is an object
        if (line.charAt(0) == '~') {
            readObject(line);
        }

        // A line starting with "X" is a custom tag
        if (line.charAt(0) == 'X') {
            readCustomTags(line);
        }
    }

    private void readSfenTag(final String line) {
        // Changes the position
        String sfen = line.substring(5);
        // gameTree.addEdit(sfen);
    }

    private void readComment(final String line) {
        // we just add the comment line to the current node's comment
        Optional<String> comment = gameNavigation.getCurrentNode().getComment();
        if (comment.isPresent()) {
            gameNavigation.getCurrentNode().setComment(comment.get() + "\n" + line.substring(1));
        } else {
            gameNavigation.getCurrentNode().setComment(line.substring(1));
        }
    }

    private void readObject(final String line) {
        Optional<String> objects = gameNavigation.getCurrentNode().getObjects();
        if (objects.isPresent()) {
            gameNavigation.getCurrentNode().setObjects(objects.get() + "\n" + line.substring(1));
        } else {
            gameNavigation.getCurrentNode().setObjects(line.substring(1));
        }
    }

    private void readCustomTags(final String line) {
        Optional<String> tags = gameNavigation.getCurrentNode().getAdditionalTags();
        if (tags.isPresent()) {
            gameNavigation.getCurrentNode().setAdditionalTags(tags.get() + "\n" + line);
        } else {
            gameNavigation.getCurrentNode().setAdditionalTags(line);
        }
    }

    private void readDotLine(final String l) {
        if (!(l.charAt(0) == '.')) {
            throw new IllegalArgumentException("New USF node should start with . - actual " + l);
        }

        String r = l.substring(1);
        // Next can be 1)Nothing 2)Move number 3)New move 4)MoveNumber:new move
        if (r.isEmpty()) {
            // Empty : we just go to the next move
            gameNavigation.moveForwardInLastVariation();
        } else {
            String[] rs = r.split(":");
            if (rs.length == 1) {
                // No :, case 2 or 3.
                // We try to read a move number
                try {
                    int mn = Integer.parseInt(r);
                    // We found a move number, we go there
                    gameNavigation.goToNodeUSF(mn);
                } catch (NumberFormatException e) {
                    // If it wasn't a number, it should be a move
                    // sequence
                    playMoveSequence(gameNavigation, r);
                }
            } else if (rs.length == 2) {
                // it should be a move number + a move sequence
                int mn = Integer.parseInt(rs[0]);
                // We found a move number, we go there
                gameNavigation.goToNodeUSF(mn);
                // next should be a move sequence
                playMoveSequence(gameNavigation, rs[1]);
            } else {
                // More that one ":" in one line? shouldn't happen
                throw (new IllegalArgumentException("Error parsing the USF File (in the line " + l + " )"));
            }

        }
    }

    private void readPreviewLine() {
        String preview = lineReader.nextLine();

        if (!(preview.charAt(0) == '^')) {
            throw new IllegalArgumentException("New USF game should start with ^ - actual " + preview);
        }

        //The preview String contains the game result, initial position, and main line moves.
        gameResult = getResult(preview.charAt(1));
        gameTree = createEmptyGameTreeWithStartingPosition(preview);

        gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

        // What follows is the move sequence
        String previewMoves = preview.substring(preview.indexOf(':') + 1);
        playMoveSequence(gameNavigation, previewMoves);

        //TODO refine result based on last move
    }

    private GameTree createEmptyGameTreeWithStartingPosition(final String preview) {
        GameTree gameTree;
        // If the next character is ":", the game is starting from the default starting position.

        if (preview.charAt(2) == ':') {
            gameTree = new GameTree();
        } else {
            // We read the starting position, in a SFEN that goes up to ":"
            String sfen = preview.substring(2, preview.indexOf(':'));
            if (SfenConverter.INITIAL_POSITION_SFEN.equals(sfen)) {
                gameTree = new GameTree();
            } else {
                gameTree = new GameTree(SfenConverter.fromSFEN(sfen));
            }
        }
        return gameTree;
    }

    /**
     * plays a move sequence represented by a String, with each move occupying 4 characters.
     */
    private static void playMoveSequence(final GameNavigation gameNavigation, final String moves) {
        // each move takes exactly 4 characters
        int numberOfMoves = moves.length() / 4;
        for (int i = 0; i < numberOfMoves; i++) {
            String move = moves.substring(4 * i, 4 * i + 4);
            Move curMove = UsfMoveConverter.fromUsfString(move, gameNavigation.getPosition());
            if (curMove == null || !move.equals(curMove.toString())) {
                LOGGER.log(Level.SEVERE, "Error parsing move: " + move + " resulted in move: " + curMove);
            }
            gameNavigation.addMove(curMove);
        }
    }

    private GameResult getResult(final char resultc) {
        GameResult gameResult = GameResult.UNKNOWN;

        switch (resultc) {
            case 's':
            case 'b':
                gameResult = GameResult.BLACK_WIN;
                break;
            case 'g':
            case 'w':
                gameResult = GameResult.WHITE_WIN;
                break;
            case 'd':
                gameResult = GameResult.OTHER;
                break;
            case '*':
                break;
            default:
                throw (new IllegalArgumentException("Error parsing the USF File (not a valid result)"));
        }
        return gameResult;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAtNextGame() {
        return lineReader.hasNextLine() &&
                !lineReader.peekNextLine().isEmpty() &&
                lineReader.peekNextLine().charAt(0) == '^';
    }

    private boolean isAtNextNode() {
        return lineReader.hasNextLine() &&
                !lineReader.peekNextLine().isEmpty() &&
                lineReader.peekNextLine().charAt(0) == '.';
    }
}
