package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.GameRecordChangedEvent;
import com.playshogi.website.gwt.client.events.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.events.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.ui.ViewKifuView;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ViewKifuActivity extends MyAbstractActivity {

	interface MyEventBinder extends EventBinder<ViewKifuActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

	private final ViewKifuView viewKifuView;

	private GameRecord gameRecord;

	private EventBus eventBus;

	private final SessionInformation sessionInformation;

	private final String kifuId;

	public ViewKifuActivity(final ViewKifuPlace place, final ViewKifuView viewKifuView, final SessionInformation sessionInformation) {
		this.viewKifuView = viewKifuView;
		this.sessionInformation = sessionInformation;
		kifuId = place.getKifuId();
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting view kifu activity");
		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, eventBus);
		viewKifuView.activate(eventBus);
		containerWidget.setWidget(viewKifuView.asWidget());

		kifuService.getKifuUsf(sessionInformation.getSessionId(), kifuId, new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String result) {
				GWT.log("Kifu loaded successfully: " + result);
				gameRecord = UsfFormat.INSTANCE.read(result);

				eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
				eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Error while loqding the kifu: " + caught);
			}
		});
	}

	@Override
	public void onStop() {
		GWT.log("Stopping view kifu activity");
		super.onStop();
	}

	@EventHandler
	public void onGameRecordChanged(final GameRecordChangedEvent gameRecordChangedEvent) {
		GWT.log("View Kifu Activity Handling GameRecordChangedEvent");
		gameRecord = gameRecordChangedEvent.getGameRecord();
		eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
		eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
	}

	@EventHandler
	public void onGameRecordSaveRequested(final GameRecordSaveRequestedEvent gameRecordSaveRequestedEvent) {
		GWT.log("View Kifu Activity Handling GameRecordSaveRequestedEvent");
		String usfString = UsfFormat.INSTANCE.write(gameRecord);
		GWT.log(usfString);
		kifuService.saveKifu(sessionInformation.getSessionId(), usfString, new AsyncCallback<String>() {

			@Override
			public void onSuccess(final String result) {
				GWT.log("Kifu saved successfully: " + result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Error while saving Kifu: ", caught);
			}
		});
	}

}