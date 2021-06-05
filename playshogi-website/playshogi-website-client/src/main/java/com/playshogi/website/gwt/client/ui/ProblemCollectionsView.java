package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.*;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.RecordDetailsPlugin;
import org.dominokit.domino.ui.datatable.plugins.SimplePaginationPlugin;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Arrays;

import static org.jboss.elemento.Elements.*;

@Singleton
public class ProblemCollectionsView extends Composite {

    private static final int PAGE_SIZE = 15;

    interface MyEventBinder extends EventBinder<ProblemCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private final LocalListDataStore<ProblemCollectionDetails> localListDataStore;
    private final SimplePaginationPlugin<ProblemCollectionDetails> simplePaginationPlugin;
    private final DataTable<ProblemCollectionDetails> table;

    private EventBus eventBus;
    private ProblemCollectionDetails collectionDetails;

    @Inject
    public ProblemCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation,
                                  final AppPlaceHistoryMapper historyMapper) {
        this.placeController = placeController;
        GWT.log("Creating public collections view");

        TableConfig<ProblemCollectionDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        table = new DataTable<>(tableConfig, localListDataStore);

        table.style().setMaxWidth("1366px");

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(table.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    private HTMLElement getDetails(final ProblemCollectionDetails details) {
        return Button.createDefault(details.getDescription()).element();
    }

    private TableConfig<ProblemCollectionDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<ProblemCollectionDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (simplePaginationPlugin.getSimplePagination().activePage() - 1)))))
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("name", "Name")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getName()))
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("practice", "Practice")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new ProblemsPlace(cell.getRecord().getId(),
                                                    0));
                                    return Elements.a(href, "_blank").add(Button.createPrimary("Practice")).element();
                                }))
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getDifficulty(cell.getRecord()))
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("solved", "Solved")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getSolved(cell.getRecord()))
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("besttime", "Personal Best Time")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getPersonalBest(cell.getRecord()))
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("leaderboard", "Leaderboard")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getLeaderboard(cell.getRecord()))
                )

        ;
        return tableConfig;
    }

    private Node getLeaderboard(final ProblemCollectionDetails record) {
        if (record.getLeaderboardNames() == null || record.getLeaderboardScores() == null) {
            return TextNode.of("-");
        }
        HtmlContentBuilder<HTMLOListElement> ol = ol();
        String[] leaderboardNames = record.getLeaderboardNames();
        for (int i = 0; i < leaderboardNames.length; i++) {
            String leaderboardName = leaderboardNames[i];
            String score = record.getLeaderboardScores()[i];
            ol.add(li().textContent(leaderboardName + ": " + score));
        }
        return ol.element();
    }

    private Node getPersonalBest(final ProblemCollectionDetails record) {
        if (record.getUserHighScore() == null) {
            return TextNode.of("-");
        }

        return TextNode.of(record.getUserHighScore());
    }

    private Text getSolved(final ProblemCollectionDetails record) {
        if (record.getNumProblems() == 0) {
            return TextNode.of("-");
        }
        return TextNode.of(record.getUserSolved() + " / " + record.getNumProblems());
    }

    private Node getDifficulty(final ProblemCollectionDetails record) {
        if (record.getDifficulty() == 0) {
            return TextNode.of("-");
        }

        HtmlContentBuilder<HTMLDivElement> difficulty = div();
        for (int i = 1; i <= 5; i++) {
            if (i > record.getDifficulty()) {
                difficulty.add(Icons.ALL.star_border());
            } else {
                difficulty.add(Icons.ALL.star());
            }
        }
        return difficulty.element();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onListProblemCollections(final ListProblemCollectionsEvent event) {
        GWT.log("ProblemCollectionsView: handle ListProblemCollectionsEvent:\n" + event);

        ProblemCollectionDetails[] collections = event.getPublicCollections();
        localListDataStore.setData(Arrays.asList(collections));
        table.load();
    }
}
