package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.board.GameImporter;
import com.playshogi.website.gwt.client.board.GameNavigator;
import com.playshogi.website.gwt.client.board.PositionSharing;
import com.playshogi.website.gwt.client.board.ShogiBoard;

public class PlayShogiMain implements EntryPoint {

	@Override
	public void onModuleLoad() {
		ShogiBoard shogiBoard = new ShogiBoard();

		ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(),
				shogiBoard.getPosition());

		PositionSharing positionSharing = new PositionSharing(shogiBoard);
		GameNavigator gameNavigator = new GameNavigator(shogiBoard, gameNavigation);
		GameImporter gameImporter = new GameImporter(shogiBoard, gameNavigation);
		// BoardConfigurationMenu boardConfigurationMenu = new
		// BoardConfigurationMenu(shogiBoard);
		// RootPanel.get().add(boardConfigurationMenu);
		// shogiBoard.setShogiBoardHandler(positionSharing);
		shogiBoard.setShogiBoardHandler(gameNavigator);

		RootPanel.get().add(positionSharing);
		RootPanel.get().add(gameNavigator);
		RootPanel.get().add(shogiBoard);
		RootPanel.get().add(gameImporter);
	}

}
