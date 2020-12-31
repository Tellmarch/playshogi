package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameResult;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordExportRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.ImportGameRecordEvent;
import com.playshogi.website.gwt.client.place.ProblemEditorPlace;
import com.playshogi.website.gwt.client.ui.ProblemEditorView;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ProblemEditorActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ProblemEditorActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final ProblemEditorView problemEditorView;

    private GameRecord gameRecord;

    private EventBus eventBus;

    private final SessionInformation sessionInformation;

    public ProblemEditorActivity(final ProblemEditorPlace place, final ProblemEditorView problemEditorView,
                                 final SessionInformation sessionInformation) {
        this.problemEditorView = problemEditorView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problem editor activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemEditorView.activate(eventBus);
        containerWidget.setWidget(problemEditorView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping new kifu activity");
        super.onStop();
    }

    @EventHandler
    public void onImportGameRecord(final ImportGameRecordEvent gameRecordChangedEvent) {
        GWT.log("problem editor Activity Handling ImportGameRecordEvent");
        gameRecord = gameRecordChangedEvent.getGameRecord();
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
    }

    @EventHandler
    public void onGameRecordSaveRequested(final GameRecordSaveRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordSaveRequestedEvent");
        gameRecord = getGameRecord();
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

    @EventHandler
    public void onGameRecordExportRequested(final GameRecordExportRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordExportRequestedEvent");
        gameRecord = getGameRecord();
        String usfString = UsfFormat.INSTANCE.write(gameRecord);
        GWT.log(usfString);
        Window.alert(usfString);
    }

    private GameRecord getGameRecord() {
        GameTree gameTree = problemEditorView.getGameNavigation().getGameTree();
        return new GameRecord(new GameInformation(), gameTree, GameResult.UNKNOWN);
    }

}