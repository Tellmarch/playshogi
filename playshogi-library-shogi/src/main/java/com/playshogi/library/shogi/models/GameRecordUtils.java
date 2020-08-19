package com.playshogi.library.shogi.models;

import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Iterator;

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
