package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.shared.models.GameDetails;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.*;
import jsinterop.base.Js;
import org.dominokit.domino.ui.badges.Badge;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.RecordDetailsPlugin;
import org.dominokit.domino.ui.datatable.plugins.SimplePaginationPlugin;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.FieldStyle;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.style.Style;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CollectionView extends Composite {


    private static final int PAGE_SIZE = 10;
    private final LocalListDataStore<GameDetails> localListDataStore;
    private final DataTable<GameDetails> table;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionHeading;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionDescription;
    private final SimplePaginationPlugin<GameDetails> simplePaginationPlugin;

    interface MyEventBinder extends EventBinder<CollectionView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    @Inject
    public CollectionView(final SessionInformation sessionInformation, final AppPlaceHistoryMapper historyMapper) {
        TableConfig<GameDetails> tableConfig = getTableConfig(historyMapper);
        //tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> Button.createDefault("more info etc").element())); TODO game details
        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        table = new DataTable<>(tableConfig, localListDataStore);

        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.css(Styles.padding_20);
        collectionHeading = Elements.h(2).textContent("Heading");
        collectionDescription = Elements.h(4).textContent("DSC");
        root.add(collectionHeading);
        root.add(collectionDescription);
        root.add(table.element());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(root.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
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
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE*(simplePaginationPlugin.getSimplePagination().activePage() - 1)))))
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
//                .addColumn( TODO SFEN MINIATURE
//                        ColumnConfig.<GameDetails>create("preview", "Preview")
//                                .styleCell(
//                                        element -> element.style.setProperty("vertical-align", "top"))
//                                .setCellRenderer(cell -> {
//                                    String sfen = cell.getRecord().getPreviewSfen();
//                                    if (sfen != null && !sfen.isEmpty()) {
//                                        BoardPreview boardPreview =
//                                                new BoardPreview(SfenConverter.fromSFEN(sfen), false,
//                                                        sessionInformation.getUserPreferences(), 0.5);
//                                        return Js.uncheckedCast(boardPreview.getElement());
//                                    } else {
//                                        return TextNode.empty();
//                                    }
//                                })
                .addColumn(
                        ColumnConfig.<GameDetails>create("view", "View")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                //.setCellRenderer(cell -> Elements.a(historyMapper.getToken(new ViewKifuPlace(cell.getRecord().getKifuId(), 0))).add(Button.createDefault("Show")).element())
                                .setCellRenderer(cell -> {
                                    String href = "#" + historyMapper.getToken(new ViewKifuPlace(cell.getRecord().getKifuId(), 0));
                                    return Elements.a(href, "_blank" ).add(Button.createPrimary("Show")).element();
                                })
                )
                .addColumn(
                        ColumnConfig.<GameDetails>create("edit", "Edit")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> Button.createPrimary("Edit").addClickListener(evt -> {
                                    ModalDialog defaultSizeModal = createModalDialog();
                                    defaultSizeModal.appendChild(CheckBox.create("Delete?"));
                                    defaultSizeModal.open();

                                }).element())
                )

                .addColumn(
                        ColumnConfig.<GameDetails>create("download", "Download")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> Button.createPrimary("Download").addClickListener(evt -> {
                                    //TODO implementation for Jean
                                }).element())
                        //TODO add pagination
                );
        return tableConfig;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
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

    @EventHandler
    public void onCollectionList(final ListCollectionGamesEvent event) {
        GWT.log("CollectionView: handle LessonsListEvent");
        //TODO add loading screen
        localListDataStore.setData(Arrays.asList(event.getDetails()));
        table.load();
        collectionHeading.textContent(event.getCollectionDetails().getName());
        collectionDescription.textContent(event.getCollectionDetails().getDescription());
    }

}
