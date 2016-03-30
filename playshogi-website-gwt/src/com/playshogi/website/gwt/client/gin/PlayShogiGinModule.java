package com.playshogi.website.gwt.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class PlayShogiGinModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(ShogiBoard.class).in(Singleton.class);
	}

}
