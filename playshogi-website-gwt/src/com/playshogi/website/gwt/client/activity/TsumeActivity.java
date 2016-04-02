package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class TsumeActivity extends AbstractActivity {

	interface MyEventBinder extends EventBinder<TsumeActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final String tsumeId;
	private final TsumeView tsumeView;

	public TsumeActivity(final TsumePlace place, final TsumeView tsumeView) {
		this.tsumeView = tsumeView;
		this.tsumeId = place.getTsumeId();
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		eventBinder.bindEventHandlers(this, eventBus);
		tsumeView.setTsumeId(tsumeId);
		containerWidget.setWidget(tsumeView.asWidget());
	}

	@EventHandler
	void onUserSkippedProblem(final UserSkippedProblemEvent event) {
		tsumeView.setTsumeId(null);
	}
}