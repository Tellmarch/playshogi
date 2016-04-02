package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.playshogi.website.gwt.client.gin.PlayShogiGinjector;

public class PlayShogiMain implements EntryPoint {

	@Override
	public void onModuleLoad() {
		PlayShogiGinjector injector = GWT.create(PlayShogiGinjector.class);
		injector.getApplication().start();
	}

	// private void myOldMain() {
	// ShogiBoard shogiBoard = new ShogiBoard();
	//
	// ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
	// GameNavigation<ShogiPosition> gameNavigation = new
	// GameNavigation<>(shogiRulesEngine, new GameTree(),
	// shogiBoard.getPosition());
	//
	// PositionSharing positionSharing = new PositionSharing(shogiBoard);
	// GameNavigator gameNavigator = new GameNavigator(shogiBoard,
	// gameNavigation);
	// GameImporter gameImporter = new GameImporter(shogiBoard, gameNavigation);
	// // BoardConfigurationMenu boardConfigurationMenu = new
	// // BoardConfigurationMenu(shogiBoard);
	// // RootPanel.get().add(boardConfigurationMenu);
	// // shogiBoard.setShogiBoardHandler(positionSharing);
	// shogiBoard.setShogiBoardHandler(gameNavigator);
	//
	// RootPanel.get().add(positionSharing);
	// RootPanel.get().add(gameNavigator);
	// RootPanel.get().add(shogiBoard);
	// RootPanel.get().add(gameImporter);
	// }

}
