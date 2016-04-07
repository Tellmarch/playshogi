package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;
import com.playshogi.website.gwt.client.services.PositionSharingService;
import com.playshogi.website.gwt.client.services.PositionSharingServiceAsync;

public class PositionSharing extends Composite {

	interface MyEventBinder extends EventBinder<PositionSharing> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final PositionSharingServiceAsync positionSharingService = GWT.create(PositionSharingService.class);
	private final ShogiBoard shogiBoard;
	private final TextBox keyField;

	private final EventBus eventBus;

	public PositionSharing(final EventBus eventBus, final ShogiBoard shogiBoard) {
		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, this.eventBus);

		this.shogiBoard = shogiBoard;
		final Button shareButton = new Button("Share");
		final Button loadButton = new Button("Load");
		final Button playSenteButton = new Button("Play as Sente");
		final Button playGoteButton = new Button("Play as Gote");
		final Button watchButton = new Button("Watch");
		keyField = new TextBox();
		keyField.setText("MyBoard");

		shareButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				positionSharingService.sharePosition(SfenConverter.toSFEN(shogiBoard.getPosition()), keyField.getText(),
						getVoidCallback("share"));
			}

		});

		loadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				positionSharingService.getPosition(keyField.getText(), new AsyncCallback<String>() {

					@Override
					public void onFailure(final Throwable caught) {
						GWT.log("load failure");
					}

					@Override
					public void onSuccess(final String result) {
						GWT.log("load success");
						ShogiPosition positionFromServer = SfenConverter.fromSFEN(result);
						if (positionFromServer != null) {
							shogiBoard.setPosition(positionFromServer);
						}
					}
				});
			}
		});

		playSenteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				shogiBoard.setPlaySenteMoves(true);
				shogiBoard.setPlayGoteMoves(false);
			}
		});

		playGoteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				shogiBoard.setPlaySenteMoves(false);
				shogiBoard.setPlayGoteMoves(true);
				waitForNextMove();
			}
		});

		watchButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				shogiBoard.setPlaySenteMoves(false);
				shogiBoard.setPlayGoteMoves(false);
				waitForNextMove();
			}
		});

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(keyField);
		horizontalPanel.add(shareButton);
		horizontalPanel.add(loadButton);
		horizontalPanel.add(playSenteButton);
		horizontalPanel.add(playGoteButton);
		horizontalPanel.add(watchButton);

		initWidget(horizontalPanel);

	}

	@EventHandler
	public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
		ShogiMove move = movePlayedEvent.getMove();
		String usfMove = UsfMoveConverter.toUsfString(move);
		GWT.log("Sending move: " + usfMove);
		positionSharingService.playMove(getKey(), usfMove, getVoidCallback("playMove"));

		if (!shogiBoard.canPlayMove()) {
			waitForNextMove();
		}
	}

	private void waitForNextMove() {
		positionSharingService.getNextMove(getKey(), new AsyncCallback<String>() {

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("getNextMove failure");
			}

			@Override
			public void onSuccess(final String move) {
				GWT.log("Received move: " + move);
				ShogiMove shogiMove = UsfMoveConverter.fromUsfString(move, shogiBoard.getPosition());

				// shogiBoard.playMove(shogiMove, false);

				if (!shogiBoard.canPlayMove()) {
					waitForNextMove();
				}
			}
		});
	}

	private String getKey() {
		return keyField.getText();
	}

	private AsyncCallback<Void> getVoidCallback(final String method) {
		return new AsyncCallback<Void>() {

			@Override
			public void onSuccess(final Void result) {
				GWT.log(method + " success");
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log(method + " failure");
			}
		};
	}
}
