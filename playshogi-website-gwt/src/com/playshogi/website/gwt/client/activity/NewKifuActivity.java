package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.website.gwt.client.events.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.GameRecordChangedEvent;
import com.playshogi.website.gwt.client.events.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.place.NewKifuPlace;
import com.playshogi.website.gwt.client.ui.NewKifuView;

public class NewKifuActivity extends MyAbstractActivity {

	interface MyEventBinder extends EventBinder<NewKifuActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final NewKifuView newKifuView;

	private GameRecord gameRecord;

	private EventBus eventBus;

	public NewKifuActivity(final NewKifuPlace place, final NewKifuView freeBoardView) {
		this.newKifuView = freeBoardView;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting new kifu activity");
		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, eventBus);
		newKifuView.activate(eventBus);
		containerWidget.setWidget(newKifuView.asWidget());
	}

	@Override
	public void onStop() {
		GWT.log("Stopping new kifu activity");
		super.onStop();
	}

	@EventHandler
	public void onGameRecordChanged(final GameRecordChangedEvent gameRecordChangedEvent) {
		gameRecord = gameRecordChangedEvent.getGameRecord();
		eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
		eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
	}

}