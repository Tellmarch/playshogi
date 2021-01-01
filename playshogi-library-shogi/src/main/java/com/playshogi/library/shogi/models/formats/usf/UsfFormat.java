package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum UsfFormat implements GameRecordFormat {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(UsfFormat.class.getName());

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
        // First, check that the file is indeed USF.
        if (!l.contains("USF:")) {
            throw (new IllegalArgumentException("Not a recognized USF File. Maybe wrong encoding?"));
        }

        // Go to the start of the next game
        while (!l.startsWith("^")) {
            l = lineReader.nextLine();
        }

        // This line contains the "preview" line

        // Reads the result
        char resultc = l.charAt(1);

        GameResult gameResult = getResult(resultc);

        // We will know start building the game tree

        ShogiPosition startingPosition;
        GameTree gameTree;
        // If the next character is ":", the game is starting from start
        // position.
        if (l.charAt(2) == ':') {
            startingPosition = ShogiInitialPositionFactory.createInitialPosition();
            gameTree = new GameTree();
        } else {
            // We read the starting position, in a SFEN that goes up to ":"
            String sfen = l.substring(2, l.indexOf(':'));
            if (SfenConverter.INITIAL_POSITION_SFEN.equals(sfen)) {
                startingPosition = ShogiInitialPositionFactory.createInitialPosition();
                gameTree = new GameTree();
            } else {
                startingPosition = SfenConverter.fromSFEN(sfen);
                gameTree = new GameTree(startingPosition);
            }
        }

        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

        // What follows is the move sequence
        String moves = l.substring(l.indexOf(':') + 1);

        playMoveSequence(gameNavigation, moves);

        GameInformation gameInformation = new GameInformation();

        // Now, we read tag lines. We read every lines until the end of the
        // file,
        // or the start of another game, identified by "^",.
        while (lineReader.hasNextLine()) {
            l = lineReader.nextLine();

            // If the line is empty, skip it
            if (l.isEmpty()) {
                continue;
            }

            if (l.charAt(0) == '^') {
                // New line
                break;
            }

            // A line starting with . changes the current node
            if (l.charAt(0) == '.') {
                String r = l.substring(1);
                // Next can be 1)Nothing 2)Move number 3)New move
                // 4)MoveNumber:new move
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
                continue;
            }

            // A line starting with # is a comment
            if (l.charAt(0) == '#') {
                // we just add the comment line to the current node's
                // comment
                gameNavigation.getCurrentNode().setComment(gameNavigation.getCurrentNode().getComment() + "\n" + l.substring(1));
                continue;
            }

            // Next, we search for header tags or node tags
            if (l.startsWith("SFEN:")) {
                // Changes the position
                String sfen = l.substring(5);
                // gameTree.addEdit(sfen);
                continue;
            }

            if (l.startsWith("BN:")) {
                String blackName = l.substring(3);
                gameInformation.setSente(blackName);
                continue;
            } else if (l.startsWith("WN:")) {
                String whiteName = l.substring(3);
                gameInformation.setGote(whiteName);
                continue;
            } else if (l.startsWith("GD:")) {
                String gameDate = l.substring(3);
                gameInformation.setDate(gameDate);
                continue;
            } else if (l.startsWith("GQ:")) {
                String gameVenue = l.substring(3);
                gameInformation.setVenue(gameVenue);
                continue;
            }

            // A line starting with ~ is an object
            if (l.charAt(0) == '~') {
                // USFObject object = new USFObject(l.substring(1));
                // gameTree.getCurrent().addObject(object);
                continue;
            }

            // A line starting with "X" is a custom tag
            if (l.charAt(0) == 'X') {
                // String tag = l.substring(1);
                // gameTree.getCurrent().addTag(l);
                continue;
            }
        }

        // TODO
        // switch (gameTree.getCurrent().getMove().getSpecialType()) {
        // case Move.SPECIAL_BREK:
        // result = Game.RESULT_BREAK;
        // break;
        // case Move.SPECIAL_JISO:
        // result = Game.RESULT_JISHOGI;
        // break;
        // case Move.SPECIAL_REPT:
        // result = Game.RESULT_SENNICHITE;
        // break;
        // case Move.SPECIAL_RSGN:
        // if (bsente) {
        // result = Game.RESULT_SENTE;
        // } else {
        // result = Game.RESULT_GOTE;
        // }
        // }

        gameNavigation.moveToStart();

        return Arrays.asList(new GameRecord(gameInformation, gameTree, gameResult));
    }

    /**
     * plays a move sequence represented by a String, with each move occupying 4
     * characters.
     */
    private static void playMoveSequence(final GameNavigation gameNavigation, final String moves) {
        // each move takes exactly 4 characters
        int numberOfMoves = moves.length() / 4;
        for (int i = 0; i < numberOfMoves; i++) {
            String move = moves.substring(4 * i, 4 * i + 4);
            LOGGER.log(Level.FINE, "Parsing move: " + move);
            Move curMove = UsfMoveConverter.fromUsfString(move, gameNavigation.getPosition());
            if (curMove == null || !move.equals(curMove.toString())) {
                LOGGER.log(Level.SEVERE, "Error parsing move: " + move + " resulted in move: " + curMove);
            }
            gameNavigation.addMove(curMove);
        }
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
            builder.append(SfenConverter.toSFEN(editMove.getPosition()));
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
