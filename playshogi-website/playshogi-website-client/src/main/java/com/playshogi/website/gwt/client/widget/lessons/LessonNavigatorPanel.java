package com.playshogi.website.gwt.client.widget.lessons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.Node;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.events.gametree.*;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.utils.DominoElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class LessonNavigatorPanel {


    private String currentVariation;

    interface MyEventBinder extends EventBinder<LessonNavigatorPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final HtmlContentBuilder<HTMLDivElement> div;
    private final HtmlContentBuilder<HTMLDivElement> nextExplanation;
    private final HtmlContentBuilder<HTMLDivElement> variations;
    private final NavigationController navigationController;
    private final UserPreferences userPreferences;
    private EventBus eventBus;
    private final Button nextVariation;

    public LessonNavigatorPanel(final NavigationController navigationController,
                                final UserPreferences userPreferences) {
        this.navigationController = navigationController;
        this.userPreferences = userPreferences;
        div = Elements.div();
        DominoElement.of(div).style().setBackgroundColor(Color.WHITE.getHex() + "a0");

        HtmlContentBuilder<HTMLDivElement> buttonsDiv = Elements.div();

        buttonsDiv.add(Button.createPrimary("<<").addClickListener(evt -> eventBus.fireEvent(new NavigateToStartEvent())));
        buttonsDiv.add(Button.createPrimary("<").addClickListener(evt -> eventBus.fireEvent(new NavigateBackEvent())));
        buttonsDiv.add(Button.createPrimary(">").addClickListener(evt -> eventBus.fireEvent(new NavigateForwardEvent())));
        buttonsDiv.add(Button.createPrimary(">>").addClickListener(evt -> eventBus.fireEvent(new NavigateToEndEvent())));

        DominoElement.of(buttonsDiv).style().setPadding("0.5em");

        div.add(buttonsDiv);

        nextVariation = Button.createPrimary("Next").style().setMargin("0.5em").get()
                .addClickListener(evt -> eventBus.fireEvent(new NavigateNextEvent()));

        div.add(nextVariation);

        nextExplanation = Elements.div();
        DominoElement.of(nextExplanation).style().setPadding("0.5em");

        variations = Elements.div();
        DominoElement.of(variations).style().setPadding("0.5em");

        div.add(nextExplanation);
        div.add(variations);

    }

    public Widget getAsWidget() {
        return new ElementWidget(div.element());
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onVisitedProgress(final VisitedProgressEvent event) {
        GWT.log("LessonNavigatorPanel: Handling VisitedProgressEvent");
        GameNavigation gameNavigation = navigationController.getGameNavigation();
        if (gameNavigation.canMoveForward()) {
            nextVariation.setTextContent("Next");
            if (gameNavigation.hasVariations()) {
                Node firstUnvisitedVariation = gameNavigation.getFirstUnvisitedVariation();

                variations.textContent("Branches:");
                HtmlContentBuilder<HTMLUListElement> list = Elements.ul();
                for (Node child : gameNavigation.getCurrentNode().getChildren()) {
                    String moveStr =
                            userPreferences.getMoveNotationAccordingToPreferences(child.getMove(),
                                    gameNavigation.getPreviousMove(), true);
                    HtmlContentBuilder<HTMLLIElement> li =
                            Elements.li().textContent(moveStr);
                    if (child == firstUnvisitedVariation) {
                        li.style("font-weight: bold");
                    }
                    if (child.isVisited()) {
                        li.add(Icons.ALL.checkbox_marked_circle_outline_mdi().size18().setTooltip("Seen")
                                .style().setMarginLeft("0.5em").setColor(Color.GREEN_DARKEN_2.getHex()));
                    }
                    list.add(li);
                }
                variations.add(list);
                variations.hidden(false);

                Move move = firstUnvisitedVariation.getMove();
                if (firstUnvisitedVariation == gameNavigation.getCurrentNode().getFirstChild()) {
                    currentVariation = "Main line " +
                            userPreferences.getMoveNotationAccordingToPreferences(move,
                                    gameNavigation.getPreviousMove(),
                                    true);
                    nextExplanation.textContent("Next: " + currentVariation);
                } else {
                    currentVariation = "Variation " +
                            userPreferences.getMoveNotationAccordingToPreferences(move,
                                    gameNavigation.getPreviousMove(),
                                    true);
                    nextExplanation.textContent("Next: " + currentVariation);
                }
            } else {
                nextExplanation.textContent(currentVariation);
                variations.hidden(true);
            }
        } else {
            nextVariation.setTextContent("Back");
            nextExplanation.textContent("Back to start of variation (" + currentVariation + ")");
            variations.hidden(true);
        }
    }
}
