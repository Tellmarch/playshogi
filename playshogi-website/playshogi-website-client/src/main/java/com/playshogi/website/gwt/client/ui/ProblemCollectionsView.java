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
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
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
    private final LocalListDataStore<GameCollectionDetails> localListDataStore;
    private final SimplePaginationPlugin<GameCollectionDetails> simplePaginationPlugin;
    private final DataTable<GameCollectionDetails> table;

    private EventBus eventBus;
    private GameCollectionDetails collectionDetails;

    @Inject
    public ProblemCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation,
                                  final AppPlaceHistoryMapper historyMapper) {
        this.placeController = placeController;
        GWT.log("Creating public collections view");

        TableConfig<GameCollectionDetails> tableConfig = getTableConfig(historyMapper);
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

    private HTMLElement getDetails(final GameCollectionDetails details) {
        return Button.createDefault(details.getDescription()).element();
    }

    private TableConfig<GameCollectionDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<GameCollectionDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (simplePaginationPlugin.getSimplePagination().activePage() - 1)))))
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("name", "Name")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getName()))
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getDifficulty(cell.getRecord()))
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("solved", "Solved")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of("10 / 40"))
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("besttime", "Personal Best Time")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of("5'42"))
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("besttime", "Leaderboard")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> ol().add(li().textContent("aaa: 10"))
                                        .add(li().textContent("bbb: 10"))
                                        .add(li().textContent("ccc: 10")).element())
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("view", "View")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new ProblemsPlace(cell.getRecord().getId(),
                                                    0));
                                    return Elements.a(href, "_blank").add(Button.createPrimary("Show")).element();
                                })
                );
        return tableConfig;
    }

    private Node getDifficulty(final GameCollectionDetails record) {
        HtmlContentBuilder<HTMLDivElement> difficulty = div();
        for (int i = 1; i <= 5; i++) {
            if (i > 3) {
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
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("ProblemCollectionsView: handle ListGameCollectionsEvent:\n" + event);

        GameCollectionDetails[] collections = event.getPublicCollections();
        localListDataStore.setData(Arrays.asList(collections));
        table.load();
    }
}
