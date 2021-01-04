package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;

public class DecorationPanel extends Composite {
    private EventBus eventBus;

    public DecorationPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<br/>"));
        panel.add(new Button("Clear", (ClickHandler) clickEvent -> {
            GWT.log("Clearing decorations");
            eventBus.fireEvent(new ClearDecorationsEvent());
        }));

        initWidget(panel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating DecorationPanel");
        this.eventBus = eventBus;
    }
}
