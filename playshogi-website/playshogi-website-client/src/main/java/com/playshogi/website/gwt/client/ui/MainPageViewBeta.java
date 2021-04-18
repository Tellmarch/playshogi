package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.button.Button;

@Singleton
public class MainPageViewBeta extends Composite {

    private final PlaceController placeController;

    interface Resources extends ClientBundle {
        @Source("com/playshogi/website/gwt/resources/icons/alarm-clock_red.png")
        ImageResource alarmClockRed();

        @Source("com/playshogi/website/gwt/resources/icons/puzzle_blue.png")
        ImageResource puzzleBlue();

        @Source("com/playshogi/website/gwt/resources/icons/speaker_green.png")
        ImageResource speakerGreen();

        @Source("com/playshogi/website/gwt/resources/icons/wrench_yellow.png")
        ImageResource wrenchYellow();

        @Source("com/playshogi/website/gwt/resources/background/silver_movement.png")
        ImageResource learnBackground();

        @Source("com/playshogi/website/gwt/resources/background/tsume.png")
        ImageResource puzzlesBackground();

        @Source("com/playshogi/website/gwt/resources/background/starting_position.png")
        ImageResource practiceBackground();

        @Source("com/playshogi/website/gwt/resources/background/game_collection.png")
        ImageResource collectionsBackground();
    }

    private final Resources resources = GWT.create(Resources.class);

    @Inject
    public MainPageViewBeta(PlaceController placeController) {
        this.placeController = placeController;

        Grid outerGrid = new Grid(1, 2);

        Grid grid = new Grid(2, 2);

        grid.setWidget(0, 0, getLearnPanel());
        grid.setWidget(0, 1, getPuzzlesPanel());
        grid.setWidget(1, 0, getPracticePanel());
        grid.setWidget(1, 1, getCollectionsPanel());

        outerGrid.setWidget(0, 0, grid);


        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                grid.getCellFormatter().setWidth(i, j, "450px");
                grid.getCellFormatter().setHeight(i, j, "200px");
                grid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
            }
        }


        initWidget(outerGrid);
    }

    private Widget getLearnPanel() {
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<b>Learn</b>"));
        panel.add(new HTML("Learn how to play Shogi with an interactive tutorial!"));
        panel.add(new HTML("<br>"));
        Grid grid = new Grid(1, 2);
        grid.setWidget(0, 0, new Image(resources.learnBackground()));

        ElementWidget button =
                new ElementWidget(Button.createPrimary("Tutorial")
                        .addClickListener(evt -> placeController.goTo(new TutorialPlace())).element());
        button.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);

        grid.setWidget(0, 1, button);

        panel.add(grid);
        decoratorPanel.setWidget(panel);
        panel.setWidth("450px");
        panel.setHeight("200px");

        return decoratorPanel;
    }

    private Widget getPuzzlesPanel() {
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<b>Puzzles</b>"));
        panel.add(new HTML("Improve your shogi skills by solving puzzles."));
        panel.add(new HTML("<br>"));
        panel.add(new Image(resources.puzzleBlue()));
        panel.add(new ElementWidget(Button.createPrimary("TsumeShogi Problems").addClickListener(evt -> placeController.goTo(new TsumePlace())).element()));
        panel.add(new Image(resources.alarmClockRed()));
        panel.add(new ElementWidget(Button.createPrimary("ByoYomi Survival").addClickListener(evt -> placeController.goTo(new ByoYomiLandingPlace())).element()));

        decoratorPanel.setWidget(panel);
        panel.setWidth("450px");
        panel.setHeight("200px");
        return decoratorPanel;
    }

    private Widget getPracticePanel() {
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<b>Practice</b>"));
        panel.add(new HTML("Practice your playing skills against a computer."));
        panel.add(new HTML("<br>"));
        Grid grid = new Grid(1, 2);
        grid.setWidget(0, 0, new Image(resources.practiceBackground()));

        ElementWidget button =
                new ElementWidget(Button.createPrimary("Play")
                        .addClickListener(evt -> placeController.goTo(new PlayPlace())).element());
        button.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);

        grid.setWidget(0, 1, button);
        panel.add(grid);
        decoratorPanel.setWidget(panel);
        panel.setWidth("450px");
        panel.setHeight("200px");
        return decoratorPanel;
    }

    private Widget getCollectionsPanel() {
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<b>Collections</b>"));
        panel.add(new HTML("Browse or Create collections of Shogi games."));
        panel.add(new HTML("<br>"));
        Grid grid = new Grid(1, 2);
        grid.setWidget(0, 0, new Image(resources.collectionsBackground()));

        ElementWidget button =
                new ElementWidget(Button.createPrimary("Collections")
                        .addClickListener(evt -> placeController.goTo(new GameCollectionsPlace())).element());
        button.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);

        grid.setWidget(0, 1, button);
        panel.add(grid);
        decoratorPanel.setWidget(panel);
        panel.setWidth("450px");
        panel.setHeight("200px");
        return decoratorPanel;
    }


    public void activate(final EventBus eventBus) {
    }
}
