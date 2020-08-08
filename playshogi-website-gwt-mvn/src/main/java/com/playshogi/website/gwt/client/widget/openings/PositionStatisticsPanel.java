package com.playshogi.website.gwt.client.widget.openings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.kif.KifMoveConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.PositionStatisticsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.models.PositionMoveDetails;

public class PositionStatisticsPanel extends Composite {
    interface MyEventBinder extends EventBinder<PositionStatisticsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private PositionDetails positionDetails;
    private String positionEvaluation = "";

    private final FlowPanel verticalPanel;

    private final AppPlaceHistoryMapper historyMapper;

    private ShogiPosition shogiPosition;

    public PositionStatisticsPanel(final AppPlaceHistoryMapper historyMapper) {
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
        GWT.log("Position statistics: handle PositionStatisticsEvent");
        positionDetails = event.getPositionDetails();
        shogiPosition = event.getShogiPosition();
        refreshInformation();
    }

    @EventHandler
    public void onPositionEvaluationEvent(final PositionEvaluationEvent event) {
        GWT.log("Position statistics: handle PositionEvaluationEvent");
        positionEvaluation = event.getEvaluation();
        refreshInformation();
    }

    private void refreshInformation() {
        GWT.log("Displaying position details: " + positionDetails);
        verticalPanel.clear();

        if (positionDetails != null) {

            int senteRate = (positionDetails.getSente_wins() * 100) / positionDetails.getTotal();
            String winRate = "Sente win rate: " + senteRate + "%";
            verticalPanel.add(new HTML(SafeHtmlUtils.fromTrustedString(winRate)));

            verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

            if (positionEvaluation != null && !positionEvaluation.isEmpty()) {
                verticalPanel.add(new HTML(SafeHtmlUtils.fromTrustedString(positionEvaluation + "<br>")));
            }

            PositionMoveDetails[] positionMoveDetails = positionDetails.getPositionMoveDetails();

            Grid grid = new Grid(positionMoveDetails.length + 1, 3);

            grid.setHTML(0, 0, SafeHtmlUtils.fromSafeConstant("Move"));
            grid.setHTML(0, 1, SafeHtmlUtils.fromSafeConstant("#"));
            grid.setHTML(0, 2, SafeHtmlUtils.fromSafeConstant("Win rate"));

            for (int i = 0; i < Math.min(positionMoveDetails.length, 8); i++) {
                PositionMoveDetails moveDetails = positionMoveDetails[i];

                String moveUsf = moveDetails.getMove();
                final ShogiMove move = UsfMoveConverter.fromUsfString(moveUsf, shogiPosition);
                Hyperlink hyperlink = new Hyperlink(KifMoveConverter.toKifStringShort(move),
                        historyMapper.getToken(new OpeningsPlace(moveDetails.getNewSfen())));

                hyperlink.setStyleName("movelink");

                hyperlink.addDomHandler(event -> {
                    GWT.log("mouse over");
                    eventBus.fireEvent(new HighlightMoveEvent(move));

                }, MouseOverEvent.getType());

                grid.setWidget(i + 1, 0, hyperlink);

                // grid.setHTML(i + 1, 0, moveDetails.getMove());
                grid.setHTML(i + 1, 1, String.valueOf(moveDetails.getMoveOcurrences()));

                GWT.log(moveDetails.toString());

                int senteMoveRate = (moveDetails.getSente_wins() * 1000) / moveDetails.getPositionOccurences();
                int goteMoveRate = (moveDetails.getGote_wins() * 1000) / moveDetails.getPositionOccurences();

                int sentePixels = moveDetails.getSente_wins() * 100 / moveDetails.getPositionOccurences();
                int gotePixels = moveDetails.getGote_wins() * 100 / moveDetails.getPositionOccurences();
                int otherPixels = 100 - sentePixels - gotePixels;

                String bar = "<table bgcolor=\"#555555\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" " +
                        "class=\"percent\"><tr><td>"
                        + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr height=\"13\">"
                        + "<td align=\"center\" background=\"green.gif\" width=\"" + sentePixels + "\">  " + (senteMoveRate / 10.) + "% </td>"
                        + "<td align=\"center\" background=\"gray.gif\" width=\"" + otherPixels + "\">   </td>"
                        + "<td align=\"center\" background=\"red.gif\" width=\"" + gotePixels + "\">  " + (goteMoveRate / 10.) + "% </td>" + "</tr></table>"
                        + "</td></tr></table>";

                grid.setHTML(i + 1, 2, bar);
                // grid.setHTML(i + 1, 2, String.valueOf(moveRate / 10.));
            }

            verticalPanel.add(grid);
        }
    }

}
