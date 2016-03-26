package com.playshogi.website.gwt.client.widget.navigation;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.place.TsumePlace;

public class NavigationBar extends Composite {

	private final AppPlaceHistoryMapper historyMapper;

	public NavigationBar(final AppPlaceHistoryMapper historyMapper) {
		this.historyMapper = historyMapper;
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName("contentButtons");

		flowPanel.add(createHyperlink("Main page", new MainPagePlace()));
		flowPanel.add(createHyperlink("How to Play", new MainPagePlace()));
		flowPanel.add(createHyperlink("Problems", new TsumePlace()));
		flowPanel.add(createHyperlink("Openings", new MainPagePlace()));
		flowPanel.add(createHyperlink("My Games", new MainPagePlace()));
		flowPanel.add(createHyperlink("Free Board", new MainPagePlace()));
		flowPanel.add(createHyperlink("Play Online", new MainPagePlace()));
		flowPanel.add(createHyperlink("About", new MainPagePlace()));

		initWidget(flowPanel);
	}

	private Hyperlink createHyperlink(final String title, final Place place) {
		Hyperlink hyperlink = new Hyperlink(title, historyMapper.getToken(place));
		hyperlink.setStyleName("contentButton");
		return hyperlink;
	}
}
