package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class TsumeActivity extends AbstractActivity {

	interface MyEventBinder extends EventBinder<TsumeActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final ClientFactory clientFactory;
	private final String tsumeId;
	private TsumeView tsumeView;

	public TsumeActivity(final TsumePlace place, final ClientFactory clientFactory) {
		this.tsumeId = place.getTsumeId();
		this.clientFactory = clientFactory;
		eventBinder.bindEventHandlers(this, clientFactory.getEventBus());
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		tsumeView = clientFactory.getTsumeView();
		tsumeView.setTsumeId(tsumeId);
		containerWidget.setWidget(tsumeView.asWidget());
	}

	@EventHandler
	void onUserSkippedProblem(final UserSkippedProblemEvent event) {
		tsumeView.setTsumeId(null);
	}
}