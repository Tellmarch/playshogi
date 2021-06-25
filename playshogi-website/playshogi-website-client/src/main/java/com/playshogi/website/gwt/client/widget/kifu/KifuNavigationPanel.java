package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;

public class KifuNavigationPanel extends Composite {

    public KifuNavigationPanel(final GameNavigatorPanel gameNavigatorPanel) {

        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<br/>"));
        panel.add(gameNavigatorPanel);
        panel.add(new HTML("<br/>"));
        initWidget(panel);
    }

}
