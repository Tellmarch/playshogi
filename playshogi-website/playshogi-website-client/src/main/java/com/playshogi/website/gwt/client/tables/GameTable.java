package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.collections.RemoveGameFromCollectionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.GameDetails;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
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
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;

import java.util.List;

import static org.jboss.elemento.Elements.br;
import static org.jboss.elemento.Elements.h;

public class GameTable {
    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<GameDetails> localListDataStore;
    private final DataTable<GameDetails> table;
    private final SimplePaginationPlugin<GameDetails> simplePaginationPlugin;
    private final UserPreferences userPreferences;
    private final boolean withEditOptions;
    private EventBus eventBus;

    public GameTable(final AppPlaceHistoryMapper historyMapper, final UserPreferences userPreferences,
                     final boolean withEditOptions) {
        this.userPreferences = userPreferences;
        this.withEditOptions = withEditOptions;

        TableConfig<GameDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        table = new DataTable<>(tableConfig, localListDataStore);
    }

    public DataTable<GameDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private TableConfig<GameDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<GameDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<GameDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (simplePaginationPlugin.getSimplePagination().activePage() - 1)))))
                .addColumn(
                        ColumnConfig.<GameDetails>create("sente", "Sente") //TODO show winner
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getSente()))
                )
                .addColumn(
                        ColumnConfig.<GameDetails>create("gote", "Gote")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getGote()))
                )
                .addColumn(
                        ColumnConfig.<GameDetails>create("date", "Date")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getDate()))
                )
                .addColumn(
                        ColumnConfig.<GameDetails>create("venue", "Venue")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getVenue()))
                )
                .addColumn(
                        ColumnConfig.<GameDetails>create("view", "View")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                //.setCellRenderer(cell -> Elements.a(historyMapper.getToken(new ViewKifuPlace(cell
                                // .getRecord().getKifuId(), 0))).add(Button.createDefault("Show")).element())
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new ViewKifuPlace(cell.getRecord().getKifuId(), 0));
                                    return Elements.a(href).add(Button.createPrimary("Show")).element();
                                })
                )
//                .addColumn(
//                        ColumnConfig.<GameDetails>create("edit", "Edit")
//                                .styleCell(
//                                        element -> element.style.setProperty("vertical-align", "top"))
//                                .setCellRenderer(cell -> Button.createPrimary("Edit").addClickListener(evt -> {
//                                    ModalDialog defaultSizeModal = createModalDialog();
//                                    defaultSizeModal.appendChild(CheckBox.create("Delete?"));
//                                    defaultSizeModal.open();
//
//                                }).element())
//                )
//                .addColumn(
//                        ColumnConfig.<GameDetails>create("download", "Download")
//                                .styleCell(
//                                        element -> element.style.setProperty("vertical-align", "top"))
//                                .setCellRenderer(cell -> Button.createPrimary("Download").addClickListener(evt -> {
//                                    //TODO implementation for Jean
//                                }).element())
//                )
        ;
        return tableConfig;
    }

    private HTMLElement getDetails(final GameDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");
        rowElement.addColumn(Column.span4()
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of("Black: " + details.getSente()))
                .appendChild(br())
                .appendChild(TextNode.of("White: " + details.getGote()))
                .appendChild(br())
                .appendChild(TextNode.of("Venue: " + details.getVenue()))
                .appendChild(br())
                .appendChild(TextNode.of("Date: " + details.getDate()))
                .appendChild(br())
        );

        BoardPreview boardPreview = new BoardPreview(SfenConverter.fromSFEN(SfenConverter.INITIAL_POSITION_SFEN),
                false, userPreferences, 0.5);
        rowElement.addColumn(Column.span4().appendChild(boardPreview.asElement()));

        if (withEditOptions) {
            rowElement.addColumn(Column.span4()
                            .appendChild(Button.createDanger("Remove from collection")
                                    .addClickListener(evt -> confirmDeletion(details))
                                    .style().setMarginRight("20px"))
//                    .appendChild(Button.createDanger("Delete")
//                            .addClickListener(evt -> confirmDeletion(details)))
            );
        }
        return rowElement.element();
    }

    public void setData(final List<GameDetails> details) {
        if ((simplePaginationPlugin.getSimplePagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            simplePaginationPlugin.getSimplePagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private ModalDialog createModalDialog() {
        ModalDialog modal = ModalDialog.create("Kifu Edit").setAutoClose(true);
        modal.appendChild(TextNode.of("SAMPLE_CONTENT"));
        Button closeButton = Button.create("CLOSE").linkify();
        Button saveButton = Button.create("SAVE").linkify();
        EventListener closeModalListener = evt -> modal.close();
        closeButton.addClickListener(closeModalListener);
        saveButton.addClickListener(closeModalListener);
        modal.appendFooterChild(saveButton);
        modal.appendFooterChild(closeButton);
        return modal;
    }

    private void confirmDeletion(final GameDetails details) {
        boolean confirm =
                Window.confirm("Are you sure you want to remove the game " + details.getSente() + " vs "
                        + details.getGote() + "?\nThis is not revertible.");
        if (confirm) {
            GWT.log("Deleting game: " + details);
            eventBus.fireEvent(new RemoveGameFromCollectionEvent(details.getId(), null));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }
}
