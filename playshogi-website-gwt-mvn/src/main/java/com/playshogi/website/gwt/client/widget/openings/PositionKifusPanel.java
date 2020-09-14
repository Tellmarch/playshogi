package com.playshogi.website.gwt.client.widget.openings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.PositionStatisticsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.shared.models.PositionDetails;

public class PositionKifusPanel extends Composite {
    interface MyEventBinder extends EventBinder<PositionKifusPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private PositionDetails positionDetails;

    private final FlowPanel verticalPanel;

    private final AppPlaceHistoryMapper historyMapper;

    public PositionKifusPanel(final AppPlaceHistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
        verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        verticalPanel.getElement().getStyle().setBackgroundColor("#DBCBCB");

        initWidget(verticalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating position statistics panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onPositionStatisticsEvent(final PositionStatisticsEvent event) {
        GWT.log("Position kifus panel: handle PositionStatisticsEvent");
        positionDetails = event.getPositionDetails();
        refreshInformation();
    }

    private void refreshInformation() {
        GWT.log("Displaying position kifus: " + positionDetails);
        verticalPanel.clear();

        verticalPanel.add(new HTML("<br/>Sample games from this position:<br/>"));

        if (positionDetails != null) {

            String[] kifuIds = positionDetails.getKifuIds();
            String[] kifuDescs = positionDetails.getKifuDesc();

            Grid grid = new Grid(kifuIds.length, 1);

            for (int i = 0; i < kifuIds.length; i++) {

                String kifuId = kifuIds[i];
                String kifuDesc = kifuDescs[i];

                Hyperlink hyperlink = new Hyperlink(kifuDesc, historyMapper.getToken(new ViewKifuPlace(kifuId, 1)));

                grid.setWidget(i, 0, hyperlink);

            }

            verticalPanel.add(grid);
        }
    }

}
