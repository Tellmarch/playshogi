package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.PositionStatisticsEvent;
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

	private final String sfen;
	private final OpeningsView openingsView;

	private EventBus eventBus;

	private final PlaceController placeController;

	public OpeningsActivity(final OpeningsPlace place, final OpeningsView freeBoardView, final PlaceController placeController) {
		this.openingsView = freeBoardView;
		this.placeController = placeController;
		this.sfen = place.getSfen();
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		this.eventBus = eventBus;
		GWT.log("Starting openings activity");
		eventBinder.bindEventHandlers(this, eventBus);
		final ShogiPosition position = SfenConverter.fromSFEN(sfen);
		openingsView.activate(position, eventBus);
		containerWidget.setWidget(openingsView.asWidget());

		kifuService.getPositionDetails(SfenConverter.toSFEN(position), 1, new AsyncCallback<PositionDetails>() {

			@Override
			public void onSuccess(final PositionDetails result) {
				GWT.log("OPENINGS - GOT POSITION DETAILS " + result);
				eventBus.fireEvent(new PositionStatisticsEvent(result, position));
			}

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("OPENINGS - ERROR GETTING POSITION STATS");
			}
		});
	}

	@Override
	public void onStop() {
		GWT.log("Stopping openings activity");
		super.onStop();
	}

	@EventHandler
	public void onPositionChanged(final PositionChangedEvent event) {
		GWT.log("OPENINGS - POSITION CHANGED EVENT");

		if (event.isTriggeredByUser()) {
			placeController.goTo(new OpeningsPlace(SfenConverter.toSFEN(event.getPosition())));
		}

	}

}