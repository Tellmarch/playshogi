package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class PositionChangedEvent extends GenericEvent {

	private final ShogiPosition position;

	public PositionChangedEvent(final ShogiPosition position) {
		this.position = position;
	}

	public ShogiPosition getPosition() {
		return position;
	}

}
