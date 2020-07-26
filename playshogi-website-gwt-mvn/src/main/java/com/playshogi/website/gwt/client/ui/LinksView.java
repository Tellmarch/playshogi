package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

@Singleton
public class LinksView extends Composite {
    private static LinksViewImplUiBinder uiBinder = GWT.create(LinksViewImplUiBinder.class);

    interface LinksViewImplUiBinder extends UiBinder<Widget, LinksView> {
    }

    public LinksView() {
        GWT.log("Creating links view");
        initWidget(uiBinder.createAndBindUi(this));
    }

}
