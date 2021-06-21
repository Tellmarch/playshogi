package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.kifu.GameRecordExportRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordPreviewRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class KifuEditorPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<KifuEditorPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private final Button importButton;
    private final Button saveButton;
    private final Button exportButton;
    private final Button previewButton;

    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();

    public KifuEditorPanel(final GameNavigator gameNavigator) {

        FlowPanel verticalPanel = new FlowPanel();

        importButton = new Button("Import kifu", this);
        verticalPanel.add(importButton);

        verticalPanel.add(new HTML("<br/>"));
        verticalPanel.add(gameNavigator);
        verticalPanel.add(new HTML("<br/>"));

        saveButton = new Button("Save kifu", this);
        verticalPanel.add(saveButton);

        verticalPanel.add(new HTML("<br/>"));

        exportButton = new Button("Export kifu", this);
//        verticalPanel.add(exportButton);

        previewButton = new Button("Preview kifu", this);
//        verticalPanel.add(previewButton);


        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == importButton) {
            GWT.log("Kifu editor: Opening the import dialog box");
            importKifuPanel.showInDialog(null);
        } else if (source == saveButton) {
            GWT.log("Kifu editor: request saving kifu");
            eventBus.fireEvent(new GameRecordSaveRequestedEvent());
        } else if (source == exportButton) {
            GWT.log("Kifu editor: request exporting kifu");
            eventBus.fireEvent(new GameRecordExportRequestedEvent());
        } else if (source == previewButton) {
            GWT.log("Kifu editor: request preview kifu");
            Window.alert("Use the back button of your browser to come back to editing");
            eventBus.fireEvent(new GameRecordPreviewRequestedEvent());
        }
    }

    public void activate(final EventBus eventBus, final KifuEditorPlace place) {
        GWT.log("Activating kifu editor panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        importKifuPanel.activate(eventBus);
        importButton.setEnabled(place.getKifuId() == null);
    }

}
