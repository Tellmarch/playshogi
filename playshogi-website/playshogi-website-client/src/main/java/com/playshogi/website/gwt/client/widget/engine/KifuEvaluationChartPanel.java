package com.playshogi.website.gwt.client.widget.engine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.gametree.MoveSelectedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.KifuEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestKifuEvaluationEvent;
import com.playshogi.website.gwt.client.util.MyChartLoader;
import com.playshogi.website.gwt.shared.models.*;

public class KifuEvaluationChartPanel extends Composite {

    interface MyEventBinder extends EventBinder<KifuEvaluationChartPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final UserPreferences userPreferences;

    private EventBus eventBus;
    private final VerticalPanel panel;
    private LineChart chart;
    private final HTML statusHTML;
    private final HTML insightsHTML;
    private final HTML mistakesHTML;
    private String kifuId;
    private PositionEvaluationDetails[] positionEvaluationDetails;
    private GameInsightsDetails gameInsightsDetails;
    private int mistakeIndex = -1;
    private boolean blackMistakes = true;

    public KifuEvaluationChartPanel(final UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        panel = new VerticalPanel();

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setWidth("400px");
        Button evaluateButton = new Button("Analyze game with computer");
        evaluateButton.addClickHandler(clickEvent -> eventBus.fireEvent(new RequestKifuEvaluationEvent()));

        flowPanel.add(evaluateButton);

        statusHTML = new HTML("Evaluation status");
        flowPanel.add(statusHTML);

        panel.add(flowPanel);

        insightsHTML = new HTML("");
        insightsHTML.setStyleName("insights-html");

        mistakesHTML = new HTML("");
        mistakesHTML.setStyleName("mistakes-html");

        initialize();
        initWidget(panel);

    }

    private void initialize() {
        MyChartLoader.INSTANCE.runWhenReady(() -> {
            chart = new LineChart();
            chart.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(final SelectEvent selectEvent) {
                    if (chart.getSelection().length() > 0) {
                        int move = chart.getSelection().get(0).getRow();
                        GWT.log("Selected move " + move);
                        eventBus.fireEvent(new MoveSelectedEvent(move));
                    }
                }
            });
            panel.add(chart);
            panel.add(insightsHTML);
            panel.add(new Button("Review mistakes", (ClickHandler) clickEvent -> reviewMistakes()));
            panel.add(mistakesHTML);
        });
    }

    private void reviewMistakes() {
        MistakeDetails mistake;
        if (blackMistakes) {
            mistakeIndex++;
            if (mistakeIndex > gameInsightsDetails.getBlackMistakes().length - 1) {
                mistakeIndex = 0;
                blackMistakes = false;
                mistake = gameInsightsDetails.getWhiteMistakes()[0];
            } else {
                mistake = gameInsightsDetails.getBlackMistakes()[mistakeIndex];
            }
        } else {
            mistakeIndex++;
            if (mistakeIndex > gameInsightsDetails.getWhiteMistakes().length - 1) {
                mistakeIndex = 0;
                blackMistakes = true;
                mistake = gameInsightsDetails.getBlackMistakes()[0];
            } else {
                mistake = gameInsightsDetails.getWhiteMistakes()[mistakeIndex];
            }
        }
        GWT.log("Going to study mistake: " + mistake);
        eventBus.fireEvent(new MoveSelectedEvent(mistake.getMoveCount() - 1));
        mistakesHTML.setHTML("<br/>" + mistake.getType() + " - At move " + mistake.getMoveCount() + ". " + (blackMistakes ? "Black" : "White") +
                " played " + mistake.getMovePlayed() + ", " +
                "losing " + (mistake.getScoreAfterMove().getEvaluationCP() + mistake.getScoreBeforeMove().getEvaluationCP())
                + " centipawns.<br/> Instead, it would have been better to play " + mistake.getComputerMove() + ".<br" +
                "/>");

    }

    private void summarizeInsights(GameInsightsDetails details) {
        this.gameInsightsDetails = details;
        insightsHTML.setHTML("<br/>Black average centipawn loss: " + details.getBlackAvgCentipawnLoss() + "<br/>" +
                getMistakesSummary(details.getBlackMistakes()) +
                "<br/>White average centipawn loss: " + details.getWhiteAvgCentipawnLoss() + "<br/>" +
                getMistakesSummary(details.getWhiteMistakes()) + "<br/>");
    }

    private String getMistakesSummary(final MistakeDetails[] details) {
        int imprecisions = 0;
        int mistakes = 0;
        int blunders = 0;

        for (MistakeDetails mistake : details) {
            switch (mistake.getType()) {
                case IMPRECISION:
                    imprecisions++;
                    break;
                case MISTAKE:
                    mistakes++;
                    break;
                case BLUNDER:
                    blunders++;
                    break;
            }
        }

        return imprecisions + " imprecisions<br/>" +
                mistakes + " mistakes<br/>" +
                blunders + " blunders<br/>";
    }

    private native void addTooltipColumn(DataTable data) /*-{
        data.addColumn({type:'string', role:'tooltip'});
    }-*/;

    private native void addAnnotationColumn(DataTable data) /*-{
        data.addColumn({type:'string', role:'annotation'});
    }-*/;

    private void drawEvaluation(KifuEvaluationEvent event) {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.NUMBER, "move");
        dataTable.addColumn(ColumnType.NUMBER, "evaluation");
        addTooltipColumn(dataTable);
        if (userPreferences.isAnnotateGraphs()) {
            addAnnotationColumn(dataTable);
        }
        positionEvaluationDetails = event.getPositionEvaluationDetails();
        dataTable.addRows(positionEvaluationDetails.length);

        boolean mate = false;

        for (int i = 0; i < positionEvaluationDetails.length; i++) {
            PrincipalVariationDetails[] topPrincipalVariations =
                    positionEvaluationDetails[i].getTopPrincipalVariations();
            if (topPrincipalVariations.length > 0) {
                dataTable.setValue(i, 0, i);
                PrincipalVariationDetails best = topPrincipalVariations[0];

                int toolTipValue = i % 2 == 0 ? best.getEvaluationCP() : -best.getEvaluationCP();
                int graphValue = Math.min(2000, Math.max(-2000, toolTipValue));

                String toolTip = "Move " + i + "\n" + "Evaluation: " + toolTipValue;
                String annotation = null;

                if (best.isForcedMate()) {
                    mate = true;
                    graphValue = (best.getNumMovesBeforeMate() <= 0) == (i % 2 == 0) ? -2000 : 2000;
                    toolTip = "Move " + i + "\n" + "Mate in " + Math.abs(best.getNumMovesBeforeMate());
                } else {
                    if (mate) {
                        annotation = "Missed mate";
                    }
                    mate = false;
                }

                dataTable.setValue(i, 1, graphValue);
                dataTable.setValue(i, 2, toolTip);
                if (userPreferences.isAnnotateGraphs()) {
                    dataTable.setValue(i, 3, annotation);
                }
            }
        }

        LineChartOptions options = LineChartOptions.create();
        options.setBackgroundColor("#f0f0f0");
        options.setFontName("Tahoma");
        options.setTitle("Evaluation");
        options.setHAxis(HAxis.create("Move"));
        options.setVAxis(VAxis.create("Centipawns"));
        options.setWidth(600);
        options.setHeight(300);

        ChartArea chartArea = ChartArea.create();
        chartArea.setLeft(60);
        chartArea.setWidth(540);
        chartArea.setTop(30);
        chartArea.setHeight(240);
        options.setChartArea(chartArea);
        chart.draw(dataTable, options);
        chart.setVisible(true);
    }

    public void activate(final EventBus eventBus, final String kifuId) {
        GWT.log("Activating KifuEvaluationChartPanel");
        this.kifuId = kifuId;
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        if (chart != null) {
            chart.setVisible(false);
        }
    }

    @EventHandler
    public void onKifuEvaluationEvent(final KifuEvaluationEvent event) {
        GWT.log("KifuEvaluationChartPanel: handle KifuEvaluationEvent");
        if (!event.getKifuId().equals(kifuId)) {
            GWT.log("KifuEvaluationChartPanel: handle KifuEvaluationEvent - not for us");
            return;
        }
        AnalysisRequestStatus status = event.getStatus();
        switch (status) {
            case IN_PROGRESS:
                statusHTML.setHTML("Analyzing... " + event.getPositionEvaluationDetails().length);
                drawEvaluation(event);
                summarizeInsights(event.getResult().getGameInsightsDetails());
                break;
            case COMPLETED:
                statusHTML.setHTML("Analysis complete!");
                drawEvaluation(event);
                summarizeInsights(event.getResult().getGameInsightsDetails());
                break;
            case QUEUED:
                statusHTML.setHTML("Queued in position " + event.getQueuePosition());
                break;
            case QUEUE_TOO_LONG:
                statusHTML.setHTML("The queue is too long - please try again later.");
                break;
            case USER_QUOTA_EXCEEDED:
                statusHTML.setHTML("User quota exceeded - please try again tomorrow.");
                break;
            case NOT_ALLOWED:
                statusHTML.setHTML("Game analysis is not available for guest users, consider registering.");
                break;
            case NOT_REQUESTED:
            case UNAVAILABLE:
                statusHTML.setHTML("Analysis: unexpected error.");
                break;
        }
    }

    @EventHandler
    public void onPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("KifuEvaluationChartPanel handling PositionChangedEvent");
        if (positionEvaluationDetails != null) {
            int moveCount = event.getPosition().getMoveCount();
            if (positionEvaluationDetails.length > moveCount && chart != null && chart.isVisible()
                    && positionEvaluationDetails[moveCount].getSfen().equals(SfenConverter.toSFEN(event.getPosition()))) {
                chart.setSelection(Selection.create(moveCount, 1));
            }
        }
    }
}
