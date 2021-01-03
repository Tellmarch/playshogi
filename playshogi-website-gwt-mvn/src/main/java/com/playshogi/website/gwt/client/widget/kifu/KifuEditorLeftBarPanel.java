package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;

public class KifuEditorLeftBarPanel extends Composite {

    interface MyEventBinder extends EventBinder<KifuEditorLeftBarPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuInformationPanel informationPanel;
    private final PositionEditingPanel editingPanel;

    private EventBus eventBus;

    public KifuEditorLeftBarPanel() {
        informationPanel = new KifuInformationPanel();
        editingPanel = new PositionEditingPanel();

        StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.EM);
        stackPanel.setPixelSize(200, 400);

        stackPanel.add(informationPanel.asWidget(), "Kifu Information", 2);
        stackPanel.add(editingPanel.asWidget(), "Edit position", 2);

        initWidget(stackPanel);
    }

    private Widget createHeaderWidget(String text, ImageResource image) {
        // Add the image and text to a horizontal panel
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setHeight("100%");
        hPanel.setSpacing(0);
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.add(new Image(image));
        HTML headerText = new HTML(text);
        headerText.setStyleName("cw-StackPanelHeader");
        hPanel.add(headerText);
        return new SimplePanel(hPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating KifuEditorLeftBarPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        informationPanel.activate(eventBus);
        editingPanel.activate(eventBus);
    }
}
