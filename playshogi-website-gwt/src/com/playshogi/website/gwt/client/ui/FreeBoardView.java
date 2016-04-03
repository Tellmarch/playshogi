package com.playshogi.website.gwt.client.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

@Singleton
public class FreeBoardView extends Composite {

	@Inject
	public FreeBoardView(final ShogiBoard shogiBoard, final GameNavigator gameNavigator, final EventBus eventBus) {
		shogiBoard.getBoardConfiguration().setShowGoteKomadai(true);
		shogiBoard.getBoardConfiguration().setPlayGoteMoves(true);
		shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);

		gameNavigator.getNavigatorConfiguration().setProblemMode(false);

		shogiBoard.setUpperRightPanel(null);

		initWidget(shogiBoard);
	}

}
