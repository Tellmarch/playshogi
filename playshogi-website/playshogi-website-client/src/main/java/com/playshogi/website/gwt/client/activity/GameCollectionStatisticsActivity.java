package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.CollectionStatisticsEvent;
import com.playshogi.website.gwt.client.place.GameCollectionStatisticsPlace;
import com.playshogi.website.gwt.client.ui.GameCollectionStatisticsView;
import com.playshogi.website.gwt.shared.models.GameCollectionStatisticsDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class GameCollectionStatisticsActivity extends MyAbstractActivity {
    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<GameCollectionStatisticsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final GameCollectionStatisticsPlace place;
    private final GameCollectionStatisticsView view;
    private final SessionInformation sessionInformation;

    public GameCollectionStatisticsActivity(final GameCollectionStatisticsPlace place,
                                            final GameCollectionStatisticsView view,
                                            final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collection statistics activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        GWT.log("Querying for collection games");
        kifuService.getGameSetStatistics(sessionInformation.getSessionId(), place.getCollectionId(),
                new AsyncCallback<GameCollectionStatisticsDetails>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("GameCollectionStatisticsActivity: error retrieving collection stats");
                    }

                    @Override
                    public void onSuccess(GameCollectionStatisticsDetails result) {
                        GWT.log("GameCollectionStatisticsActivity: retrieved collection stats");
                        eventBus.fireEvent(new CollectionStatisticsEvent(result));
                    }
                });
    }


}
