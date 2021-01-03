package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.GameResult;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.*;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.PreviewKifuPlace;
import com.playshogi.website.gwt.client.ui.KifuEditorView;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class KifuEditorActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<KifuEditorActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final KifuEditorPlace place;
    private final KifuEditorView kifuEditorView;
    private final SessionInformation sessionInformation;
    private final PlaceController placeController;

    private EventBus eventBus;

    public KifuEditorActivity(final KifuEditorPlace place, final KifuEditorView kifuEditorView,
                              final SessionInformation sessionInformation, final PlaceController placeController) {
        this.place = place;
        this.kifuEditorView = kifuEditorView;
        this.sessionInformation = sessionInformation;
        this.placeController = placeController;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problem editor activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        kifuEditorView.activate(eventBus, place.getType());
        containerWidget.setWidget(kifuEditorView.asWidget());
        if (place.getKifuId() != null) {
            kifuService.getKifuUsf(sessionInformation.getSessionId(), place.getKifuId(), new AsyncCallback<String>() {
                @Override
                public void onFailure(final Throwable throwable) {
                    GWT.log("Error requesting the kifu: " + place.getKifuId());
                }

                @Override
                public void onSuccess(final String usf) {
                    GWT.log("Received Kifu USF");
                    kifuEditorView.loadGameRecord(UsfFormat.INSTANCE.readSingle(usf));
                }
            });
        }
    }

    @Override
    public void onStop() {
        GWT.log("Stopping problem editor activity");
        super.onStop();
    }

    @EventHandler
    public void onImportGameRecord(final ImportGameRecordEvent gameRecordChangedEvent) {
        GWT.log("problem editor Activity Handling ImportGameRecordEvent");
        GameRecord gameRecord = gameRecordChangedEvent.getGameRecord();
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
    }

    @EventHandler
    public void onGameRecordSaveRequested(final GameRecordSaveRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordSaveRequestedEvent");
        String usfString = UsfFormat.INSTANCE.write(getGameRecord());
        GWT.log(usfString);
        sessionInformation.ifLoggedIn(() ->
                kifuService.saveKifu(sessionInformation.getSessionId(), usfString, new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(final String result) {
                        GWT.log("Kifu saved successfully: " + result);
                    }

                    @Override
                    public void onFailure(final Throwable caught) {
                        GWT.log("Error while saving Kifu: ", caught);
                    }
                }));
    }

    @EventHandler
    public void onGameRecordExportRequested(final GameRecordExportRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordExportRequestedEvent");
        String usfString = UsfFormat.INSTANCE.write(getGameRecord());
        GWT.log(usfString);
        Window.alert(usfString);
    }

    @EventHandler
    public void onGameRecordPreviewRequested(final GameRecordPreviewRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordPreviewRequestedEvent");
        String usfString = UsfFormat.INSTANCE.write(getGameRecord());
        placeController.goTo(new PreviewKifuPlace(usfString, 0));
    }

    private GameRecord getGameRecord() {
        GameTree gameTree = kifuEditorView.getGameNavigation().getGameTree();
        return new GameRecord(new GameInformation(), gameTree, GameResult.UNKNOWN);
    }

}