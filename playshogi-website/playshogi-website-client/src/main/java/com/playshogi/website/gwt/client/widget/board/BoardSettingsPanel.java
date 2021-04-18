package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class BoardSettingsPanel extends Composite {

    public BoardSettingsPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new Button("Flip board", (ClickHandler) clickEvent -> GWT.log("Flipping the board")));
        CheckBox test = new CheckBox("test");
        panel.add(test);
        initWidget(panel);
    }
}
