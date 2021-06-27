package com.playshogi.website.gwt.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ViewLessonPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.GamePreview;
import com.playshogi.website.gwt.client.widget.collections.LessonPropertiesForm;
import com.playshogi.website.gwt.client.widget.problems.TagsElement;
import com.playshogi.website.gwt.shared.models.LessonDetails;
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
import org.dominokit.domino.ui.datatable.plugins.*;
import org.dominokit.domino.ui.datatable.plugins.filter.header.BooleanHeaderFilter;
import org.dominokit.domino.ui.datatable.plugins.filter.header.SelectHeaderFilter;
import org.dominokit.domino.ui.datatable.plugins.filter.header.TextHeaderFilter;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.datatable.store.RecordsSorter;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.style.Style;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Comparator;
import java.util.List;

import static org.dominokit.domino.ui.datatable.plugins.SortDirection.ASC;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.h;

public class LessonsTable {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private static final int PAGE_SIZE = 15;

    private final LocalListDataStore<LessonDetails> localListDataStore;
    private final SimplePaginationPlugin<LessonDetails> simplePaginationPlugin;
    private final DataTable<LessonDetails> table;
    private final LessonPropertiesForm lessonPropertiesForm;
    private final SessionInformation sessionInformation;
    private CustomSearchTableAction<LessonDetails> customSearchTableAction;
    private EventBus eventBus;

    public LessonsTable(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation) {
        this.sessionInformation = sessionInformation;
        TableConfig<LessonDetails> tableConfig = getTableConfig(historyMapper);
        tableConfig.addPlugin(new RecordDetailsPlugin<>(cell -> getDetails(cell.getRecord())));
        tableConfig.addPlugin(new SortPlugin<>());
        addFilterPlugin(tableConfig);

        localListDataStore = new LocalListDataStore<>();

        simplePaginationPlugin = new SimplePaginationPlugin<>(PAGE_SIZE);
        tableConfig.addPlugin(simplePaginationPlugin);
        localListDataStore.setPagination(simplePaginationPlugin.getSimplePagination());
        localListDataStore.setRecordsSorter(getRecordsSorter());
        localListDataStore.setSearchFilter(new LessonsSearchFilter());

        table = new DataTable<>(tableConfig, localListDataStore);
        lessonPropertiesForm = new LessonPropertiesForm();
    }

    public DataTable<LessonDetails> getTable() {
        return table;
    }

    public Widget getAsWidget() {
        return new ElementWidget(table.element());
    }

    private void addFilterPlugin(final TableConfig<LessonDetails> tableConfig) {
        ColumnHeaderFilterPlugin<LessonDetails> filterPlugin = ColumnHeaderFilterPlugin.create();
        filterPlugin.addHeaderFilter("author", TextHeaderFilter.create());
        filterPlugin.addHeaderFilter("title", TextHeaderFilter.create());
        SelectHeaderFilter<LessonDetails> difficultyFilter = SelectHeaderFilter.create();
        difficultyFilter.appendChild(SelectOption.create("1", "★☆☆☆☆"));
        difficultyFilter.appendChild(SelectOption.create("2", "★★☆☆☆"));
        difficultyFilter.appendChild(SelectOption.create("3", "★★★☆☆"));
        difficultyFilter.appendChild(SelectOption.create("4", "★★★★☆"));
        difficultyFilter.appendChild(SelectOption.create("5", "★★★★★"));
        filterPlugin.addHeaderFilter("difficulty", difficultyFilter);
        filterPlugin.addHeaderFilter("status", BooleanHeaderFilter.create());
        tableConfig.addPlugin(filterPlugin);

        filterPlugin.getFiltersRowElement().toggleDisplay(false);

        customSearchTableAction = new CustomSearchTableAction<>();
        tableConfig.addPlugin(
                new HeaderBarPlugin<LessonDetails>("")
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

    private HTMLElement getDetails(final LessonDetails details) {
        Row<Row_12> rowElement = Row.create();
        rowElement.style().setMarginLeft("40px").setMarginRight("40px").setMarginTop("10px").setMarginBottom("10px");

        rowElement.addColumn(Column.span4()
                .appendChild(new TagsElement(details.getTags()).asElement())
                .appendChild(h(5).add("Description:"))
                .appendChild(TextNode.of(details.getDescription()))
        );
        rowElement.addColumn(Column.span4().appendChild(Button.createPrimary("Edit properties")
                .addClickListener(evt -> lessonPropertiesForm.showInPopup(details)))
                .appendChild(Elements.br())
                .appendChild(Button.createPrimary("Add child")
                        .addClickListener(evt -> {
                            LessonDetails child = new LessonDetails();
                            child.setParentLessonId(details.getLessonId());
                            child.setTags(details.getTags());
                            lessonPropertiesForm.showInPopup(child);
                        }).style().setMarginTop("1em"))
        );

        if (details.getKifuId() != null) {
            HtmlContentBuilder<HTMLDivElement> previewDiv = Elements.div();

            kifuService.getKifuUsf(null, details.getKifuId(), new AsyncCallback<String>() {
                @Override
                public void onFailure(final Throwable throwable) {
                }

                @Override
                public void onSuccess(final String usf) {
                    GamePreview gamePreview = new GamePreview(sessionInformation.getUserPreferences(),
                            UsfFormat.INSTANCE.readSingle(usf), 0.5);
                    previewDiv.add(gamePreview.asElement());
                    previewDiv.add(Button.createPrimary("Set as preview")
                            .addClickListener(evt -> {
                                details.setPreviewSfen(SfenConverter.toSFEN(gamePreview.getCurrentPosition()));
                                lessonPropertiesForm.showInPopup(details);
                            }).style().setMarginTop("1em"));
                }
            });

            rowElement.addColumn(Column.span4().appendChild(previewDiv));
        } else {
            rowElement.addColumn(Column.span4());
        }


//        rowElement.addColumn(Column.span4().appendChild(Button.createDanger(Icons.ALL.delete_forever())
//                .setContent("Delete lesson")
//                .addClickListener(evt -> confirmLessonDeletion(details))));
        return rowElement.element();
    }

    private TableConfig<LessonDetails> getTableConfig(final AppPlaceHistoryMapper historyMapper) {
        TableConfig<LessonDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<LessonDetails>create("num", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (simplePaginationPlugin.getSimplePagination().activePage() - 1)))))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("id", "id")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(cell.getRecord().getLessonId())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("status", "Status")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("center")
                                .setCellRenderer(
                                        cell -> {
                                            if (!cell.getRecord().isHidden()) {
                                                return Style.of(Icons.ALL.check_circle())
                                                        .setColor(Color.GREEN_DARKEN_3.getHex())
                                                        .element();
                                            } else {
                                                return Style.of(Icons.ALL.highlight_off())
                                                        .setColor(Color.RED_DARKEN_3.getHex())
                                                        .element();
                                            }
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("title", "Title")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getTitle())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("description", "Description")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getDescription())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("parent", "Parent")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getParentLessonId())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("kifu", "Kifu")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getKifuId())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("problems", "Problem Collection")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getProblemCollectionId())))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> getDifficulty(cell.getRecord()))
                                .setSortable(true))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("author", "Author")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getAuthor()))
                                .setSortable(true)
                )
                .addColumn(
                        ColumnConfig.<LessonDetails>create("tags", "Tags")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(
                                        cell -> {
                                            HtmlContentBuilder<HTMLElement> span = Elements.span();
                                            for (String tag : cell.getRecord().getTags()) {
                                                span.add(Badge.create(tag)
                                                        .setBackground(ColorScheme.GREEN.color())
                                                        .style().setMarginRight("1em")
                                                        .element());
                                            }
                                            return span.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("edit", "Edit")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell ->
                                        Button.createPrimary("Edit").addClickListener(evt ->
                                                lessonPropertiesForm.showInPopup(cell.getRecord())).element()
                                ))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("open", "Open Lesson")
                                .styleCell(
                                        element -> element.style.setProperty(
                                                "vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    LessonDetails record = cell.getRecord();
                                    if (record.getKifuId() != null) {
                                        String href =
                                                "#" + historyMapper.getToken(new ViewLessonPlace(record.getKifuId(),
                                                        0));
                                        return Elements.a(href).add(Button.createPrimary(
                                                "Open")).element();
                                    } else if (record.getProblemCollectionId() != null) {
                                        String href =
                                                "#" + historyMapper.getToken(new ProblemsPlace(record.getProblemCollectionId(),
                                                        0));
                                        return Elements.a(href).add(Button.createPrimary(
                                                "Open")).element();
                                    } else {
                                        return TextNode.of("N/A");
                                    }
                                }));
        return tableConfig;
    }

    private RecordsSorter<LessonDetails> getRecordsSorter() {
        return (sortBy, sortDirection) -> {
            Comparator<LessonDetails> comparator = (o1, o2) -> 0;
            if ("difficulty".equals(sortBy)) {
                comparator = Comparator.comparingInt(LessonDetails::getDifficulty);
            } else if ("title".equals(sortBy)) {
                comparator =
                        Comparator.comparing(LessonDetails::getTitle,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            } else if ("author".equals(sortBy)) {
                comparator =
                        Comparator.comparing(LessonDetails::getAuthor,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            }
            return sortDirection == ASC ? comparator : comparator.reversed();
        };
    }

    private Node getDifficulty(final LessonDetails record) {
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

    public void setData(final List<LessonDetails> details) {
        if ((simplePaginationPlugin.getSimplePagination().activePage() - 1) * PAGE_SIZE >= details.size()) {
            simplePaginationPlugin.getSimplePagination().gotoPage(1);
        }
        localListDataStore.setData(details);
        table.load();
        customSearchTableAction.doSearch();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        lessonPropertiesForm.activate(eventBus);
    }

    private void confirmLessonDeletion(final LessonDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the lesson " + details.getTitle() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting lesson: " + details);
//            eventBus.fireEvent(new DeleteProblemCollectionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    public void addSearch(final String search) {
        customSearchTableAction.setSearch(search);
    }
}
