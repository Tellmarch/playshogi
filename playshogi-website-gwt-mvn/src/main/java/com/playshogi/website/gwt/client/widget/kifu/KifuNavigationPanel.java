package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class KifuNavigationPanel extends Composite {

    public KifuNavigationPanel(final GameNavigator gameNavigator) {

        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<br/>"));
        panel.add(gameNavigator);
        panel.add(new HTML("<br/>"));
        initWidget(panel);
    }

}
