package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.RequestAddKifuToCollectionEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestKifuDeletionEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.ProblemPlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.GamePreview;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.dominokit.domino.ui.datatable.plugins.SortDirection.ASC;
import static org.jboss.elemento.Elements.br;
import static org.jboss.elemento.Elements.h;

public class KifuTable {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    interface MyEventBinder extends EventBinder<KifuTable> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<KifuDetails> localListDataStore;
    private final DataTable<KifuDetails> table;
    private final AdvancedPaginationPlugin<KifuDetails> paginationPlugin;
    private final UserPreferences userPreferences;
    private EventBus eventBus;
    private GameCollectionDetails[] myGameCollections;
    private ProblemCollectionDetails[] myProblemCollections;
    private CustomSearchTableAction<KifuDetails> customSearchTableAction;


    public KifuTable(final AppPlaceHistoryMapper historyMapper, final UserPreferences userPreferences) {
        this.userPreferences = userPreferences;

        TableConfig<KifuDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        tableConfig.addPlugin(new SortPlugin<>());
        addFilterPlugin(tableConfig);

        localListDataStore = new LocalListDataStore<>();

        paginationPlugin = new AdvancedPaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(paginationPlugin);
        localListDataStore.setPagination(paginationPlugin.getPagination());
        localListDataStore.setRecordsSorter(getRecordsSorter());
        localListDataStore.setSearchFilter(new KifuSearchFilter());

        table = new DataTable<>(tableConfig, localListDataStore);
    }

    private void addFilterPlugin(final TableConfig<KifuDetails> tableConfig) {
        ColumnHeaderFilterPlugin<KifuDetails> filterPlugin = ColumnHeaderFilterPlugin.create();
        filterPlugin.addHeaderFilter("name", TextHeaderFilter.create());
        SelectHeaderFilter<KifuDetails> typeFilter = SelectHeaderFilter.create();
        typeFilter.appendChild(SelectOption.create("GAME", "Game"));
        typeFilter.appendChild(SelectOption.create("LESSON", "Lesson"));
        typeFilter.appendChild(SelectOption.create("PROBLEM", "Problem"));
        filterPlugin.addHeaderFilter("type", typeFilter);
        tableConfig.addPlugin(filterPlugin);

        filterPlugin.getFiltersRowElement().toggleDisplay(false);

        customSearchTableAction = new CustomSearchTableAction<>();
        tableConfig.addPlugin(
                new HeaderBarPlugin<KifuDetails>("")
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

    public DataTable<KifuDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private TableConfig<KifuDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<KifuDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<KifuDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (paginationPlugin.getPagination().activePage() - 1)))))
                .addColumn(
                        ColumnConfig.<KifuDetails>create("name", "Name")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getName()))
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<KifuDetails>create("type", "Type")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getType())))
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<KifuDetails>create("lastModified", "Last Modified")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getUpdateDate())))
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<KifuDetails>create("view", "View")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href = cell.getRecord().getType() != KifuDetails.KifuType.PROBLEM ?
                                            "#" + historyMapper.getToken(new ViewKifuPlace(cell.getRecord().getId(),
                                                    0)) :
                                            "#" + historyMapper.getToken(new ProblemPlace(cell.getRecord().getId()));
                                    return Elements.a(href).add(Button.createPrimary("View")).element();
                                })
                )
                .addColumn(
                        ColumnConfig.<KifuDetails>create("edit", "Edit")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String href =
                                            "#" + historyMapper.getToken(new KifuEditorPlace(cell.getRecord().getId()
                                                    , cell.getRecord().getType(), null));
                                    return Elements.a(href).add(Button.createPrimary(Icons.ALL.edit()).setContent(
                                            "Edit")).element();
                                })
                )
//                .addColumn(
//                        ColumnConfig.<KifuDetails>create("download", "Download")
//                                .styleCell(
//                                        element -> element.style.setProperty("vertical-align", "top"))
//                                .setCellRenderer(cell -> Button.createPrimary("Download").addClickListener(evt -> {
//                                    //TODO implementation for Jean
//                                }).element())
//                )
        ;
        return tableConfig;
    }

    private RecordsSorter<KifuDetails> getRecordsSorter() {
        return (sortBy, sortDirection) -> {
            Comparator<KifuDetails> comparator = (o1, o2) -> 0;
            if ("name".equals(sortBy)) {
                comparator =
                        Comparator.comparing(KifuDetails::getName,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            } else if ("type".equals(sortBy)) {
                comparator =
                        Comparator.comparing(KifuDetails::getType,
                                Comparator.nullsFirst(Comparator.naturalOrder()));
            } else if ("lastModified".equals(sortBy)) {
                GWT.log(sortDirection.toString());
                comparator =
                        Comparator.comparing(KifuDetails::getUpdateDate,
                                Comparator.nullsFirst(Comparator.naturalOrder()));
            }
            return sortDirection == ASC ? comparator : comparator.reversed();
        };
    }

    private HTMLElement getDetails(final KifuDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");
        rowElement.addColumn(Column.span4()
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of("Name: " + details.getName()))
                .appendChild(br())
        );

        HtmlContentBuilder<HTMLDivElement> previewDiv = Elements.div();

        kifuService.getKifuUsf(null, details.getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(final Throwable throwable) {
            }

            @Override
            public void onSuccess(final String usf) {
                GamePreview gamePreview = new GamePreview(userPreferences, UsfFormat.INSTANCE.readSingle(usf), 0.5);
                previewDiv.add(gamePreview.asElement());
            }
        });

        rowElement.addColumn(Column.span4().appendChild(previewDiv));

        rowElement.addColumn(Column.span4()
                .appendChild(Button.createDanger(Icons.ALL.delete_forever())
                        .setContent("Delete")
                        .addClickListener(evt -> confirmDeletion(details))
                        .style().setMarginRight("20px"))
                .appendChild(Button.createPrimary(Icons.ALL.playlist_add())
                        .setContent("Add to collection")
                        .addClickListener(evt -> addKifuToCollection(details))
                        .style().setMarginRight("20px"))
        );
        return rowElement.element();
    }

    public void setData(final List<KifuDetails> details) {
        if ((paginationPlugin.getPagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            paginationPlugin.getPagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    private void confirmDeletion(final KifuDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the kifu " + details.toString() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting kifu: " + details);
            eventBus.fireEvent(new RequestKifuDeletionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    private void addKifuToCollection(final KifuDetails details) {
        GWT.log("Add Kifu To Collection: " + details.getId());
        DialogBox dialog = createAddToCollectionDialogBox(details);
        dialog.center();
        dialog.show();
    }

    private DialogBox createAddToCollectionDialogBox(final KifuDetails details) {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Add Kifu to collection");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);


        ListBox listBox = createCollectionsDropdown(details.getType());
        dialogContents.add(listBox);

        FlowPanel buttonsPanel = new FlowPanel();
        com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Cancel",
                (ClickHandler) event -> dialogBox.hide());
        com.google.gwt.user.client.ui.Button saveButton = new com.google.gwt.user.client.ui.Button("Add",
                (ClickHandler) event -> {
                    addKifuToCollection(details, listBox.getSelectedValue());
                    dialogBox.hide();
                });
        buttonsPanel.add(closeButton);
        buttonsPanel.add(saveButton);
        dialogContents.add(buttonsPanel);

        dialogContents.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

    private ListBox createCollectionsDropdown(final KifuDetails.KifuType type) {
        ListBox list = new ListBox();
        if (type == KifuDetails.KifuType.PROBLEM) {
            for (ProblemCollectionDetails collection : myProblemCollections) {
                list.addItem(collection.getName(), collection.getId());
            }

        } else {
            for (GameCollectionDetails collection : myGameCollections) {
                list.addItem(collection.getName(), collection.getId());
            }
        }

        list.setVisibleItemCount(1);
        return list;
    }

    private void addKifuToCollection(final KifuDetails details, final String selectedValue) {
        GWT.log("Add Kifu " + details.getId() + " to Collection: " + selectedValue);
        eventBus.fireEvent(new RequestAddKifuToCollectionEvent(details.getId(), selectedValue, details.getType()));
    }

    @EventHandler
    public void onListKifusEvent(final ListKifusEvent event) {
        GWT.log("UserKifusView: handle ListKifusEvent");
        setData(Arrays.asList(event.getKifus()));
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle ListGameCollectionsEvent");
        myGameCollections = event.getMyCollections();
    }

    @EventHandler
    public void onListProblemCollectionsEvent(final ListProblemCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle ListProblemCollectionsEvent");
        myProblemCollections = event.getMyCollections();
    }
}
