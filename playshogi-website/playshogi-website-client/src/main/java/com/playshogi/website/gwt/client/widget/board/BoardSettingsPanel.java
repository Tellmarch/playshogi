package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.events.user.ArrowModeSelectedEvent;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.user.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.SwitchButton;

public class BoardSettingsPanel extends Composite {

    interface MyEventBinder extends EventBinder<BoardSettingsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    public BoardSettingsPanel(final UserPreferences userPreferences) {
        FlowPanel panel = new FlowPanel();
        panel.add(new ElementWidget(CheckBox.create("Flip Board").addChangeHandler(value -> GWT.log(String.valueOf(value))).element()));
        SwitchButton pieces = SwitchButton.create("Pieces", "Traditional", "International");
        if (userPreferences.getPieceStyle() == PieceGraphics.Style.HIDETCHI) {
            pieces.check();
        }
        panel.add(new ElementWidget(pieces.addChangeHandler(this::setInternationalPieceStyle).element()));
        SwitchButton notation = SwitchButton.create("Move Notation", "Traditional", "International");
        if (userPreferences.getNotationStyle() == UserPreferences.NotationStyle.WESTERN_ALPHABETICAL) {
            notation.check();
        }
        panel.add(new ElementWidget(notation.addChangeHandler(this::setInternationalMoveNotation).element()));
        CheckBox drawArrows =
                CheckBox.create("Let me draw arrows").check().addChangeHandler(value -> eventBus.fireEvent(new ArrowModeSelectedEvent(value)));
        panel.add(new ElementWidget(drawArrows.element()));
        panel.add(new ElementWidget(Button.create("Clear Arrows").addClickListener(value -> eventBus.fireEvent(new ClearDecorationsEvent())).element()));


        initWidget(panel);
    }

    private void setInternationalMoveNotation(final Boolean value) {
        eventBus.fireEvent(new NotationStyleSelectedEvent(value ? UserPreferences.NotationStyle.WESTERN_ALPHABETICAL :
                UserPreferences.NotationStyle.TRADITIONAL));
    }

    private void setInternationalPieceStyle(final Boolean value) {
        eventBus.fireEvent(new PieceStyleSelectedEvent(value ? PieceGraphics.Style.HIDETCHI :
                PieceGraphics.Style.RYOKO));
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
    }
}
