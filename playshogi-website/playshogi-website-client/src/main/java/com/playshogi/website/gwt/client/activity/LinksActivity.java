package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.ui.LinksView;

public class LinksActivity extends MyAbstractActivity {

    private final LinksView linksView;

    public LinksActivity(final LinksView linksView) {
        this.linksView = linksView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting links activity");
        containerWidget.setWidget(linksView.asWidget());
    }

}
