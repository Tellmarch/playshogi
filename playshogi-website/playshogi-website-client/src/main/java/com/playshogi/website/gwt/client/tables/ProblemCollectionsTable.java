package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.DeleteProblemCollectionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemCollectionPlace;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.collections.ProblemCollectionPropertiesForm;
import com.playshogi.website.gwt.client.widget.problems.TagsElement;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.*;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.RecordDetailsPlugin;
import org.dominokit.domino.ui.datatable.plugins.SimplePaginationPlugin;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.List;

import static org.jboss.elemento.Elements.*;

public class ProblemCollectionsTable {

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<ProblemCollectionDetails> localListDataStore;
    private final SimplePaginationPlugin<ProblemCollectionDetails> simplePaginationPlugin;
    private final DataTable<ProblemCollectionDetails> table;
    private final boolean canEdit;
    private final boolean isAuthor;
    private EventBus eventBus;
    private ProblemCollectionPropertiesForm problemCollectionProperties;

    public ProblemCollectionsTable(final AppPlaceHistoryMapper historyMapper, boolean canEdit, boolean isAuthor) {
        this.canEdit = canEdit;
        this.isAuthor = isAuthor;
        TableConfig<ProblemCollectionDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        table = new DataTable<>(tableConfig, localListDataStore);
        table.style().setMaxWidth("1366px");
        problemCollectionProperties = new ProblemCollectionPropertiesForm();
    }

    public DataTable<ProblemCollectionDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private HTMLElement getDetails(final ProblemCollectionDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");

        rowElement.addColumn(Column.span4()
                .appendChild(new TagsElement(details.getTags()).asElement())
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of(details.getDescription()))
        );
        if (canEdit) {
            rowElement.addColumn(Column.span4().appendChild(Button.createPrimary("Edit properties")
                    .addClickListener(evt -> problemCollectionProperties.showInPopup(details))));
            rowElement.addColumn(Column.span4().appendChild(Button.createDanger("Delete collection")
                    .addClickListener(evt -> confirmCollectionDeletion(details))));
        }
        return rowElement.element();
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
                                    return Elements.a(href).add(Button.createSuccess(Icons.ALL.timer()).setContent(
                                            "Practice / Speedrun")).element();
                                }))
                .addColumn(
                        ColumnConfig.<ProblemCollectionDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getDifficulty(cell.getRecord()))
                );
        if (!isAuthor) {
            tableConfig.addColumn(
                    ColumnConfig.<ProblemCollectionDetails>create("author", "Author")
                            .styleCell(
                                    element -> element.style.setProperty("vertical-align", "top"))
                            .setCellRenderer(cell -> TextNode.of(cell.getRecord().getAuthor()))
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
}
