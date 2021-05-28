package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.FieldStyle;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Style;
import org.dominokit.domino.ui.utils.TextNode;

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
    private final ImportCollectionPanel importCollectionPanel = new ImportCollectionPanel();

    public ManageLessonsView() {
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
                                        cell -> TextNode.of(cell.getTableRow().getRecord().getLessonId() + 1 + "")))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("status", "Status")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .textAlign("center")
                                .setCellRenderer(
                                        cell -> {
                                            if (!cell.getTableRow().getRecord().isHidden()) {
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
                                            CheckBox activeCheckBox =
                                                    CheckBox.create("")
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(!cell.getRecord().isHidden());
                                            cell.setDirtyRecordHandler(
                                                    dirty -> dirty.setHidden(!activeCheckBox.getValue()));
                                            return activeCheckBox.element();
                                        }))
                .addColumn(
                        ColumnConfig.<LessonDetails>create("title", "Title")
                                .styleCell(
                                        element -> element.style.setProperty("vertical-align", "top"))
                                .setCellRenderer(cell -> TextNode.of(cell.getTableRow().getRecord().getTitle()))
                                .setEditableCellRenderer(
                                        cell -> {
                                            TextBox nameBox =
                                                    TextBox.create()
                                                            .setFieldStyle(FieldStyle.ROUNDED)
                                                            .value(cell.getRecord().getTitle());
                                            cell.setDirtyRecordHandler(dirty -> dirty.setTitle(nameBox.getValue()));
                                            return nameBox.element();
                                        }))
                .setDirtyRecordHandlers(
                        LessonDetails::new,
                        (originalRecord, dirtyRecord) -> {
                            originalRecord.setHidden(dirtyRecord.isHidden());
                            originalRecord.setTitle(dirtyRecord.getTitle());
                        });
        localListDataStore = new LocalListDataStore<>();
        table = new DataTable<>(tableConfig, localListDataStore);

        initWidget(new ElementWidget(table.element()));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageLessonsView");
        this.eventBus = eventBus;
        importCollectionPanel.activate(eventBus);
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("ManageLessonsView: handle LessonsListEvent");
        localListDataStore.setData(Arrays.asList(event.getLessons()));
        table.load();
    }
}
