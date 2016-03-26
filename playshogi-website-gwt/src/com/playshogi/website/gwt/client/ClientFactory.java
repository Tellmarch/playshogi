package com.playshogi.website.gwt.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.playshogi.website.gwt.client.ui.MainPageView;

public interface ClientFactory {
	EventBus getEventBus();

	PlaceController getPlaceController();

	MainPageView getMainPageView();
}
