package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.place.NewKifuPlace;
import com.playshogi.website.gwt.client.ui.NewKifuView;

public class NewKifuActivity extends MyAbstractActivity {

	interface MyEventBinder extends EventBinder<NewKifuActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final NewKifuView newKifuView;

	public NewKifuActivity(final NewKifuPlace place, final NewKifuView freeBoardView) {
		this.newKifuView = freeBoardView;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting new kifu activity");
		eventBinder.bindEventHandlers(this, eventBus);
		newKifuView.activate(eventBus);
		containerWidget.setWidget(newKifuView.asWidget());
	}

	@Override
	public void onStop() {
		GWT.log("Stopping new kifu activity");
		super.onStop();
	}

}