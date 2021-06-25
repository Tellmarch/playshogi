package com.playshogi.website.gwt.client.widget.openings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.kifu.PositionStatisticsEvent;
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

    private final FlowPanel verticalPanel;

    private final AppPlaceHistoryMapper historyMapper;
    private final UserPreferences userPreferences;
    private final boolean inOpeningExplorer;

    private ShogiPosition shogiPosition;
    private String gameSetId;

    public PositionStatisticsPanel(final AppPlaceHistoryMapper historyMapper, final UserPreferences userPreferences,
                                   final boolean inOpeningExplorer) {
        this.historyMapper = historyMapper;
        this.userPreferences = userPreferences;
        this.inOpeningExplorer = inOpeningExplorer;
        verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        if (inOpeningExplorer) {
            verticalPanel.getElement().getStyle().setBackgroundColor("#DBCBCB");
        }

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
        gameSetId = event.getGameSetId();
        refreshInformation();
    }

    private void refreshInformation() {
        GWT.log("Displaying position details");
        verticalPanel.clear();

        if (positionDetails != null) {

            int senteRate = (positionDetails.getSente_wins() * 100) / positionDetails.getTotal();
            String winRate = "Sente win rate: " + senteRate + "%";
            verticalPanel.add(new HTML(SafeHtmlUtils.fromTrustedString(winRate)));

            verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

            PositionMoveDetails[] positionMoveDetails = positionDetails.getPositionMoveDetails();

            Grid grid = new Grid(positionMoveDetails.length + 1, 3);

            grid.setHTML(0, 0, SafeHtmlUtils.fromSafeConstant("Move"));
            grid.setHTML(0, 1, SafeHtmlUtils.fromSafeConstant("#"));
            grid.setHTML(0, 2, SafeHtmlUtils.fromSafeConstant("Win rate"));

            for (int i = 0; i < Math.min(positionMoveDetails.length, 8); i++) {
                PositionMoveDetails moveDetails = positionMoveDetails[i];

                String moveUsf = moveDetails.getMove();
                final ShogiMove move = UsfMoveConverter.fromUsfString(moveUsf, shogiPosition);

                Widget hyperlink;
                if (inOpeningExplorer) {
                    hyperlink = new Hyperlink(userPreferences.getMoveNotationAccordingToPreferences(move, false),
                            historyMapper.getToken(new OpeningsPlace(moveDetails.getNewSfen(), gameSetId)));
                } else {
                    hyperlink = new Anchor(userPreferences.getMoveNotationAccordingToPreferences(move, false));
                    hyperlink.addDomHandler(event -> eventBus.fireEvent(new MovePlayedEvent(move)),
                            ClickEvent.getType());
                }

                hyperlink.setStyleName("movelink");

                hyperlink.addDomHandler(event -> eventBus.fireEvent(new HighlightMoveEvent(move)),
                        MouseOverEvent.getType());

                grid.setWidget(i + 1, 0, hyperlink);

                // grid.setHTML(i + 1, 0, moveDetails.getMove());
                grid.setHTML(i + 1, 1, String.valueOf(moveDetails.getMoveOcurrences()));

                int senteMoveRate = (moveDetails.getSente_wins() * 1000) / moveDetails.getNewPositionOccurences();
                int goteMoveRate = (moveDetails.getGote_wins() * 1000) / moveDetails.getNewPositionOccurences();

                int sentePixels = moveDetails.getSente_wins() * 100 / moveDetails.getNewPositionOccurences();
                int gotePixels = moveDetails.getGote_wins() * 100 / moveDetails.getNewPositionOccurences();
                int otherPixels = 100 - sentePixels - gotePixels;

                String senteWinRateLabel = senteMoveRate != 0 ? (senteMoveRate / 10.) + "%" : "";
                String goteWinRateLabel = goteMoveRate != 0 ? (goteMoveRate / 10.) + "%" : "";

                String bar = "<table bgcolor=\"#555555\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" " +
                        "class=\"percent\"><tr><td>"
                        + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr height=\"13\">"
                        + "<td align=\"center\" background=\"green.gif\" width=\"" + sentePixels + "\">  " + senteWinRateLabel + " " +
                        "</td>"
                        + "<td align=\"center\" background=\"gray.gif\" width=\"" + otherPixels + "\">   </td>"
                        + "<td align=\"center\" background=\"red.gif\" width=\"" + gotePixels + "\">  " + goteWinRateLabel + " </td" +
                        ">" + "</tr></table>"
                        + "</td></tr></table>";

                grid.setHTML(i + 1, 2, bar);
                // grid.setHTML(i + 1, 2, String.valueOf(moveRate / 10.));
            }

            verticalPanel.add(grid);
        }
    }

}
