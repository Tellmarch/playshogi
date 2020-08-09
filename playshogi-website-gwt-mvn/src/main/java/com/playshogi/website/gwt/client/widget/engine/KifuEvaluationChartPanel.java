package com.playshogi.website.gwt.client.widget.engine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.playshogi.website.gwt.client.events.KifuEvaluationEvent;
import com.playshogi.website.gwt.client.events.RequestKifuEvaluationEvent;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;

public class KifuEvaluationChartPanel extends Composite {

    interface MyEventBinder extends EventBinder<KifuEvaluationChartPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private VerticalPanel panel;
    private LineChart chart;

    public KifuEvaluationChartPanel() {
        panel = new VerticalPanel();

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setWidth("400px");
        Button evaluateButton = new Button("Evaluate");
        evaluateButton.addClickHandler(clickEvent -> eventBus.fireEvent(new RequestKifuEvaluationEvent()));

        flowPanel.add(evaluateButton);
        panel.add(flowPanel);

        initialize();
        initWidget(panel);

    }

    private void initialize() {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(() -> {
            chart = new LineChart();
            panel.add(chart);
        });
    }

    @EventHandler
    public void onKifuEvaluationEvent(final KifuEvaluationEvent event) {
        GWT.log("KifuEvaluationChartPanel: handle KifuEvaluationEvent");
        drawEvaluation(event);
    }

    private void drawEvaluation(KifuEvaluationEvent event) {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.NUMBER, "move");
        dataTable.addColumn(ColumnType.NUMBER, "evaluation");
        PositionEvaluationDetails[] positionEvaluationDetails = event.getPositionEvaluationDetails();
        dataTable.addRows(positionEvaluationDetails.length);
        for (int i = 0; i < positionEvaluationDetails.length; i++) {
            PrincipalVariationDetails[] history = positionEvaluationDetails[i].getPrincipalVariationHistory();
            if (history.length > 0) {
                dataTable.setValue(i, 0, i);
                int graphValue = i % 2 == 0 ? history[0].getEvaluationCP() : -history[0].getEvaluationCP();
                graphValue = Math.min(1000, Math.max(-1000, graphValue));
                dataTable.setValue(i, 1, graphValue);
            }
        }

        LineChartOptions options = LineChartOptions.create();
        options.setBackgroundColor("#f0f0f0");
        options.setFontName("Tahoma");
        options.setTitle("Evaluation");
        options.setHAxis(HAxis.create("Move"));
        options.setVAxis(VAxis.create("Centipawns"));
        options.setWidth(800);
        options.setHeight(400);

        chart.draw(dataTable, options);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating KifuEvaluationChartPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
