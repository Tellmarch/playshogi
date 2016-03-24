package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.Event;
import com.playshogi.library.shogi.models.moves.ShogiMove;

public class MovePlayedEvent extends Event<MovePlayedEventHandler> {
	private final ShogiMove move;

	public MovePlayedEvent(final ShogiMove move) {
		this.move = move;
	}

	public ShogiMove getMove() {
		return move;
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<MovePlayedEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void dispatch(final MovePlayedEventHandler handler) {
		// TODO Auto-generated method stub

	}

}
