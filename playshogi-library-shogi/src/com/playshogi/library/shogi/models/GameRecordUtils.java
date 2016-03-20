package com.playshogi.library.shogi.models;

import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public class GameRecordUtils {

	public static void print(final GameRecord gameRecord) {
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
				gameRecord.getGameTree(), new ShogiInitialPositionFactory().createInitialPosition());

		System.out.println(gameNavigation.getPosition().toString());
		while (gameNavigation.canMoveForward()) {
			gameNavigation.moveForward();
			System.out.println(gameNavigation.getPosition().toString());
		}
	}
}
