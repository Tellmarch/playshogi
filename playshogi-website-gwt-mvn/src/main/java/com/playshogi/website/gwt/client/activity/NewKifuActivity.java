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
import com.playshogi.website.gwt.client.place.NewKifuPlace;
import com.playshogi.website.gwt.client.ui.NewKifuView;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class NewKifuActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<NewKifuActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final NewKifuView newKifuView;

    private GameRecord gameRecord;

    private EventBus eventBus;

    private final SessionInformation sessionInformation;

    public NewKifuActivity(final NewKifuPlace place, final NewKifuView freeBoardView,
                           final SessionInformation sessionInformation) {
        this.newKifuView = freeBoardView;
        this.sessionInformation = sessionInformation;
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
        GWT.log("New Kifu Activity Handling GameRecordChangedEvent");
        gameRecord = gameRecordChangedEvent.getGameRecord();
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
    }

    @EventHandler
    public void onGameRecordSaveRequested(final GameRecordSaveRequestedEvent gameRecordSaveRequestedEvent) {
        GWT.log("New Kifu Activity Handling GameRecordSaveRequestedEvent");
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