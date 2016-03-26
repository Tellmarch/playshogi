package com.playshogi.website.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class TsumeView extends Composite {

	public TsumeView() {
		ShogiBoard shogiBoard = new ShogiBoard();

		ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(),
				shogiBoard.getPosition());

		GameNavigator gameNavigator = new GameNavigator(shogiBoard, gameNavigation);
		shogiBoard.setShogiBoardHandler(gameNavigator);

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(gameNavigator);
		verticalPanel.add(shogiBoard);
		initWidget(verticalPanel);
	}

	public void setTsumeId(final String tsumeId) {
		// TODO Auto-generated method stub

	}
}
