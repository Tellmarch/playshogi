package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.Node;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.datatable.ColumnConfig;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.TableConfig;
import org.dominokit.domino.ui.datatable.plugins.AdvancedPaginationPlugin;
import org.dominokit.domino.ui.datatable.store.LocalListDataStore;
import org.dominokit.domino.ui.forms.Select;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.forms.TextArea;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.tag.TagsInput;
import org.dominokit.domino.ui.tag.store.LocalTagsStore;
import org.dominokit.domino.ui.upload.FileUpload;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.elemento.Elements.b;

public class ImportCollectionPopup {

    private static final int PAGE_SIZE = 10;

    interface MyEventBinder extends EventBinder<ImportCollectionPopup> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ModalDialog defaultSizeModal;
    private LocalListDataStore<String> localListDataStore;
    private TabsPanel tabs;
    private Select<GameCollectionDetails> gameCollectionSelect;
    private Select<ProblemCollectionDetails> problemCollectionSelect;
    private String draftId = "new";

    public ImportCollectionPopup() {
        defaultSizeModal = createModalDialog();
    }

    private ModalDialog createModalDialog() {

        ModalDialog modal = ModalDialog.create("Import Kifu Collection").setAutoClose(true).large();
        modal.appendChild(TextNode.of("With this dialog you can import a collection of " +
                "kifus."));

        Select<String> charsetSelect = Select.<String>create()
                .appendChild(SelectOption.create("UTF-8", "Encoding: Unicode (UTF-8)"))
                .appendChild(SelectOption.create("SHIFT-JIS", "Encoding: Japanese (SHIFT-JIS)"))
                .appendChild(SelectOption.create("windows-932", "Encoding: Japanese (windows-932)"))
                .setSearchable(false)
                .selectAt(2);

        FileUpload fileUpload = FileUpload.create()
                .setIcon(Icons.ALL.touch_app())
                .setUrl(GWT.getModuleBaseURL() + "uploadKifu")
                .multipleFiles()
                .autoUpload()
                .setName("file")
                .accept("zip,usf,kif,psn")
                .appendChild(Elements.h(3).textContent("Drop files here or click to upload."))
                .appendChild(
                        Elements.em()
                                .textContent(
                                        "(Supported formats: Zip file containing .usf, .kif or .psn files.)"))
                .onAddFile(
                        fileItem -> {
                            Notification.createInfo("File added. " + fileItem.getFileName()).show();
                            fileItem.addBeforeUploadHandler((request, formData) -> {
                                formData.append("collectionId", draftId);
                                formData.append("returnUsf", "false");
                                formData.append("returnSummary", "true");
                                formData.append("charset", charsetSelect.getValue());
                            });
                            fileItem.addErrorHandler(
                                    request -> Notification.createDanger("Error while uploading").show());
                            fileItem.addSuccessUploadHandler(
                                    request -> {
                                        GWT.log("Successful upload");
                                        parseResponse(request.responseText);
                                        Notification.createSuccess("File uploaded successfully").show();
                                        fileItem.remove();
                                    });
                        });
        modal.appendChild(fileUpload);

        modal.appendChild(charsetSelect);

        Tab kifusTab = Tab.create("Kifus")
                .appendChild(b().textContent("Uploaded kifus:"))
                .appendChild(createKifusTable())
                .appendChild(Button.createPrimary("Upload Kifus only, not in a collection"));
        Tab gamesTab = Tab.create("Import as Game Collection")
                .appendChild(b().textContent("Import all kifus as a game collection"))
                .appendChild(createGameCollectionsForm());
        Tab problemsTab = Tab.create("Import as Problem Collection")
                .appendChild(b().textContent("Import all kifus as a problem collection"))
                .appendChild(createProblemCollectionsForm());

        tabs = TabsPanel.create()
                .appendChild(kifusTab)
                .appendChild(gamesTab)
                .appendChild(problemsTab)
                .hide();

        modal.appendChild(tabs);

        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        modal.appendFooterChild(closeButton);
        return modal;

    }

    private Node createGameCollectionsForm() {
        Tab newTab = createNewGameCollectionTab();
        Tab existingTab = createExistingGameCollectionTab();
        return TabsPanel.create().appendChild(newTab).appendChild(existingTab).element();
    }

    private Tab createExistingGameCollectionTab() {
        gameCollectionSelect = Select.create();
        return Tab.create("Add to existing Game Collection")
                .appendChild(gameCollectionSelect)
                .appendChild(Button.createPrimary("Upload Kifus in existing Game Collection"));
    }

    private Tab createNewGameCollectionTab() {
        Select<String> visibility = Select.<String>create()
                .appendChild(SelectOption.create("Public", "Visibility: Public"))
                .appendChild(SelectOption.create("Unlisted", "Visibility: Unlisted"))
                .setSearchable(false)
                .selectAt(1);
        TextArea description = TextArea.create("Description").setHelperText("Less than 5000 characters");
        TextBox title = TextBox.create("Title");
        title.setValue("My New Game Collection");

        return Tab.create("New Game Collection")
                .appendChild(title)
                .appendChild(description)
                .appendChild(visibility)
                .appendChild(Button.createPrimary("Upload Kifus in new Game Collection"));
    }

    private Node createProblemCollectionsForm() {
        Tab newTab = createNewProblemCollectionTab();
        Tab existingTab = createExistingProblemCollectionTab();
        return TabsPanel.create().appendChild(newTab).appendChild(existingTab).element();
    }

    private Tab createExistingProblemCollectionTab() {
        problemCollectionSelect = Select.create();
        return Tab.create("Add to existing Problem Collection")
                .appendChild(problemCollectionSelect)
                .appendChild(Button.createPrimary("Upload Kifus in existing Problem Collection"));
    }

    private Tab createNewProblemCollectionTab() {
        Select<String> visibility = Select.<String>create()
                .appendChild(SelectOption.create("Public", "Visibility: Public"))
                .appendChild(SelectOption.create("Unlisted", "Visibility: Unlisted"))
                .setSearchable(false)
                .selectAt(1);
        TextArea description = TextArea.create("Description").setHelperText("Less than 5000 characters");
        TextBox title = TextBox.create("Title");
        title.setValue("My New Problem Collection");

        LocalTagsStore<String> tagsStore =
                LocalTagsStore.<String>create()
                        .addItem("Openings", "Openings")
                        .addItem("Hisshi", "Hisshi")
                        .addItem("Castle", "Castle")
                        .addItem("Endgame", "Endgame")
                        .addItem("Find the next move", "Find the next move")
                        .addItem("For beginners", "For beginners")
                        .addItem("Tsume", "Tsume");
        TagsInput<String> tags = TagsInput.create("Tags", tagsStore);

        Select<Integer> difficulty = Select.<Integer>create()
                .appendChild(SelectOption.create(1, "Difficulty: Very Easy (★☆☆☆☆)"))
                .appendChild(SelectOption.create(2, "Difficulty: Easy (★★☆☆☆)"))
                .appendChild(SelectOption.create(3, "Difficulty: Medium (★★★☆☆)"))
                .appendChild(SelectOption.create(4, "Difficulty: Hard (★★★★☆)"))
                .appendChild(SelectOption.create(5, "Difficulty: Very Hard (★★★★★)"))
                .setSearchable(false)
                .selectAt(3);

        return Tab.create("New Problem Collection")
                .appendChild(title)
                .appendChild(description)
                .appendChild(difficulty)
                .appendChild(visibility)
                .appendChild(tags.setPlaceholder("Tags..."))
                .appendChild(Button.createPrimary("Upload Kifus in new Problem Collection"));
    }

    private Node createKifusTable() {
        AdvancedPaginationPlugin<String> paginationPlugin = new AdvancedPaginationPlugin<>(PAGE_SIZE);
        TableConfig<String> tableConfig = new TableConfig<>();
        tableConfig.addColumn(
                ColumnConfig.<String>create("id", "#")
                        .styleCell(
                                element -> element.style.setProperty("vertical-align", "top"))
                        .textAlign("right")
                        .asHeader()
                        .setCellRenderer(
                                cell -> TextNode.of(String.valueOf(cell.getTableRow().getIndex() + 1 + PAGE_SIZE * (paginationPlugin.getPagination().activePage() - 1)))));
        tableConfig.addColumn(
                ColumnConfig.<String>create("Description", "Description")
                        .setCellRenderer(cell -> TextNode.of(cell.getRecord()))
        );
        tableConfig.addPlugin(paginationPlugin);
        localListDataStore = new LocalListDataStore<>();
        localListDataStore.setPagination(paginationPlugin.getPagination());
        DataTable<String> table = new DataTable<>(tableConfig, localListDataStore);
        return table.element();
    }

    private void parseResponse(final String responseText) {
        List<String> kifus = new ArrayList<>();
        for (String line : responseText.split("\n")) {
            if (line.startsWith("COLLECTION")) {
                String draftCollectionId = line.substring(11).trim();
                GWT.log("Draft ID: " + draftCollectionId);
            } else if (line.startsWith("KIFU:")) {
                kifus.add(line.substring(5).trim());
            }
        }
        localListDataStore.setData(kifus);
        tabs.show();
    }

    public void show() {
        reset();
        defaultSizeModal.open();
    }

    private void reset() {
        tabs.hide();
        draftId = uuid();
        GWT.log("Using draft id: " + draftId);
    }

    public native static String uuid() /*-{
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
            function(c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r
                        : (r & 0x3 | 0x8);
                return v.toString(16);
            });
}-*/;

    public void activate(final EventBus eventBus) {
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("ImportCollectionPopup: handle GameCollectionsEvent:");
        if (event.getMyCollections() != null) {
            gameCollectionSelect.removeAllOptions();
            for (GameCollectionDetails collection : event.getMyCollections()) {
                gameCollectionSelect.appendChild(SelectOption.create(collection, collection.getId(),
                        collection.getName()));
            }
        }
    }

    @EventHandler
    public void onListProblemCollectionsEvent(final ListProblemCollectionsEvent event) {
        GWT.log("ImportCollectionPopup: handle ListProblemCollectionsEvent:");
        if (event.getMyCollections() != null) {
            problemCollectionSelect.removeAllOptions();
            for (ProblemCollectionDetails collection : event.getMyCollections()) {
                problemCollectionSelect.appendChild(SelectOption.create(collection, collection.getId(),
                        collection.getName()));
            }
        }
    }
}
