package com.playshogi.website.gwt.client.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class TsumeView extends Composite {

	@Inject
	public TsumeView(final ShogiBoard shogiBoard, final EventBus eventBus) {
		shogiBoard.getBoardConfiguration().setShowGoteKomadai(false);
		shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

		GameNavigator gameNavigator = new GameNavigator(eventBus, shogiBoard.getBoardConfiguration());

		ProblemFeedbackPanel problemFeedbackPanel = new ProblemFeedbackPanel(eventBus, gameNavigator);
		shogiBoard.setUpperRightPanel(problemFeedbackPanel);

		initWidget(shogiBoard);
	}

}
