package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

@Singleton
public class FreeBoardView extends Composite {

	private final ShogiBoard shogiBoard;
	private final GameNavigator gameNavigator;

	@Inject
	public FreeBoardView(final EventBus eventBus) {
		GWT.log("Creating free board view");
		shogiBoard = new ShogiBoard();
		gameNavigator = new GameNavigator();
		shogiBoard.setUpperRightPanel(null);

		initWidget(shogiBoard);
	}

	public void activate(final EventBus eventBus) {
		GWT.log("Activating free board view");
		shogiBoard.activate(eventBus);
		gameNavigator.activate(eventBus);
	}

}
