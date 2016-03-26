package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.playshogi.website.gwt.client.ClientFactory;

public class AppActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	public AppActivityMapper(final ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof MainPagePlace)
			return new MainPageActivity((MainPagePlace) place, clientFactory);
		else if (place instanceof GoodbyePlace)
			return new TsumeActivity((GoodbyePlace) place, clientFactory);

		return null;
	}

}
