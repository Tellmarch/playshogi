package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.NewKifuPlace;

@Singleton
public class MyGamesView extends Composite {

	private final AppPlaceHistoryMapper historyMapper;

	@Inject
	public MyGamesView(final AppPlaceHistoryMapper historyMapper) {
		GWT.log("Creating my games view");
		this.historyMapper = historyMapper;

		Hyperlink newKifuLink = new Hyperlink("New kifu", historyMapper.getToken(new NewKifuPlace()));
		initWidget(newKifuLink);
	}

}
