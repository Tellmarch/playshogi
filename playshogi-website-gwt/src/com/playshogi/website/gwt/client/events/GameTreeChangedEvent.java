package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.models.record.GameTree;

public class GameTreeChangedEvent extends GenericEvent {
	private final GameTree gameTree;

	public GameTreeChangedEvent(final GameTree gameTree) {
		this.gameTree = gameTree;
	}

	public GameTree getGameTree() {
		return gameTree;
	}

}
