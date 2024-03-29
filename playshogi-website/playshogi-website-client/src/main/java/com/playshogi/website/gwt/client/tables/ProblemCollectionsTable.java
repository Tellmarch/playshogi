package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.DeleteProblemCollectionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemCollectionPlace;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ProblemsRacePlace;
import com.playshogi.website.gwt.client.widget.collections.ProblemCollectionPropertiesForm;
import com.playshogi.website.gwt.client.widget.problems.TagsElement;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.*;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.*;
import org.dominokit.domino.ui.datatable.plugins.filter.header.SelectHeaderFilter;
import org.dominokit.domino.ui.datatable.plugins.filter.header.TextHeaderFilter;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.datatable.store.RecordsSorter;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Comparator;
import java.util.List;

import static org.dominokit.domino.ui.datatable.plugins.SortDirection.ASC;
import static org.jboss.elemento.Elements.*;

public class ProblemCollectionsTable {

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<ProblemCollectionDetails> localListDataStore;
    private final SimplePaginationPlugin<ProblemCollectionDetails> simplePaginationPlugin;
    private final DataTable<ProblemCollectionDetails> table;
    private final AppPlaceHistoryMapper historyMapper;
    private final boolean canEdit;
    private final boolean isAuthor;
    private EventBus eventBus;
    private final ProblemCollectionPropertiesForm problemCollectionProperties;
    private CustomSearchTableAction<ProblemCollectionDetails> customSearchTableAction;

    public ProblemCollectionsTable(final AppPlaceHistoryMapper historyMapper, boolean canEdit, boolean isAuthor) {
        this.historyMapper = historyMapper;
        this.canEdit = canEdit;
        this.isAuthor = isAuthor;
        TableConfig<ProblemCollectionDetails> tableConfig = getTableConfig();
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        tableConfig.addPlugin(new SortPlugin<>());
        addFilterPlugin(tableConfig);

        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        localListDataStore.setRecordsSorter(getRecordsSorter());
        localListDataStore.setSearchFilter(new ProblemCollectionSearchFilter());

        table = new DataTable<>(tableConfig, localListDataStore);
        table.style().setMaxWidth("1366px");
        problemCollectionProperties = new ProblemCollectionPropertiesForm();
    }

    public DataTable<ProblemCollectionDetails> getTable() {
        return table;
    }

    private void addFilterPlugin(final TableConfig<ProblemCollectionDetails> tableConfig) {
        ColumnHeaderFilterPlugin<ProblemCollectionDetails> filterPlugin = ColumnHeaderFilterPlugin.create();
        filterPlugin.addHeaderFilter("author", TextHeaderFilter.create());
        filterPlugin.addHeaderFilter("name", TextHeaderFilter.create());
        SelectHeaderFilter<ProblemCollectionDetails> difficultyFilter = SelectHeaderFilter.create();
        difficultyFilter.appendChild(SelectOption.create("1", "★☆☆☆☆"));
        difficultyFilter.appendChild(SelectOption.create("2", "★★☆☆☆"));
        difficultyFilter.appendChild(SelectOption.create("3", "★★★☆☆"));
        difficultyFilter.appendChild(SelectOption.create("4", "★★★★☆"));
        difficultyFilter.appendChild(SelectOption.create("5", "★★★★★"));
        filterPlugin.addHeaderFilter("difficulty", difficultyFilter);
        SelectHeaderFilter<ProblemCollectionDetails> visibilityFilter = SelectHeaderFilter.create();
        visibilityFilter.appendChild(SelectOption.create("public", "Public"));
        visibilityFilter.appendChild(SelectOption.create("unlisted", "Unlisted"));
        filterPlugin.addHeaderFilter("visibility", visibilityFilter);
        tableConfig.addPlugin(filterPlugin);

        filterPlugin.getFiltersRowElement().toggleDisplay(false);

        customSearchTableAction = new CustomSearchTableAction<>();
        tableConfig.addPlugin(
                new HeaderBarPlugin<ProblemCollectionDetails>("")
                        .addActionElement(new HeaderBarPlugin.ClearSearch<>())
                        .addActionElement(customSearchTableAction)
                        .addActionElement(
                                dataTable ->
                                        Icons.ALL.filter_menu_mdi().size18().setTooltip("Filters...")
                                                .clickable()
                                                .addClickListener(
                                                        evt ->
                                                                filterPlugin
                                                                        .getFiltersRowElement()
                                                                        .toggleDisplay())
                                                .element()));
    }

    private HTMLElement getDetails(final ProblemCollectionDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");

        rowElement.addColumn(Column.span4()
                .appendChild(new TagsElement(details.getTags()).asElement())
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of(details.getDescription()))
        );
        String href = "#" + historyMapper.getToken(new ProblemsRacePlace(details.getId(), 0));
        rowElement.addColumn(Column.span3().appendChild(Elements.a(href).add(Button.createSuccess(Icons.ALL.flag_checkered_mdi()).setContent("Race!"))));
        if (canEdit) {
            rowElement.addColumn(Column.span2().appendChild(Button.createPrimary("Edit properties")
                    .addClickListener(evt -> problemCollectionProperties.showInPopup(details))));
            rowElement.addColumn(Column.span2().appendChild(Button.createDanger(Icons.ALL.delete_forever())
                    .setContent("Delete collection")
                    .addClickListener(evt -> confirmCollectionDeletion(details))));
        }
        return rowElement.element();
    }

    private TableConfig<ProblemCollectionDetails> getTableConfig() {
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
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("practice", "Practice")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new ProblemsPlace(cell.getRecord().getId(),
                                                    0));
                                    return Elements.a(href).add(Button.createSuccess(Icons.ALL.timer()).setContent(
                                            "Practice / Speedrun")).element();
                                }))
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getDifficulty(cell.getRecord()))
                                .setSortable(true)
                );
        if (!isAuthor) {
            tableConfig.addColumn(
                    ColumnConfig.<ProblemCollectionDetails>create("author", "Author")
                            .styleCell(
                                    element -> element.style.setProperty("vertical-align", "top"))
                            .setCellRenderer(cell -> TextNode.of(cell.getRecord().getAuthor()))
                            .setSortable(true)
            );
        }
        if (isAuthor || canEdit) {
            tableConfig.addColumn(
                    ColumnConfig.<ProblemCollectionDetails>create("visibility", "Visibility")
                            .styleCell(
                                    element -> element.style.setProperty("vertical-align", "top"))
                            .setCellRenderer(cell -> TextNode.of(cell.getRecord().getVisibility()))
            );
        }
        tableConfig
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
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("leaderboard", "Leaderboard")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getLeaderboard(cell.getRecord()))
                );
        if (isAuthor) {
            tableConfig
                    .addColumn(
                            ColumnConfig.<ProblemCollectionDetails>create("open", "Open Collection")
                                    .styleCell(
                                            element -> element.style.setProperty("vertical-align", "top"))
                                    .setCellRenderer(cell -> {
                                        String href =
                                                "#" + historyMapper.getToken(new ProblemCollectionPlace(cell.getRecord().getId()));
                                        return Elements.a(href).add(Button.createPrimary(
                                                "Open")).element();
                                    }));
        }
        return tableConfig;
    }

    private RecordsSorter<ProblemCollectionDetails> getRecordsSorter() {
        return (sortBy, sortDirection) -> {
            Comparator<ProblemCollectionDetails> comparator = (o1, o2) -> 0;
            if ("difficulty".equals(sortBy)) {
                comparator = Comparator.comparingInt(ProblemCollectionDetails::getDifficulty);
            } else if ("name".equals(sortBy)) {
                comparator =
                        Comparator.comparing(ProblemCollectionDetails::getName,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            } else if ("author".equals(sortBy)) {
                comparator =
                        Comparator.comparing(ProblemCollectionDetails::getAuthor,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            } else if ("besttime".equals(sortBy)) {
                comparator =
                        Comparator.comparing(ProblemCollectionDetails::getUserHighScore,
                                Comparator.nullsFirst(Comparator.naturalOrder()));
            }
            return sortDirection == ASC ? comparator : comparator.reversed();
        };
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

    public void setData(final List<ProblemCollectionDetails> details) {
        if ((simplePaginationPlugin.getSimplePagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            simplePaginationPlugin.getSimplePagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
        customSearchTableAction.doSearch();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        problemCollectionProperties.activate(eventBus);
    }

    private void confirmCollectionDeletion(final ProblemCollectionDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the collection " + details.getName() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting collection: " + details);
            eventBus.fireEvent(new DeleteProblemCollectionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    public void addSearch(final String search) {
        customSearchTableAction.setSearch(search);
    }
}
