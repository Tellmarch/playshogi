package com.playshogi.website.gwt.client.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class TsumeView extends Composite {

	@Inject
	public TsumeView(final ShogiBoard shogiBoard, final GameNavigator gameNavigator, final EventBus eventBus) {
		shogiBoard.getBoardConfiguration().setShowGoteKomadai(false);
		shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

		gameNavigator.getNavigatorConfiguration().setProblemMode(true);

		ProblemFeedbackPanel problemFeedbackPanel = new ProblemFeedbackPanel(eventBus, gameNavigator);
		shogiBoard.setUpperRightPanel(problemFeedbackPanel);

		initWidget(shogiBoard);
	}

}
