package com.playshogi.website.gwt.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.playshogi.website.gwt.client.ui.MainPageView;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class ClientFactoryImpl implements ClientFactory {
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private final MainPageView mainPageView = new MainPageView(this);
	private final TsumeView tsumeView = new TsumeView();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	public static EventBus getEventbus() {
		return eventBus;
	}

	@Override
	public MainPageView getMainPageView() {
		return mainPageView;
	}

	@Override
	public TsumeView getTsumeView() {
		return tsumeView;
	}

}
