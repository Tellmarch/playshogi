package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dominokit.domino.ui.badges.Badge;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.FieldStyle;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.style.Style;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ManageLessonsView extends Composite {


    private final LocalListDataStore<LessonDetails> localListDataStore;
    private final DataTable<LessonDetails> table;

    interface MyEventBinder extends EventBinder<ManageLessonsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    @Inject
    public ManageLessonsView(final SessionInformation sessionInformation) {
        TableConfig<LessonDetails> tableConfig = new TableConfig<>();
        tableConfig
                .addColumn(
                        ColumnConfig.<LessonDetails>create("edit_save", "")
                                .styleCell(element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(
                                        cell ->
                                                Icons.ALL
                                                        .pencil_mdi()
                                                        .clickable()
                                                        .setTooltip("Edit")
                                                        .addClickListener(evt -> cell.getTableRow().edit())
                                                        .element())
                                .setEditableCellRenderer(
                                        cell ->
                                                Icons.ALL
                                                        .content_save_mdi()
                                                        .clickable()
                                                        .setTooltip("Save")
                                                        .addClickListener(
                                                                evt -> {
                                                                    if (cell.getTableRow().validate().isValid()) {
                                                                        cell.getTableRow().save();
                                                                    }
                                                                })
                                                        .element()))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("id", "#")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("right")
                                .asHeader()
                                .setCellRenderer(
                                        cell -> TextNode.of(cell.getRecord().getLessonId() + 1 + "")))
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
                                        })
                                .setEditableCellRenderer(
                                        cell -> {
                                            CheckBox checkBox =
                                                    CheckBox.create("")
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(!cell.getRecord().isHidden());
                                            cell.setDirtyRecordHandler(
                                                    dirty -> dirty.setHidden(!checkBox.getValue()));
                                            return checkBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("title", "Title")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getTitle()))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getTitle());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setTitle(textBox.getValue()));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("description", "Description")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getDescription()))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getDescription());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setDescription(textBox.getValue()));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("parent", "Parent")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getParentLessonId()))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getParentLessonId());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setParentLessonId(textBox.getValue()));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("kifu", "Kifu")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getRecord().getKifuId()))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getKifuId());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setKifuId(textBox.getValue()));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("difficulty", "Difficulty")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getDifficulty())))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(String.valueOf(cell.getRecord().getDifficulty()));
                                            cell.setDirtyRecordHandler(dirty -> dirty.setDifficulty(Integer.parseInt(textBox.getValue())));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("index", "Index")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(String.valueOf(cell.getRecord().getIndex())))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(String.valueOf(cell.getRecord().getIndex()));
                                            cell.setDirtyRecordHandler(dirty -> dirty.setIndex(Integer.parseInt(textBox.getValue())));
                                            return textBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("preview", "Preview")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> {
                                    String sfen = cell.getRecord().getPreviewSfen();
                                    if (sfen != null && !sfen.isEmpty()) {
                                        BoardPreview boardPreview =
                                                new BoardPreview(SfenConverter.fromSFEN(sfen), false,
                                                        sessionInformation.getUserPreferences(), 0.5);
                                        return Js.uncheckedCast(boardPreview.getElement());
                                    } else {
                                        return TextNode.empty();
                                    }
                                })
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getPreviewSfen());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setPreviewSfen(textBox.getValue()));
                                            return textBox.element();
                                        }))
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
                                        })
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox textBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(String.join(",", cell.getRecord().getTags()));
                                            cell.setDirtyRecordHandler(dirty ->
                                                    dirty.setTags(textBox.getValue().split(","))
                                            );
                                            return textBox.element();
                                        }))
                .setDirtyRecordHandlers(
                        LessonDetails::new,
                        (originalRecord, dirtyRecord) -> {
                            originalRecord.setHidden(dirtyRecord.isHidden());
                            originalRecord.setTitle(dirtyRecord.getTitle());
                            originalRecord.setTags(dirtyRecord.getTags());
                        });
        localListDataStore = new LocalListDataStore<>();
        table = new DataTable<>(tableConfig, localListDataStore);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(table.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageLessonsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("ManageLessonsView: handle LessonsListEvent");
        localListDataStore.setData(Arrays.asList(event.getLessons()));
        table.load();
    }
}
