package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.playshogi.website.gwt.client.board.PositionSharing;
import com.playshogi.website.gwt.client.board.ShogiBoard;

public class PlayShogiMain implements EntryPoint {

	@Override
	public void onModuleLoad() {
		ShogiBoard shogiBoard = new ShogiBoard();
		PositionSharing positionSharing = new PositionSharing(shogiBoard);
		// BoardConfigurationMenu boardConfigurationMenu = new
		// BoardConfigurationMenu(shogiBoard);
		// RootPanel.get().add(boardConfigurationMenu);
		shogiBoard.setShogiBoardHandler(positionSharing);
		RootPanel.get().add(positionSharing);
		RootPanel.get().add(shogiBoard);
	}

}
