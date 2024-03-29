package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.collections.MoveProblemDownEvent;
import com.playshogi.website.gwt.client.events.collections.MoveProblemUpEvent;
import com.playshogi.website.gwt.client.events.collections.RemoveProblemFromCollectionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.ProblemPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import org.dominokit.domino.ui.badges.Badge;
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
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.List;

import static org.jboss.elemento.Elements.*;

public class ProblemTable {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<ProblemDetails> localListDataStore;
    private final DataTable<ProblemDetails> table;
    private final SimplePaginationPlugin<ProblemDetails> simplePaginationPlugin;
    private final UserPreferences userPreferences;
    private final boolean withEditOptions;
    private EventBus eventBus;
    private boolean isAuthor;
    private ProblemCollectionDetails collectionDetails;

    public ProblemTable(final AppPlaceHistoryMapper historyMapper, final UserPreferences userPreferences,
                        final boolean withEditOptions) {
        this.userPreferences = userPreferences;
        this.withEditOptions = withEditOptions;

        TableConfig<ProblemDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        table = new DataTable<>(tableConfig, localListDataStore);
    }

    public DataTable<ProblemDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private TableConfig<ProblemDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<ProblemDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getIndexInCollection()))))
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("updown", "Order")
                                .setCellRenderer(cell -> getUpDownIcons(cell.getRecord()))
                )
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("nummoves", "Number of Moves")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getNumMoves())))
                )
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("tags", "Tags")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(
                                        cell -> {
                                            HtmlContentBuilder<HTMLElement> span = Elements.span();

                                            String[] tags = cell.getRecord().getTags();
                                            if (tags != null) {
                                                for (String tag : tags) {
                                                    span.add(Badge.create(tag)
                                                            .setBackground(ColorScheme.GREEN.color())
                                                            .style().setMarginRight("1em")
                                                            .element());
                                                }
                                            }
                                            return span.element();
                                        })
                )
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("view", "View")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                //.setCellRenderer(cell -> Elements.a(historyMapper.getToken(new ViewKifuPlace(cell
                                // .getRecord().getKifuId(), 0))).add(Button.createDefault("Show")).element())
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new ProblemPlace(cell.getRecord().getKifuId()));
                                    return Elements.a(href).add(Button.createPrimary(Icons.ALL.play_circle_filled()).setContent("Show")).element();
                                })
                )
                .addColumn(
                        ColumnConfig.<ProblemDetails>create("edit", "Edit")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(
                                                    new KifuEditorPlace(cell.getRecord().getKifuId(),
                                                            KifuDetails.KifuType.PROBLEM,
                                                            collectionDetails.getId()));
                                    return Elements.a(href).add(Button.createPrimary(Icons.ALL.edit()).setContent(
                                            "Edit")).element();
                                })
                )
//                .addColumn(
//                        ColumnConfig.<ProblemDetails>create("download", "Download")
//                                .styleCell(
//                                        element -> element.style.setProperty("vertical-align", "top"))
//                                .setCellRenderer(cell -> Button.createPrimary("Download").addClickListener(evt -> {
//                                }).element())
//                )
        ;
        return tableConfig;
    }

    private Node getUpDownIcons(final ProblemDetails record) {
        HtmlContentBuilder<HTMLDivElement> difficulty = div();
        difficulty.add(Button.createPrimary(Icons.ALL.transfer_up_mdi())
                .addClickListener(e -> eventBus.fireEvent(new MoveProblemUpEvent(record, collectionDetails))));
        difficulty.add(Button.createPrimary(Icons.ALL.transfer_down_mdi())
                .addClickListener(e -> eventBus.fireEvent(new MoveProblemDownEvent(record, collectionDetails))));
        return difficulty.element();
    }

    private HTMLElement getDetails(final ProblemDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");
        rowElement.addColumn(Column.span4()
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of("Moves: " + details.getNumMoves()))
                .appendChild(br())
        );

        HtmlContentBuilder<HTMLDivElement> previewDiv = Elements.div();

        kifuService.getKifuUsf(null, details.getKifuId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(final Throwable throwable) {
            }

            @Override
            public void onSuccess(final String usf) {
                BoardPreview boardPreview = new BoardPreview(
                        UsfFormat.INSTANCE.readSingle(usf).getGameTree().getInitialPosition(),
                        false, userPreferences, 0.5);
                previewDiv.add(boardPreview.asElement());
            }
        });

        rowElement.addColumn(Column.span4().appendChild(previewDiv));

        if (withEditOptions || isAuthor) {
            rowElement.addColumn(Column.span4()
                            .appendChild(Button.createDanger(Icons.ALL.delete_forever()).setContent("Remove from " +
                                    "collection")
                                    .addClickListener(evt -> confirmDeletion(details))
                                    .style().setMarginRight("20px"))
//                    .appendChild(Button.createDanger("Delete")
//                            .addClickListener(evt -> confirmDeletion(details)))
            );
        }
        return rowElement.element();
    }

    public void setData(final List<ProblemDetails> details, final boolean isAuthor,
                        final ProblemCollectionDetails collectionDetails) {
        this.isAuthor = isAuthor;
        this.collectionDetails = collectionDetails;
        if ((simplePaginationPlugin.getSimplePagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            simplePaginationPlugin.getSimplePagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }


    private void confirmDeletion(final ProblemDetails details) {
        boolean confirm =
                Window.confirm("Are you sure you want to remove the problem?\nThis is not revertible.");
        if (confirm) {
            GWT.log("Deleting game: " + details);
            eventBus.fireEvent(new RemoveProblemFromCollectionEvent(details.getId(), null));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }
}
