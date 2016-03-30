package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.activity.FreeBoardActivity;
import com.playshogi.website.gwt.client.activity.MainPageActivity;
import com.playshogi.website.gwt.client.activity.TsumeActivity;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.place.TsumePlace;

public class AppActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	public AppActivityMapper(final ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof MainPagePlace) {
			return new MainPageActivity(clientFactory);
		} else if (place instanceof TsumePlace) {
			return new TsumeActivity((TsumePlace) place, clientFactory);
		} else if (place instanceof FreeBoardPlace) {
			return new FreeBoardActivity((FreeBoardPlace) place, clientFactory);
		}
		return null;
	}

}
