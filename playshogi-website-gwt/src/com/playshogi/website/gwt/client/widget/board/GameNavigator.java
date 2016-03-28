package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;

public class GameNavigator extends Composite implements ClickHandler {

	interface MyEventBinder extends EventBinder<GameNavigator> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final Button firstButton;
	private final Button previousButton;
	private final Button nextButton;
	private final Button lastButton;
	private final GameNavigation<ShogiPosition> gameNavigation;

	private final EventBus eventBus;

	public GameNavigator(final EventBus eventBus, final GameNavigation<ShogiPosition> gameNavigation) {

		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, this.eventBus);
		this.gameNavigation = gameNavigation;
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

	@EventHandler
	public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
		ShogiMove move = movePlayedEvent.getMove();
		String usfMove = UsfMoveConverter.toUsfString(move);
		GWT.log("Move played: " + usfMove);
		boolean existingMove = gameNavigation.hasMoveInCurrentPosition(move);
		gameNavigation.addMove(move);
		firePositionChanged();
		if (!existingMove) {
			eventBus.fireEvent(new NewVariationPlayedEvent());
		}
		if (!gameNavigation.canMoveForward()) {
			eventBus.fireEvent(new EndOfVariationReachedEvent());
		}
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
		firePositionChanged();
	}

	private void firePositionChanged() {
		eventBus.fireEvent(new PositionChangedEvent());
	}

}
