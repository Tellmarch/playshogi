package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorPanel;

@Singleton
public class NewKifuView extends Composite {

	private static final String NEWKIFU = "newkifu";
	private final ShogiBoard shogiBoard;
	private final GameNavigator gameNavigator;
	private final KifuEditorPanel kifuEditorPanel;

	@Inject
	public NewKifuView() {
		GWT.log("Creating new kifu view");
		shogiBoard = new ShogiBoard(NEWKIFU);
		gameNavigator = new GameNavigator(NEWKIFU);

		kifuEditorPanel = new KifuEditorPanel(gameNavigator);

		shogiBoard.setUpperRightPanel(kifuEditorPanel);

		initWidget(shogiBoard);
	}

	public void activate(final EventBus eventBus) {
		GWT.log("Activating new kifu view");
		shogiBoard.activate(eventBus);
		gameNavigator.activate(eventBus);
		kifuEditorPanel.activate(eventBus);
	}

}
