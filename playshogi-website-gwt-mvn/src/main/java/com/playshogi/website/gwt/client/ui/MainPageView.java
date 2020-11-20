package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.place.*;

@Singleton
public class MainPageView extends Composite {

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
    }

    private final Resources resources = GWT.create(Resources.class);

    @Inject
    public MainPageView(PlaceController placeController) {
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
                grid.getCellFormatter().setWidth(i, j, "400px");
                grid.getCellFormatter().setHeight(i, j, "200px");
                grid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
            }
        }


        initWidget(outerGrid);
    }

    private Widget getLearnPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Learn</b>"));
        defaultPanelInternal.add(new HTML("Learn how to play Shogi with an interactive tutorial!"));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.alarmClockRed()));
        defaultPanelInternal.add(new Button("Tutorial",
                (ClickHandler) clickEvent -> placeController.goTo(new TutorialPlace())));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getPuzzlesPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Puzzles</b>"));
        defaultPanelInternal.add(new HTML("Improve your shogi skills by solving puzzles."));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.puzzleBlue()));
        defaultPanelInternal.add(new Button("TsumeShogi Problems",
                (ClickHandler) clickEvent -> placeController.goTo(new TsumePlace())));
        defaultPanelInternal.add(new Image(resources.alarmClockRed()));
        defaultPanelInternal.add(new Button("ByoYomi Survival",
                (ClickHandler) clickEvent -> placeController.goTo(new ByoYomiLandingPlace())));

        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getPracticePanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Practice</b>"));
        defaultPanelInternal.add(new HTML("Practice your playing skills against a computer."));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.speakerGreen()));
        defaultPanelInternal.add(new Button("Play",
                (ClickHandler) clickEvent -> placeController.goTo(new PlayPlace())));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getCollectionsPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Collections</b>"));
        defaultPanelInternal.add(new HTML("Browse or create collections of Shogi games, from amateur tournament " +
                "archives to professional title games."));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.wrenchYellow()));
        defaultPanelInternal.add(new Button("Collections",
                (ClickHandler) clickEvent -> placeController.goTo(new GameCollectionsPlace())));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }


    public void activate(final EventBus eventBus) {
    }
}
