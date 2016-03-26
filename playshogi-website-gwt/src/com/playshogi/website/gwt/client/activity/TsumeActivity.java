package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class TsumeActivity extends AbstractActivity {
	private final ClientFactory clientFactory;
	private final String tsumeId;

	public TsumeActivity(final TsumePlace place, final ClientFactory clientFactory) {
		this.tsumeId = place.getTsumeId();
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		TsumeView tsumeView = clientFactory.getTsumeView();
		tsumeView.setTsumeId(tsumeId);
		containerWidget.setWidget(tsumeView.asWidget());
	}
}