package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class CollectionHelpActivity extends MyAbstractActivity {

    private final Widget collectionHelpView;

    public CollectionHelpActivity(final Widget collectionHelpView) {
        this.collectionHelpView = collectionHelpView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting collection help activity");
        containerWidget.setWidget(collectionHelpView.asWidget());
    }

}
