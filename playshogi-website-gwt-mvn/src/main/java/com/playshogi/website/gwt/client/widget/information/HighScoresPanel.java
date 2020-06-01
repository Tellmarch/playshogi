package com.playshogi.website.gwt.client.widget.information;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.HighScoreListEvent;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;

import java.util.Arrays;

public class HighScoresPanel extends Composite {

    interface MyEventBinder extends EventBinder<HighScoresPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private final CellTable<SurvivalHighScore> highScoreTable;

    public HighScoresPanel() {
        GWT.log("Creating high scores panel");
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        highScoreTable = new CellTable<>();

        TextColumn<SurvivalHighScore> highScoreNameColumn = new TextColumn<SurvivalHighScore>() {
            @Override
            public String getValue(final SurvivalHighScore object) {
                return String.valueOf(object.getName());
            }
        };
        highScoreTable.addColumn(highScoreNameColumn, "Name");


        TextColumn<SurvivalHighScore> highScoreColumn = new TextColumn<SurvivalHighScore>() {
            @Override
            public String getValue(final SurvivalHighScore object) {
                return String.valueOf(object.getScore());
            }
        };
        highScoreTable.addColumn(highScoreColumn, "Score");

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant(
                "ByoYomi Survival High Scores:<br>")));

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));
        flowPanel.add(highScoreTable);
        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        initWidget(flowPanel);
    }

    @EventHandler
    public void onHighScoreList(final HighScoreListEvent event) {
        GWT.log("HighScoresPanel: handle HighScoreListEvent");
        highScoreTable.setRowCount(event.getHighScores().length);
        highScoreTable.setRowData(0, Arrays.asList(event.getHighScores()));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating HighScoresPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
