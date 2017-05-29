package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.openings.PositionStatisticsPanel;

@Singleton
public class OpeningsView extends Composite {

	private static final String OPENINGS = "openings";
	private final ShogiBoard shogiBoard;
	private final GameNavigator gameNavigator;
	private final PositionStatisticsPanel positionStatisticsPanel;

	@Inject
	public OpeningsView(final AppPlaceHistoryMapper historyMapper) {
		GWT.log("Creating openings view");
		shogiBoard = new ShogiBoard(OPENINGS);
		gameNavigator = new GameNavigator(OPENINGS);

		positionStatisticsPanel = new PositionStatisticsPanel(historyMapper);

		shogiBoard.setUpperRightPanel(positionStatisticsPanel);

		initWidget(shogiBoard);
	}

	public void activate(final ShogiPosition position, final EventBus eventBus) {
		GWT.log("Activating openings view");
		shogiBoard.activate(eventBus);
		gameNavigator.getGameNavigation().setGameTree(new GameTree(position));
		gameNavigator.activate(eventBus);
		positionStatisticsPanel.activate(eventBus);
	}

}
