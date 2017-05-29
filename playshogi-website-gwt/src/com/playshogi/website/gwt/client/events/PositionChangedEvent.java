package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class PositionChangedEvent extends GenericEvent {

	private final ShogiPosition position;
	private final boolean triggeredByUser;

	public PositionChangedEvent(final ShogiPosition position, final boolean triggeredByUser) {
		this.position = position;
		this.triggeredByUser = triggeredByUser;
	}

	public ShogiPosition getPosition() {
		return position;
	}

	public boolean isTriggeredByUser() {
		return triggeredByUser;
	}

}
