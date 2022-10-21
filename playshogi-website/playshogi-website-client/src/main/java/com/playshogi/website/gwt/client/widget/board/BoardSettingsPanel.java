package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.kifu.*;
import com.playshogi.website.gwt.client.events.user.ArrowModeSelectedEvent;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.user.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.Radio;
import org.dominokit.domino.ui.forms.RadioGroup;
import org.dominokit.domino.ui.forms.SwitchButton;
import org.dominokit.domino.ui.icons.Icons;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import static org.jboss.elemento.Elements.h;

public class BoardSettingsPanel extends Composite {

    interface MyEventBinder extends EventBinder<BoardSettingsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private DialogBox dialogBox = null;

    public BoardSettingsPanel(final UserPreferences userPreferences) {

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.add(CheckBox.create("Flip Board").addChangeHandler(this::onFlipBoard));
        div.add(CheckBox.create("Blind Mode").addChangeHandler(this::onBlindMode));
        SwitchButton pieces = SwitchButton.create("Pieces", "Traditional", "International");
        if (userPreferences.getPieceStyle() == PieceGraphics.Style.HIDETCHI) {
            pieces.check();
        }
        div.add(pieces.addChangeHandler(this::setInternationalPieceStyle));

        Radio<UserPreferences.NotationStyle> radio1 = Radio.create(UserPreferences.NotationStyle.WESTERN_ALPHABETICAL
                , "International").check();
        Radio<UserPreferences.NotationStyle> radio2 = Radio.create(UserPreferences.NotationStyle.WESTERN_NUMERICAL,
                "International (numbers)");
        Radio<UserPreferences.NotationStyle> radio3 = Radio.create(UserPreferences.NotationStyle.TRADITIONAL,
                "Traditional");

        RadioGroup<UserPreferences.NotationStyle> notationGroup = RadioGroup.<UserPreferences.NotationStyle>create(
                "notation")
                .appendChild(radio1)
                .appendChild(radio2)
                .appendChild(radio3)
                .horizontal();

        switch (userPreferences.getNotationStyle()) {
            case TRADITIONAL:
                radio3.check();
                break;
            case WESTERN_NUMERICAL:
                radio2.check();
                break;
            case WESTERN_ALPHABETICAL:
                radio1.check();
                break;
            default:
                break;
        }

        div.add(h(5).textContent("Move notation:"));
        div.add(notationGroup.addChangeHandler(this::setInternationalMoveNotation));

        CheckBox drawArrows =
                CheckBox.create("Let me draw arrows").check()
                        .addChangeHandler(value -> eventBus.fireEvent(new ArrowModeSelectedEvent(value)));
        div.add(drawArrows);
        div.add(Button.create(Icons.ALL.eraser_mdi()).setContent("Clear Arrows")
                .addClickListener(value -> eventBus.fireEvent(new ClearDecorationsEvent())));

        div.add(Button.create(Icons.ALL.content_copy()).setContent("Copy Position SFEN")
                .addClickListener(value -> eventBus.fireEvent(new CopyPositionEvent())).style().setMarginLeft("1em"));

        div.add(Button.create(Icons.ALL.content_copy()).setContent("Create Diagram SVG")
                .addClickListener(value -> eventBus.fireEvent(new CreateSVGDiagramEvent())).style().setMarginLeft("1em"));

        initWidget(new ElementWidget(div.element()));
    }

    private void onFlipBoard(boolean inverted) {
        GWT.log("Flip board: " + inverted);
        eventBus.fireEvent(new FlipBoardEvent(inverted));
    }

    private void onBlindMode(boolean blind) {
        GWT.log("Blind Mode: " + blind);
        eventBus.fireEvent(new BlindModeEvent(blind));
    }

    private void setInternationalMoveNotation(final UserPreferences.NotationStyle notationStyle) {
        eventBus.fireEvent(new NotationStyleSelectedEvent(notationStyle));
    }

    private void setInternationalPieceStyle(final Boolean value) {
        eventBus.fireEvent(new PieceStyleSelectedEvent(value ? PieceGraphics.Style.HIDETCHI :
                PieceGraphics.Style.RYOKO));
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
    }

    private DialogBox createDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Board Settings");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(this);
        dialogContents.setCellHorizontalAlignment(this, HasHorizontalAlignment.ALIGN_CENTER);

        com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Close",
                (ClickHandler) event -> dialogBox.hide());
        dialogContents.add(closeButton);

        dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

    public void showInDialog() {
        if (dialogBox == null) {
            dialogBox = createDialogBox();
        }
        dialogBox.center();
        dialogBox.show();
    }
}
