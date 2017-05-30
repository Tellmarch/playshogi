package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.activity.FreeBoardActivity;
import com.playshogi.website.gwt.client.activity.LoginActivity;
import com.playshogi.website.gwt.client.activity.MainPageActivity;
import com.playshogi.website.gwt.client.activity.MyGamesActivity;
import com.playshogi.website.gwt.client.activity.NewKifuActivity;
import com.playshogi.website.gwt.client.activity.OpeningsActivity;
import com.playshogi.website.gwt.client.activity.TsumeActivity;
import com.playshogi.website.gwt.client.activity.ViewKifuActivity;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.place.LoginPlace;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.place.MyGamesPlace;
import com.playshogi.website.gwt.client.place.NewKifuPlace;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.ui.FreeBoardView;
import com.playshogi.website.gwt.client.ui.LoginView;
import com.playshogi.website.gwt.client.ui.MainPageView;
import com.playshogi.website.gwt.client.ui.MyGamesView;
import com.playshogi.website.gwt.client.ui.NewKifuView;
import com.playshogi.website.gwt.client.ui.OpeningsView;
import com.playshogi.website.gwt.client.ui.TsumeView;
import com.playshogi.website.gwt.client.ui.ViewKifuView;

public class AppActivityMapper implements ActivityMapper {

	@Inject
	MainPageView mainPageView;
	@Inject
	TsumeView tsumeView;
	@Inject
	FreeBoardView freeBoardView;
	@Inject
	LoginView loginView;
	@Inject
	NewKifuView newKifuView;
	@Inject
	ViewKifuView viewKifuView;
	@Inject
	MyGamesView myGamesView;
	@Inject
	OpeningsView openingsView;
	@Inject
	SessionInformation sessionInformation;
	@Inject
	PlaceController placeController;

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof MainPagePlace) {
			return new MainPageActivity(mainPageView);
		} else if (place instanceof TsumePlace) {
			return new TsumeActivity((TsumePlace) place, tsumeView);
		} else if (place instanceof FreeBoardPlace) {
			return new FreeBoardActivity((FreeBoardPlace) place, freeBoardView);
		} else if (place instanceof OpeningsPlace) {
			return new OpeningsActivity((OpeningsPlace) place, openingsView, placeController);
		} else if (place instanceof MyGamesPlace) {
			return new MyGamesActivity(myGamesView);
		} else if (place instanceof LoginPlace) {
			return new LoginActivity((LoginPlace) place, loginView, sessionInformation);
		} else if (place instanceof NewKifuPlace) {
			return new NewKifuActivity((NewKifuPlace) place, newKifuView, sessionInformation);
		} else if (place instanceof ViewKifuPlace) {
			return new ViewKifuActivity((ViewKifuPlace) place, viewKifuView, sessionInformation);
		}
		return null;
	}

}
