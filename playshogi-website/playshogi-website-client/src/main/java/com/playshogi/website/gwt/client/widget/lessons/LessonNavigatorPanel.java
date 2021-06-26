package com.playshogi.website.gwt.client.widget.lessons;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.gametree.*;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.utils.DominoElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class LessonNavigatorPanel {

    private final HtmlContentBuilder<HTMLDivElement> div;
    private EventBus eventBus;
    private final Button nextVariation;
    private final Button backButton;

    public LessonNavigatorPanel() {
        div = Elements.div();
        DominoElement.of(div).style().setBackgroundColor(Color.WHITE.getHex() + "a0");

        HtmlContentBuilder<HTMLDivElement> buttonsDiv = Elements.div();

        buttonsDiv.add(Button.createPrimary("<<").addClickListener(evt -> eventBus.fireEvent(new NavigateToStartEvent())));
        buttonsDiv.add(Button.createPrimary("<").addClickListener(evt -> eventBus.fireEvent(new NavigateBackEvent())));
        buttonsDiv.add(Button.createPrimary(">").addClickListener(evt -> eventBus.fireEvent(new NavigateForwardEvent())));
        buttonsDiv.add(Button.createPrimary(">>").addClickListener(evt -> eventBus.fireEvent(new NavigateToEndEvent())));

        DominoElement.of(buttonsDiv).style().setPadding("0.5em");

        div.add(buttonsDiv);

        backButton = Button.createPrimary("Back").style().setMargin("0.5em").get()
                .addClickListener(evt -> eventBus.fireEvent(new NavigateStartUnvisitedVariationEvent()));
        nextVariation = Button.createPrimary("Next").style().setMargin("0.5em").get()
                .addClickListener(evt -> eventBus.fireEvent(new NavigateNextEvent()));

        div.add(backButton);
        div.add(nextVariation);

    }

    public Widget getAsWidget() {
        return new ElementWidget(div.element());
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
