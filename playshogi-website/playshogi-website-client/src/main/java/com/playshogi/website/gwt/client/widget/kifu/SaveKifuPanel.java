package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.events.kifu.SaveKifuEvent;
import com.playshogi.website.gwt.client.events.kifu.SaveKifuResultEvent;
import com.playshogi.website.gwt.client.place.UserKifusPlace;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class SaveKifuPanel extends Composite {


    interface MyEventBinder extends EventBinder<SaveKifuPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;

    private final TextBox name;
    private final TextArea usf;
    private final ListBox typeList;

    private DialogBox saveDialogBox;
    private EventBus eventBus;
    private GameRecord gameRecord;

    public SaveKifuPanel(final PlaceController placeController) {
        this.placeController = placeController;

        FlowPanel panel = new FlowPanel();

        Grid grid = new Grid(3, 2);
        grid.setHTML(0, 0, "Kifu name:");
        grid.setHTML(1, 0, "USF:");
        grid.setHTML(2, 0, "Type:");

        name = createTextBox("My New Kifu");
        usf = createTextArea("");
        typeList = createTypeDropdown();

        grid.setWidget(0, 1, name);
        grid.setWidget(1, 1, usf);
        grid.setWidget(2, 1, typeList);

        panel.add(grid);

        panel.add(new HTML("<br/>"));

        initWidget(panel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating SaveKifuPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
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

    private ListBox createTypeDropdown() {
        ListBox list = new ListBox();
        list.addItem("Game");
        list.addItem("Problem");
        list.addItem("Lesson");
        list.setVisibleItemCount(1);
        return list;
    }

    private DialogBox createSaveDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Save Kifu");
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

    private void saveDetails() {
        KifuDetails.KifuType kifuType = KifuDetails.KifuType.valueOf(typeList.getSelectedValue().toUpperCase());
        eventBus.fireEvent(new SaveKifuEvent(UsfFormat.INSTANCE.write(gameRecord), kifuType, name.getText()));
    }

    public void showInSaveDialog(final GameRecord gameRecord, final KifuDetails.KifuType type) {
        saveDialogBox = createSaveDialogBox();

        fillDetails(gameRecord, type);

        saveDialogBox.center();
        saveDialogBox.show();
    }

    private void fillDetails(final GameRecord gameRecord, final KifuDetails.KifuType type) {
        this.gameRecord = gameRecord;
        name.setText(getDefaultName(gameRecord));
        usf.setText(UsfFormat.INSTANCE.write(gameRecord));
        for (int i = 0; i < typeList.getItemCount(); i++) {
            if (typeList.getItemText(i).equalsIgnoreCase(type.toString())) {
                typeList.setSelectedIndex(i);
            }
        }
    }

    private String getDefaultName(final GameRecord gameRecord) {
        GameInformation info = gameRecord.getGameInformation();
        String name = info.getLocation() + " - " + info.getBlack() + " - " + info.getWhite() + " - " + info.getDate();
        return name.length() <= 45 ? name : name.substring(0, 45);
    }

    @EventHandler
    public void onSaveKifuResultEvent(final SaveKifuResultEvent event) {
        GWT.log("SaveKifuPanel: Handling SaveKifuResultEvent: " + event.isSuccess() + " - " + event.getKifuId());
        if (event.isSuccess()) {
            Window.alert("Kifu saved!");
            if (saveDialogBox != null) saveDialogBox.hide();
            placeController.goTo(new UserKifusPlace());
        } else {
            Window.alert("Error saving the kifu");
            if (saveDialogBox != null) saveDialogBox.hide();
        }
    }
}
