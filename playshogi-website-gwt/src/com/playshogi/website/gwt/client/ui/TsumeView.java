package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.services.ProblemsService;
import com.playshogi.website.gwt.client.services.ProblemsServiceAsync;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

public class TsumeView extends Composite {

	private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
	private final GameNavigation<ShogiPosition> gameNavigation;
	private final ShogiBoard shogiBoard;
	private final ClientFactory clientFactory;

	public TsumeView(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		shogiBoard = new ShogiBoard(clientFactory.getEventBus());
		shogiBoard.getBoardConfiguration().setShowGoteKomadai(false);

		ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
		gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(), shogiBoard.getPosition());

		GameNavigator gameNavigator = new GameNavigator(clientFactory.getEventBus(), gameNavigation);

		ProblemFeedbackPanel problemFeedbackPanel = new ProblemFeedbackPanel(clientFactory.getEventBus(),
				gameNavigator);
		shogiBoard.setUpperRightPanel(problemFeedbackPanel);

		initWidget(shogiBoard);
	}

	public void setTsumeId(final String tsumeId) {
		if (tsumeId == null || tsumeId.equalsIgnoreCase("null")) {
			int number = Random.nextInt(800) + 100;
			requestTsume(String.valueOf(number));
		} else {
			requestTsume(tsumeId);
		}
	}

	private void requestTsume(final String tsumeId) {
		problemsService.getProblemUsf(tsumeId, getProblemRequestCallback(tsumeId));
	}

	private AsyncCallback<String> getProblemRequestCallback(final String tsumeId) {
		return new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String resultUsf) {
				if (resultUsf == null) {
					GWT.log("Got null usf from server for problem request: " + tsumeId);
				} else {
					GWT.log("Got usf from server for problem request: " + tsumeId + " : " + resultUsf);
					GameRecord gameRecord = UsfFormat.INSTANCE.read(resultUsf);
					GWT.log("Updating game navigator...");
					gameNavigation.setGameTree(gameRecord.getGameTree());
					shogiBoard.setPosition(gameNavigation.getPosition());
				}
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Remote called failed for problem request: " + tsumeId);
			}
		};
	}
}
