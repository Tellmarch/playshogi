package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.NewKifuPlace;

@Singleton
public class MyGamesView extends ResizeComposite {

	@Inject
	public MyGamesView(final AppPlaceHistoryMapper historyMapper) {
		GWT.log("Creating my games view");

		Hyperlink newKifuLink = new Hyperlink("New kifu", historyMapper.getToken(new NewKifuPlace()));

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);

		dockLayoutPanel.addNorth(newKifuLink, 1);
		dockLayoutPanel.add(new HTML("Test"));

		initWidget(dockLayoutPanel);
	}

}
