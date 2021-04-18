package com.playshogi.website.gwt.client.widget;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TablePanel extends VerticalPanel {

    private final Label titleLabel;

    public TablePanel(final String title, final CellTable<?> table) {
        super();
        this.setHorizontalAlignment(ALIGN_CENTER);
        titleLabel = new Label(title);
        titleLabel.setStyleName("table-title");
        this.add(titleLabel);
        SimplePager collectionsPager = new SimplePager();
        collectionsPager.setDisplay(table);
        this.add(table);
        this.add(collectionsPager);
    }

    public void setTableTitle(final String title) {
        titleLabel.setText(title);
    }
}
