package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.events.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.UserNavigatedBackEvent;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class KifuEditorPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<KifuEditorPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private final Button importButton;
    private final Button saveButton;

    private DialogBox importDialogBox;

    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();

    public KifuEditorPanel(final GameNavigator gameNavigator) {

        FlowPanel verticalPanel = new FlowPanel();

        importButton = new Button("Import kifu");
        importButton.addClickHandler(this);

        verticalPanel.add(importButton);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        verticalPanel.add(gameNavigator);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        saveButton = new Button("Save kifu");
        saveButton.addClickHandler(this);

        verticalPanel.add(saveButton);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == importButton) {
            GWT.log("Kifu editor: Opening the import dialog box");
            if (importDialogBox == null) {
                importDialogBox = createImportDialogBox();
            }
            importDialogBox.center();
            importDialogBox.show();
        } else if (source == saveButton) {
            GWT.log("Kifu editor: request saving kifu");
            eventBus.fireEvent(new GameRecordSaveRequestedEvent());
        }
    }

    @EventHandler
    public void onNewVariation(final NewVariationPlayedEvent event) {
        GWT.log("Kifu editor: handle new variation played event");
    }

    @EventHandler
    public void onEndOfVariation(final EndOfVariationReachedEvent event) {
        GWT.log("Kifu editor: handle end of variation reached event");
    }

    @EventHandler
    public void onUserNavigatedBack(final UserNavigatedBackEvent event) {
        GWT.log("Kifu editor: handle user navigated back event");
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating kifu editor panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        importKifuPanel.activate(eventBus);
    }

    private DialogBox createImportDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Import kifu");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(importKifuPanel);
        dialogContents.setCellHorizontalAlignment(importKifuPanel, HasHorizontalAlignment.ALIGN_CENTER);

        Button closeButton = new Button("Close", (ClickHandler) event -> dialogBox.hide());
        dialogContents.add(closeButton);

        dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

}
