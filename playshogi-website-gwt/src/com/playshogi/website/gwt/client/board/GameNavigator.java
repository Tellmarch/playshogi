package com.playshogi.website.gwt.client.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class GameNavigator extends Composite implements ShogiBoardHandler, ClickHandler {

	private final ShogiBoard shogiBoard;
	private final Button firstButton;
	private final Button previousButton;
	private final Button nextButton;
	private final Button lastButton;
	private final GameNavigation<ShogiPosition> gameNavigation;

	public GameNavigator(final ShogiBoard shogiBoard, final GameNavigation<ShogiPosition> gameNavigation) {

		this.gameNavigation = gameNavigation;
		this.shogiBoard = shogiBoard;
		firstButton = new Button("<<");
		previousButton = new Button("<");
		nextButton = new Button(">");
		lastButton = new Button(">>");

		firstButton.addClickHandler(this);
		previousButton.addClickHandler(this);
		nextButton.addClickHandler(this);
		lastButton.addClickHandler(this);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(firstButton);
		horizontalPanel.add(previousButton);
		horizontalPanel.add(nextButton);
		horizontalPanel.add(lastButton);

		initWidget(horizontalPanel);

	}

	@Override
	public void handleMovePlayed(final ShogiMove move) {
		String usfMove = UsfMoveConverter.toUsfString(move);
		GWT.log("Move played: " + usfMove);
		gameNavigation.addMove(move);
	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == firstButton) {
			gameNavigation.moveToStart();
		} else if (source == nextButton) {
			gameNavigation.moveForward();
		} else if (source == previousButton) {
			GWT.log("Moving back");
			gameNavigation.moveBack();
		} else if (source == lastButton) {
			gameNavigation.moveToEndOfVariation();
		}
		GWT.log("Displaying board");
		shogiBoard.displayPosition();
	}

}
