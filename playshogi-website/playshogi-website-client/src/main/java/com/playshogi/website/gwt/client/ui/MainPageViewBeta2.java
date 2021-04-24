package com.playshogi.website.gwt.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.Typography.Paragraph;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.thumbnails.Thumbnail;

import static org.jboss.elemento.Elements.*;

@Singleton
public class MainPageViewBeta2 extends Composite {

    private final PlaceController placeController;

    @Inject
    public MainPageViewBeta2(PlaceController placeController) {
        this.placeController = placeController;

        HTMLDivElement element = Card.create(
                "Welcome to PlayShogi.com!",
                "On this website, you can learn the rules, practice against a computer, study with a collection of " +
                        "problems and game records, explore openings, and more!")
                .appendChild(
                        Row.create()
                                .addColumn(Column.span3())
                                .addColumn(getLearnCard())
                                .addColumn(getPracticeCard())
                                .addColumn(getCollectionsCard()))
                .appendChild(
                        Row.create()
                                .addColumn(Column.span3())
                                .addColumn(getPuzzlesCard())
                                .addColumn(getByoYomiCard())
                                .addColumn(getOpeningsCard()))
                .element();

        initWidget(new ElementWidget(element));
    }

    private Column getCollectionsCard() {
        return getCard("images/background/game_collection.png", "Collections", "Browse or Create collections of " +
                "Shogi games.", "Collections", new GameCollectionsPlace());
    }

    private Column getPracticeCard() {
        return getCard("images/background/starting_position.png", "Practice", "Practice your playing skills against " +
                "a computer.", "Play", new PlayPlace());
    }

    private Column getPuzzlesCard() {
        return getCard("images/background/tsume.png", "Puzzles", "Improve your shogi skills by solving puzzles.",
                "TsumeShogi Problems", new TsumePlace());
    }

    private Column getByoYomiCard() {
        return getCard("images/background/tsume.png", "Byo-yomi survival", "Try to solve problems as fast as " +
                "possible!", "ByoYomi Survival", new ByoYomiPlace());
    }

    private Column getRealProblemsCard() {
        return getCard("images/background/tsume.png", "Problems from Real Games", "Can you play like Habu? Find out " +
                        "with this set of problems extracted from famous professional games!", "Real Games Problems",
                new ByoYomiPlace());
    }

    private Column getLearnCard() {
        return getCard("images/background/silver_movement.png", "Learn", "Learn how to play Shogi with an " +
                "interactive tutorial!", "Tutorial", new TutorialPlace());
    }

    private Column getOpeningsCard() {
        return getCard("images/background/starting_position.png", "Openings", "Browse openings from a database of " +
                "50K professional games!", "Openings", new OpeningsPlace());
    }


    private Column getCard(final String image, final String title, final String description,
                           final String buttonText, final Place place) {
        return Column.span2()
                .appendChild(
                        Thumbnail.create()
                                .setContent(
                                        a().add(
                                                img(image)
                                                        .css(Styles.img_responsive)))
                                .appendCaptionChild(h(3).textContent(title))
                                .appendCaptionChild(Paragraph.create(description))
                                .appendCaptionChild(Button.createPrimary(buttonText)
                                        .addClickListener(evt -> this.placeController.goTo(place)).element()));
    }

    public void activate(final EventBus eventBus) {
    }
}
