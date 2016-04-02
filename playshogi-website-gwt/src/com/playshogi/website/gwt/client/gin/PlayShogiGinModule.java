package com.playshogi.website.gwt.client.gin;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.mvp.AppActivityMapper;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class PlayShogiGinModule extends AbstractGinModule {

	private final Place defaultPlace = new MainPagePlace();

	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(ShogiBoard.class).in(Singleton.class);
		bind(ActivityMapper.class).to(AppActivityMapper.class).in(Singleton.class);
	}

	@Singleton
	@Provides
	PlaceController providePlaceController(final EventBus eventBus) {
		return new PlaceController(eventBus);
	}

	@Singleton
	@Provides
	ActivityManager provideActivityManager(final ActivityMapper mapper, final EventBus eventBus) {
		return new ActivityManager(mapper, eventBus);
	}

	@Singleton
	@Provides
	PlaceHistoryHandler providePlaceHistoryHandler(final AppPlaceHistoryMapper historyMapper, final EventBus eventBus,
			final PlaceController placeController) {
		PlaceHistoryHandler placeHistoryHandler = new PlaceHistoryHandler(historyMapper);
		placeHistoryHandler.register(placeController, eventBus, defaultPlace);
		return placeHistoryHandler;
	}
}
