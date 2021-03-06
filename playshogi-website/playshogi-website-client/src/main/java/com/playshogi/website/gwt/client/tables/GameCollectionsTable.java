package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.ConvertGameCollectionEvent;
import com.playshogi.website.gwt.client.events.collections.DeleteGameCollectionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.GameCollectionPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.kifu.CollectionPropertiesPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import elemental2.dom.HTMLElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.RecordDetailsPlugin;
import org.dominokit.domino.ui.datatable.plugins.SimplePaginationPlugin;
import org.dominokit.domino.ui.datatable.plugins.SortPlugin;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.datatable.store.RecordsSorter;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;

import java.util.Comparator;
import java.util.List;

import static org.dominokit.domino.ui.datatable.plugins.SortDirection.ASC;
import static org.jboss.elemento.Elements.h;

public class GameCollectionsTable {

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<GameCollectionDetails> localListDataStore;
    private final SimplePaginationPlugin<GameCollectionDetails> simplePaginationPlugin;
    private final DataTable<GameCollectionDetails> table;
    private final boolean canEdit;
    private final boolean isAuthor;
    private final CollectionPropertiesPanel collectionPropertiesPanel = new CollectionPropertiesPanel();
    private EventBus eventBus;

    public GameCollectionsTable(final AppPlaceHistoryMapper historyMapper, boolean canEdit, boolean isAuthor) {
        this.canEdit = canEdit;
        this.isAuthor = isAuthor;
        TableConfig<GameCollectionDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        tableConfig.addPlugin(new SortPlugin<>());
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        localListDataStore.setRecordsSorter(getRecordsSorter());
        table = new DataTable<>(tableConfig, localListDataStore);
        table.style().setMaxWidth("1366px");
    }

    public DataTable<GameCollectionDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private HTMLElement getDetails(final GameCollectionDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");
        rowElement.addColumn(Column.span4()
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of(details.getDescription())));
        if (canEdit) {
            rowElement.addColumn(Column.span4().appendChild(Button.createPrimary("Edit properties")
                    .addClickListener(evt -> collectionPropertiesPanel.showInUpdateDialog(details))));
            rowElement.addColumn(Column.span4().appendChild(Button.createDanger(Icons.ALL.delete_forever()).setContent("Delete collection")
                    .addClickListener(evt -> confirmCollectionDeletion(details)).style().setMarginRight("1em"))
                    .appendChild(Button.createDanger("Convert to Problem Collection")
                            .addClickListener(evt -> confirmCollectionConversion(details))));
        }
        return rowElement.element();
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
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("numGames", "Number of Games")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getNumGames())))
                                .setSortable(true)
                );
        if (!isAuthor) {
            tableConfig.addColumn(
                    ColumnConfig.<GameCollectionDetails>create("author", "Author")
                            .styleCell(
                                    element -> element.style.setProperty("vertical-align", "top"))
                            .setCellRenderer(cell -> TextNode.of(cell.getRecord().getAuthor()))
                            .setSortable(true)
            );
        }
        if (isAuthor || canEdit) {
            tableConfig.addColumn(
                    ColumnConfig.<GameCollectionDetails>create("visibility", "Visibility")
                            .styleCell(
                                    element -> element.style.setProperty("vertical-align", "top"))
                            .setCellRenderer(cell -> TextNode.of(cell.getRecord().getVisibility()))
                            .setSortable(true)
            );
        }
        tableConfig
                .addColumn(
                        ColumnConfig.<GameCollectionDetails>create("open", "Open Collection")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new GameCollectionPlace(cell.getRecord().getId()));
                                    return Elements.a(href).add(Button.createPrimary(
                                            "Open")).element();
                                }));
        return tableConfig;
    }

    private RecordsSorter<GameCollectionDetails> getRecordsSorter() {
        return (sortBy, sortDirection) -> {
            Comparator<GameCollectionDetails> comparator = (o1, o2) -> 0;
            if ("numGames".equals(sortBy)) {
                comparator = Comparator.comparingInt(GameCollectionDetails::getNumGames);
            } else if ("name".equals(sortBy)) {
                comparator =
                        Comparator.comparing(GameCollectionDetails::getName,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            } else if ("visibility".equals(sortBy)) {
                comparator = Comparator.comparing(GameCollectionDetails::getVisibility);
            } else if ("author".equals(sortBy)) {
                comparator =
                        Comparator.comparing(GameCollectionDetails::getAuthor,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            }
            return sortDirection == ASC ? comparator : comparator.reversed();
        };
    }

    public void setData(final List<GameCollectionDetails> details) {
        if ((simplePaginationPlugin.getSimplePagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            simplePaginationPlugin.getSimplePagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        collectionPropertiesPanel.activate(eventBus);
    }

    private void confirmCollectionDeletion(final GameCollectionDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the collection " + details.getName() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting collection: " + details);
            eventBus.fireEvent(new DeleteGameCollectionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    private void confirmCollectionConversion(final GameCollectionDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to convert the collection " + details.getName() + "?");
        if (confirm) {
            eventBus.fireEvent(new ConvertGameCollectionEvent(details.getId()));
        }
    }
}
