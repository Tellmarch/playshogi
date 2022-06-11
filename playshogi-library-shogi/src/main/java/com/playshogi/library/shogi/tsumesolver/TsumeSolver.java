package com.playshogi.library.shogi.tsumesolver;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.*;


public class TsumeSolver {

    private ShogiRulesEngine engine = new ShogiRulesEngine();

    public List<ShogiMove> getAllPossibleChecks(ShogiPosition position) {
        List<ShogiMove> allPossibleMoves = engine.getAllPossibleMoves(position);
        List<ShogiMove> allPossibleChecks = new ArrayList<>();
        for (ShogiMove move : allPossibleMoves) {
            engine.playMoveInPosition(position, move);
            if (engine.isPositionCheck(position)) {
                allPossibleChecks.add(move);
            }
            engine.undoMoveInPosition(position, move);
        }
        return allPossibleChecks;
    }

    public List<ShogiMove> getAllPossibleAnswers(ShogiPosition position) {
        List<ShogiMove> allPossibleAnswers = new ArrayList<>();
        List<ShogiMove> allPossibleMoves = engine.getAllPossibleMoves(position);
        for (ShogiMove move : allPossibleMoves) {
            engine.playMoveInPosition(position, move);
            if (!engine.isPositionCheck(position, Player.WHITE)) {
                allPossibleAnswers.add(move);
            }
            engine.undoMoveInPosition(position, move);
        }
        return allPossibleAnswers;
    }

    public int numCalls = 0;

    //Which side to move, is it correct position, is it mate
    public List<List<ShogiMove>> allMatingLines(final ShogiPosition position, final int depth) {
        numCalls++;
        if (numCalls % 1000 == 0)
            System.out.println(numCalls);
        List<List<ShogiMove>> variations = new ArrayList<>();
        // legal moves


        //base case for depth 1
        if (depth == 1) {
            List<ShogiMove> allPossibleMoves = engine.getAllPossibleMoves(position);
            //find all the variations that end up in checkmate in given position (expecting mate in 1)
            for (ShogiMove move : allPossibleMoves) {
                engine.playMoveInPosition(position, move);
                if (engine.isPositionCheckmate(position)) {
                    variations.add(Arrays.asList(move)); //save the last move
                }
                engine.undoMoveInPosition(position, move);
            }
            return variations;
        }

        //recursion for depth > 1

        List<ShogiMove> allPossibleMoves = getAllPossibleChecks(position);

        senteMoves:
        for (ShogiMove senteMove : allPossibleMoves) {
            engine.playMoveInPosition(position, senteMove);

            List<ShogiMove> allPossibleAnswers = getAllPossibleAnswers(position);
            List<List<ShogiMove>> possibleAnswerLines = new ArrayList<>();

            for (ShogiMove goteMove : allPossibleAnswers) {
                engine.playMoveInPosition(position, goteMove);

                List<List<ShogiMove>> possibleMatingLine = allMatingLines(position, depth - 2);//if all moves
                // then mate and add it
                //variations.add(move1,move2,moves from function result);
                if (possibleMatingLine.isEmpty()) { // there are no mating lines
                    engine.undoMoveInPosition(position, goteMove);
                    engine.undoMoveInPosition(position, senteMove);
                    continue senteMoves; // will skip the whole sente sequence as incorrect
                }

                possibleAnswerLines.addAll(addMoves(possibleMatingLine, senteMove, goteMove));
                engine.undoMoveInPosition(position, goteMove);
            }
            // all variations were mates
            if (possibleAnswerLines.isEmpty()) {
                variations.add(Arrays.asList(senteMove));
            } else {
                variations.addAll(possibleAnswerLines);
            }
            engine.undoMoveInPosition(position, senteMove);
        }

        return variations;
    }

    private List<List<ShogiMove>> addMoves(final List<List<ShogiMove>> possibleMatingLine, final ShogiMove senteMove,
                                           final ShogiMove goteMove) {
        List<List<ShogiMove>> result = new ArrayList<>();
        for (List<ShogiMove> matingLine : possibleMatingLine) {
            List<ShogiMove> variation = new ArrayList<>();
            variation.add(senteMove);
            variation.add(goteMove);
            variation.addAll(matingLine);
            result.add(variation);
        }
        return result;
    }

}
