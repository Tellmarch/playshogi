package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.collections.CreateGameCollectionEvent;
import com.playshogi.website.gwt.client.events.collections.SaveGameCollectionDetailsEvent;
import com.playshogi.website.gwt.client.events.collections.SaveGameCollectionDetailsResultEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

public class CollectionPropertiesPanel extends Composite {

    interface MyEventBinder extends EventBinder<CollectionPropertiesPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private final TextBox title;
    private final TextArea description;
    private final ListBox visibility;

    private DialogBox updateDialogBox;
    private DialogBox createDialogBox;
    private GameCollectionDetails details;

    public CollectionPropertiesPanel() {
        FlowPanel verticalPanel = new FlowPanel();

        Grid grid = new Grid(3, 2);
        grid.setHTML(0, 0, "Title:");
        grid.setHTML(1, 0, "Description:");
        grid.setHTML(2, 0, "Visibility:");

        title = createTextBox("My Game Collection");
        description = createTextArea("A collection of games");
        visibility = createVisibilityDropdown();

        grid.setWidget(0, 1, title);
        grid.setWidget(1, 1, description);
        grid.setWidget(2, 1, visibility);

        verticalPanel.add(grid);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        initWidget(verticalPanel);
    }

    private TextBox createTextBox(String defaultText) {
        TextBox textBox = new TextBox();
        textBox.setText(defaultText);
        textBox.setVisibleLength(60);
        return textBox;
    }

    private TextArea createTextArea(String defaultText) {
        TextArea textArea = new TextArea();
        textArea.setText(defaultText);
        textArea.setVisibleLines(8);
        textArea.setCharacterWidth(60);
        return textArea;
    }

    private ListBox createVisibilityDropdown() {
        ListBox list = new ListBox();
        list.addItem("Public");
        list.addItem("Private");
        list.addItem("Unlisted");
        list.setVisibleItemCount(1);
        return list;
    }

    private DialogBox createUpdateDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Game Collection Properties");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(this);
        dialogContents.setCellHorizontalAlignment(this, HasHorizontalAlignment.ALIGN_CENTER);

        FlowPanel buttonsPanel = new FlowPanel();
        Button closeButton = new Button("Cancel", (ClickHandler) event -> dialogBox.hide());
        Button saveButton = new Button("Save", (ClickHandler) event -> saveDetails());
        buttonsPanel.add(closeButton);
        buttonsPanel.add(saveButton);
        dialogContents.add(buttonsPanel);

        dialogContents.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

    private DialogBox createCreateDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Create new Game Collection");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(this);
        dialogContents.setCellHorizontalAlignment(this, HasHorizontalAlignment.ALIGN_CENTER);

        FlowPanel buttonsPanel = new FlowPanel();
        Button closeButton = new Button("Cancel", (ClickHandler) event -> dialogBox.hide());
        Button saveButton = new Button("Create", (ClickHandler) event -> createCollection());
        buttonsPanel.add(closeButton);
        buttonsPanel.add(saveButton);
        dialogContents.add(buttonsPanel);

        dialogContents.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }


    private void saveDetails() {
        GameCollectionDetails newDetails = new GameCollectionDetails();
        newDetails.setId(details.getId());
        newDetails.setName(title.getText());
        newDetails.setDescription(description.getText());
        newDetails.setVisibility(visibility.getSelectedItemText());

        eventBus.fireEvent(new SaveGameCollectionDetailsEvent(newDetails));
    }

    private void createCollection() {
        GameCollectionDetails newDetails = new GameCollectionDetails();
        newDetails.setName(title.getText());
        newDetails.setDescription(description.getText());
        newDetails.setVisibility(visibility.getSelectedItemText());

        eventBus.fireEvent(new CreateGameCollectionEvent(newDetails));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionPropertiesPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    public void showInUpdateDialog(final GameCollectionDetails details) {
        if (updateDialogBox == null) {
            updateDialogBox = createUpdateDialogBox();
        }

        fillDetails(details);

        updateDialogBox.center();
        updateDialogBox.show();
    }

    public void showInCreateDialog() {
        if (createDialogBox == null) {
            createDialogBox = createCreateDialogBox();
        }

        title.setText("My New Game Collection");
        description.setText("My New Game Collection");
        visibility.setSelectedIndex(1); // Private by default

        createDialogBox.center();
        createDialogBox.show();
    }

    private void fillDetails(final GameCollectionDetails details) {
        this.details = details;
        title.setText(details.getName());
        description.setText(details.getDescription());
        for (int i = 0; i < visibility.getItemCount(); i++) {
            if (visibility.getItemText(i).equalsIgnoreCase(details.getVisibility())) {
                visibility.setSelectedIndex(i);
            }
        }
    }


    @EventHandler
    public void onSaveGameCollectionDetailsResult(final SaveGameCollectionDetailsResultEvent event) {
        GWT.log("GameCollectionsActivity: Handling SaveGameCollectionDetailsResultEvent: " + event.isSuccess());
        if (event.isSuccess()) {
            Window.alert("Game Collection saved!");
        } else {
            Window.alert("Error saving the game collection");
        }
        if (updateDialogBox != null) updateDialogBox.hide();
        if (createDialogBox != null) createDialogBox.hide();
    }
}
