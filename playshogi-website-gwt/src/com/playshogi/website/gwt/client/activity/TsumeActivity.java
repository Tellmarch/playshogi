package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.playshogi.website.gwt.client.ClientFactory;

public class TsumeActivity extends AbstractActivity {
	private final ClientFactory clientFactory;
	// Name that will be appended to "Good-bye, "
	private final String name;

	public TsumeActivity(final GoodbyePlace place, final ClientFactory clientFactory) {
		this.name = place.getGoodbyeName();
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GoodbyeView goodbyeView = clientFactory.getGoodbyeView();
		goodbyeView.setName(name);
		containerWidget.setWidget(goodbyeView.asWidget());
	}
}