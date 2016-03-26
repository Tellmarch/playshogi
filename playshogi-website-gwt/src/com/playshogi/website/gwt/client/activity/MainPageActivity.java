package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.ui.MainPageView;

public class MainPageActivity extends AbstractActivity {
	private final ClientFactory clientFactory;

	public MainPageActivity(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		MainPageView mainPageView = clientFactory.getMainPageView();
		containerWidget.setWidget(mainPageView.asWidget());
	}

	@Override
	public String mayStop() {
		return "Please hold on. This activity is stopping.";
	}

}
