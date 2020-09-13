package com.playshogi.library.shogi.models;

import com.playshogi.library.models.EditMove;
import com.playshogi.library.models.Move;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.models.record.Node;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameRecordUtils {

    public static void print(final GameRecord gameRecord) {
        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameRecord.getGameTree(),
                ShogiInitialPositionFactory.createInitialPosition());

        System.out.println(gameNavigation.getPosition().toString());
        while (gameNavigation.canMoveForward()) {
            gameNavigation.moveForward();
            System.out.println(gameNavigation.getPosition().toString());
        }
    }

    public static List<Move> getMainVariationMoves(final GameRecord gameRecord) {
        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameRecord.getGameTree(),
                ShogiInitialPositionFactory.createInitialPosition());
        ArrayList<Move> moves = new ArrayList<>();
        while (gameNavigation.canMoveForward()) {
            moves.add(gameNavigation.getMainVariationMove());
            gameNavigation.moveForward();
        }
        return moves;
    }

    public static ShogiPosition getinitialPosition(final GameRecord gameRecord) {
        return getinitialPosition(gameRecord.getGameTree());
    }

    public static ShogiPosition getinitialPosition(final GameTree gameTree) {
        Node rootNode = gameTree.getRootNode();
        if (rootNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) rootNode.getMove();
            return (ShogiPosition) editMove.getPosition();
        } else {
            return ShogiInitialPositionFactory.createInitialPosition();
        }
    }

    public static Iterable<ShogiPosition> getMainVariation(final GameRecord gameRecord) {

        return () -> new Iterator<ShogiPosition>() {

            GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                    gameRecord.getGameTree(),
                    ShogiInitialPositionFactory.createInitialPosition());

            boolean first = true;

            @Override
            public boolean hasNext() {
                if (first) {
                    return true;
                } else {
                    return gameNavigation.canMoveForward();
                }
            }

            @Override
            public ShogiPosition next() {
                if (first) {
                    first = false;
                    return gameNavigation.getPosition();
                } else {
                    gameNavigation.moveForward();
                    return gameNavigation.getPosition();
                }
            }
        };

    }
}
