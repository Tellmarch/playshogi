package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.CollectionStatisticsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.util.MyChartLoader;
import com.playshogi.website.gwt.shared.models.GameCollectionStatisticsDetails;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.dominokit.domino.ui.Typography.Paragraph;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.b;

@Singleton
public class GameCollectionStatisticsView extends Composite {

    interface MyEventBinder extends EventBinder<GameCollectionStatisticsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SessionInformation sessionInformation;
    private final AppPlaceHistoryMapper historyMapper;

    private final HtmlContentBuilder<HTMLHeadingElement> collectionHeading;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionDescription;

    private final Tab ratingTab;

    private LineChart ratingOverTimeGraph;
    private LineChart ratingOverGamesGraph;
    private EventBus eventBus;

    private GameCollectionStatisticsDetails statistics;

    @Inject
    public GameCollectionStatisticsView(final SessionInformation sessionInformation,
                                        final AppPlaceHistoryMapper historyMapper,
                                        final PlaceController placeController) {
        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;

        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.css(Styles.padding_20);
        collectionHeading = Elements.h(2).textContent("");
        collectionDescription = Elements.h(4).textContent("");
        root.add(collectionHeading);
        root.add(collectionDescription);


        ratingTab = Tab.create(Icons.ALL.face(), " RATING")
                .appendChild(b().textContent("Profile Content"));

        String SAMPLE_TEXT = "TO DO";
        root.add(
                TabsPanel.create()
                        .appendChild(
                                Tab.create(Icons.ALL.home_mdi(), " GENERAL")
                                        .appendChild(b().textContent("Home Content"))
                                        .appendChild(Paragraph.create(SAMPLE_TEXT)))
                        .appendChild(ratingTab.activate())
                        .appendChild(
                                Tab.create(Icons.ALL.email(), " OPENINGS")
                                        .appendChild(b().textContent("Messages Content"))
                                        .appendChild(Paragraph.create(SAMPLE_TEXT)))
                        .appendChild(
                                Tab.create(Icons.ALL.settings(), " ENDGAME")
                                        .appendChild(b().textContent("Settings Content"))
                                        .appendChild(Paragraph.create(SAMPLE_TEXT)))
                        .element());


        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(root.element()));
        scrollPanel.setSize("100%", "100%");


        createChartWhenReady();
        initWidget(scrollPanel);
    }

    private void createChartWhenReady() {
        MyChartLoader.INSTANCE.runWhenReady(() -> {
            GWT.log("*** START GameCollectionStatisticsView INITIALIZATION");
            ratingOverTimeGraph = new LineChart();
            ratingTab.appendChild(Js.<Node>uncheckedCast(ratingOverTimeGraph.getElement()));
            ratingOverGamesGraph = new LineChart();
            ratingTab.appendChild(Js.<Node>uncheckedCast(ratingOverGamesGraph.getElement()));
            draw();
        });
    }


    private void draw() {
        if (ratingOverTimeGraph == null || ratingOverGamesGraph == null) {
            return;
        }
        if (statistics == null) {
            ratingOverTimeGraph.clearChart();
            ratingOverGamesGraph.clearChart();
            return;
        }


        ratingOverGamesGraph.draw(getRatingOverGamesData(), getRatingOverGamesChartOptions());
        ratingOverTimeGraph.draw(getRatingOverTimeData(), getRatingOverTimeChartOptions());
    }

    private static LineChartOptions getRatingOverGamesChartOptions() {
        LineChartOptions options = LineChartOptions.create();
        options.setBackgroundColor("#f0f0f0");
        options.setFontName("Tahoma");
        options.setTitle("Rating over games");
        options.setHAxis(HAxis.create("Game"));
        options.setVAxis(VAxis.create("Rating"));
        options.setWidth(800);
        options.setHeight(400);
        return options;
    }

    private static LineChartOptions getRatingOverTimeChartOptions() {
        LineChartOptions options = LineChartOptions.create();
        options.setBackgroundColor("#f0f0f0");
        options.setFontName("Tahoma");
        options.setTitle("Rating over time");
        options.setHAxis(HAxis.create("Date"));
        options.setVAxis(VAxis.create("Rating"));
        options.setWidth(800);
        options.setHeight(400);
        return options;
    }

    private DataTable getRatingOverTimeData() {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.DATE, "Date");
        for (int i = 0; i < statistics.getRatingType().length; i++) {
            dataTable.addColumn(ColumnType.NUMBER, statistics.getRatingType()[i]);
        }
        dataTable.addRows(statistics.getRatingDates().length);
        for (int i = 0; i < statistics.getRatingDates().length; i++) {
            dataTable.setValue(i, 0, statistics.getRatingDates()[i]);
        }
        for (int col = 0; col < statistics.getRatingValues().length; col++) {
            for (int row = 0; row < statistics.getRatingValues()[col].length; row++) {
                dataTable.setValue(row, col + 1, statistics.getRatingValues()[col][row]);
            }
        }
        return dataTable;
    }

    private DataTable getRatingOverGamesData() {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.NUMBER, "Game");
        for (int i = 0; i < statistics.getRatingType().length; i++) {
            dataTable.addColumn(ColumnType.NUMBER, statistics.getRatingType()[i]);
        }
        dataTable.addRows(statistics.getRatingDates().length);
        for (int i = 0; i < statistics.getRatingDates().length; i++) {
            dataTable.setValue(i, 0, i);
        }
        for (int col = 0; col < statistics.getRatingValues().length; col++) {
            for (int row = 0; row < statistics.getRatingValues()[col].length; row++) {
                dataTable.setValue(row, col + 1, statistics.getRatingValues()[col][row]);
            }
        }
        return dataTable;
    }


    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionView");
        this.eventBus = eventBus;
        this.statistics = null;
        eventBinder.bindEventHandlers(this, eventBus);
        draw();
    }


    @EventHandler
    public void onCollectionStatistics(final CollectionStatisticsEvent event) {
        GWT.log("CollectionView: handle CollectionStatisticsEvent");
        statistics = event.getStatistics();
        collectionHeading.textContent(statistics.getDetails().getName());
        collectionDescription.textContent(statistics.getDetails().getDescription());
        draw();
    }
}
