package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class KifuNavigationPanel extends Composite {

    public KifuNavigationPanel(final GameNavigator gameNavigator) {

        FlowPanel verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        verticalPanel.add(gameNavigator);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        initWidget(verticalPanel);
    }

}
