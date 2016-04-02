package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.playshogi.website.gwt.client.activity.FreeBoardActivity;
import com.playshogi.website.gwt.client.activity.MainPageActivity;
import com.playshogi.website.gwt.client.activity.TsumeActivity;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.MainPageView;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class AppActivityMapper implements ActivityMapper {

	@Inject MainPageView mainPageView;
	@Inject TsumeView tsumeView;

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof MainPagePlace) {
			return new MainPageActivity(mainPageView);
		} else if (place instanceof TsumePlace) {
			return new TsumeActivity((TsumePlace) place, tsumeView);
		} else if (place instanceof FreeBoardPlace) {
			return new FreeBoardActivity((FreeBoardPlace) place);
		}
		return null;
	}

}