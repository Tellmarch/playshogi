package com.playshogi.website.gwt.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.website.gwt.client.events.user.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.widget.board.PieceGraphics;

public class PiecesSelectorPanel extends Composite implements ClickHandler {

    private final RadioButton radio1;
    private final RadioButton radio2;
    private EventBus eventBus;

    public PiecesSelectorPanel() {

        AbsolutePanel panel = new AbsolutePanel();

        panel.setSize("150px", "100px");

        Image image1 = new Image(PieceGraphics.getPieceImage(Piece.SENTE_PAWN, PieceGraphics.Style.RYOKO));
        Image image2 = new Image(PieceGraphics.getPieceImage(Piece.SENTE_PAWN, PieceGraphics.Style.HIDETCHI));

        radio1 = new RadioButton("pieces", "Trad.");
        radio2 = new RadioButton("pieces", "Int.");

        panel.add(radio1, 0, 50);
        panel.add(radio2, 75, 50);

        panel.add(image1, 0, 0);
        panel.add(image2, 75, 0);

        radio1.addClickHandler(this);
        radio2.addClickHandler(this);
        image1.addClickHandler(clickEvent -> {
            GWT.log("User selected traditional pieces");
            radio1.setValue(true);
            eventBus.fireEvent(new PieceStyleSelectedEvent(PieceGraphics.Style.RYOKO));
        });
        image2.addClickHandler(clickEvent -> {
            GWT.log("User selected international pieces");
            radio2.setValue(true);
            eventBus.fireEvent(new PieceStyleSelectedEvent(PieceGraphics.Style.HIDETCHI));
        });

        radio1.setValue(true);

        initWidget(panel);

    }


    @Override
    public void onClick(ClickEvent clickEvent) {
        if (radio1.getValue()) {
            GWT.log("User selected traditional pieces");
            eventBus.fireEvent(new PieceStyleSelectedEvent(PieceGraphics.Style.RYOKO));
        } else if (radio2.getValue()) {
            GWT.log("User selected international pieces");
            eventBus.fireEvent(new PieceStyleSelectedEvent(PieceGraphics.Style.HIDETCHI));
        }
    }

    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
