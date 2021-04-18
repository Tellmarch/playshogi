package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.place.PlayPlace;
import com.playshogi.website.gwt.client.ui.PlayView;
import com.playshogi.website.gwt.shared.services.ComputerService;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class PlayActivity extends MyAbstractActivity {

    private final PlayView playView;
    private final SessionInformation sessionInformation;

    interface MyEventBinder extends EventBinder<PlayActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final ComputerServiceAsync computerService = GWT.create(ComputerService.class);

    public PlayActivity(final PlayPlace place, final PlayView playView, final SessionInformation sessionInformation) {
        this.playView = playView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting play activity");
        eventBinder.bindEventHandlers(this, eventBus);
        playView.activate(eventBus);
        containerWidget.setWidget(playView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping play activity");
        super.onStop();
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("PLAY - POSITION CHANGED EVENT - " + event.isTriggeredByUser());
        GWT.log("Position SFEN: " + SfenConverter.toSFEN(event.getPosition()));

        if (event.getPosition().getPlayerToMove() == Player.WHITE) {
            computerService.getComputerMove(sessionInformation.getSessionId(),
                    SfenConverter.toSFEN(event.getPosition()),
                    new AsyncCallback<String>() {
                        @Override
                        public void onFailure(final Throwable throwable) {
                            GWT.log("getComputerMove failed");
                        }

                        @Override
                        public void onSuccess(final String move) {
                            GWT.log("getComputerMove success: " + move);
                            playView.getGameNavigator().addMove(UsfMoveConverter.fromUsfString(move,
                                    playView.getPosition()), false);
                        }
                    });
        }

    }
}
