package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;

@Singleton
public class GameCollectionsView extends Composite {

    @Inject
    public GameCollectionsView(final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating game collections view");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("Game collections<br/>")));

        initWidget(flowPanel);
    }

}
