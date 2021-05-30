package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.widget.openings.PositionKifusPanel;
import com.playshogi.website.gwt.client.widget.openings.PositionStatisticsPanel;

public class DatabasePanel extends Composite {

    interface MyEventBinder extends EventBinder<DatabasePanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PositionStatisticsPanel positionStatisticsPanel;
    private final PositionKifusPanel positionKifusPanel;

    private EventBus eventBus;

    public DatabasePanel(AppPlaceHistoryMapper appPlaceHistoryMapper, UserPreferences userPreferences) {
        FlowPanel panel = new FlowPanel();
        positionStatisticsPanel = new PositionStatisticsPanel(appPlaceHistoryMapper, userPreferences, false);
        panel.add(positionStatisticsPanel);
        positionKifusPanel = new PositionKifusPanel(appPlaceHistoryMapper, false);
        panel.add(positionKifusPanel);
        initWidget(panel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating DatabasePanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        positionStatisticsPanel.activate(eventBus);
        positionKifusPanel.activate(eventBus);
    }
}
