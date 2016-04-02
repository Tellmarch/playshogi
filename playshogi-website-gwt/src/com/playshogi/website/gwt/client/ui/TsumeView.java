package com.playshogi.website.gwt.client.ui;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class TsumeView extends Composite {

	@Inject
	public TsumeView(final ShogiBoard shogiBoard, final EventBus eventBus) {
		shogiBoard.getBoardConfiguration().setShowGoteKomadai(false);
		shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

		ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(),
				shogiBoard.getPosition());

		GameNavigator gameNavigator = new GameNavigator(eventBus, gameNavigation, shogiBoard.getBoardConfiguration());

		ProblemFeedbackPanel problemFeedbackPanel = new ProblemFeedbackPanel(eventBus, gameNavigator);
		shogiBoard.setUpperRightPanel(problemFeedbackPanel);

		initWidget(shogiBoard);
	}

}
