package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.ui.OpeningsView;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class OpeningsActivity extends MyAbstractActivity {

	interface MyEventBinder extends EventBinder<OpeningsActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

	private final String boardId;
	private final OpeningsView openingsView;

	public OpeningsActivity(final OpeningsPlace place, final OpeningsView freeBoardView) {
		this.openingsView = freeBoardView;
		this.boardId = place.getBoardId();
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting openings activity");
		eventBinder.bindEventHandlers(this, eventBus);
		openingsView.activate(eventBus);
		containerWidget.setWidget(openingsView.asWidget());
	}

	@Override
	public void onStop() {
		GWT.log("Stopping openings activity");
		super.onStop();
	}

	@EventHandler
	public void onPositionChanged(final PositionChangedEvent event) {
		GWT.log("OPENINGS - POSITION CHANGED EVENT");

		kifuService.getPositionDetails(SfenConverter.toSFEN(event.getPosition()), 1, new AsyncCallback<PositionDetails>() {

			@Override
			public void onSuccess(final PositionDetails result) {
				GWT.log("OPENINGS - GOT POSITION DETAILS " + result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("OPENINGS - ERROR GETTING POSITION STATS");
			}
		});
	}

}