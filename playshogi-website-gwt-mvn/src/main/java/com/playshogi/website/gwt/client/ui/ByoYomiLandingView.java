package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.place.ByoYomiPlace;
import com.playshogi.website.gwt.client.widget.information.HighScoresPanel;
import com.playshogi.website.gwt.client.widget.problems.CustomizeSurvivalPanel;

@Singleton
public class ByoYomiLandingView extends Composite {

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

    private final Resources resources = com.google.gwt.core.client.GWT.create(Resources.class);

    private DialogBox customizeDialogBox;
    private final HighScoresPanel highScorePanel;

    @Inject
    public ByoYomiLandingView(PlaceController placeController) {
        this.placeController = placeController;
        GWT.log("Creating byo yomi landing view");

        Grid outerGrid = new Grid(1, 2);

        Grid grid = new Grid(2, 2);

        grid.setWidget(0, 0, getDefaultPanel());
        grid.setWidget(0, 1, getSurvivalPanel());
        grid.setWidget(1, 0, getByoYomiPanel());
        grid.setWidget(1, 1, getCustomPanel());

        outerGrid.setWidget(0, 0, grid);

        highScorePanel = new HighScoresPanel();
        outerGrid.setWidget(0, 1, highScorePanel);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                grid.getCellFormatter().setWidth(i, j, "400px");
                grid.getCellFormatter().setHeight(i, j, "200px");
                grid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
            }
        }


        initWidget(outerGrid);
    }

    private Widget getDefaultPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>High score</b>"));
        defaultPanelInternal.add(new HTML("Experience the default survival settings and compete for high scores!"));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.alarmClockRed()));
        defaultPanelInternal.add(new Button("Play!",
                (ClickHandler) clickEvent -> placeController.goTo(new ByoYomiPlace())));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new HTML("Solve as many problems as you can within 5 minutes, the difficulty will " +
                "gradually increase."));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getSurvivalPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Survival mode</b>"));
        defaultPanelInternal.add(new HTML("A more relaxed experience with no time pressure."));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.puzzleBlue()));
        defaultPanelInternal.add(new Button("Play!",
                (ClickHandler) clickEvent -> placeController.goTo(new ByoYomiPlace(3, 5, 0, false, 0, 0))));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new HTML("Solve problems with no time limit, until you get 3 wrong answers! The " +
                "difficulty will " +
                "gradually increase."));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getByoYomiPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Byo-yomi mode</b>"));
        defaultPanelInternal.add(new HTML("Perfect training for tough byo-yomi endgames!"));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.speakerGreen()));
        defaultPanelInternal.add(new Button("Play!",
                (ClickHandler) clickEvent -> placeController.goTo(new ByoYomiPlace(3, 5, 0, false, 30, 0))));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new HTML("You only have 30 seconds to make each move! Keep solving problems until " +
                "you get 3 wrong answers. The difficulty will " +
                "gradually increase."));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }

    private Widget getCustomPanel() {
        DecoratorPanel defaultPanel = new DecoratorPanel();
        FlowPanel defaultPanelInternal = new FlowPanel();
        defaultPanelInternal.add(new HTML("<b>Custom mode</b>"));
        defaultPanelInternal.add(new HTML("Choose the settings that you prefer!"));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new Image(resources.wrenchYellow()));
        defaultPanelInternal.add(new Button("Customize", (ClickHandler) clickEvent -> {
            if (customizeDialogBox == null) {
                customizeDialogBox = createCustomizeDialogBox();
            }
            customizeDialogBox.center();
            customizeDialogBox.show();
        }));
        defaultPanelInternal.add(new HTML("<br>"));
        defaultPanelInternal.add(new HTML("Fully configure all parameters for a customized training."));
        defaultPanel.setWidget(defaultPanelInternal);
        defaultPanelInternal.setWidth("400px");
        defaultPanelInternal.setHeight("200px");
        return defaultPanel;
    }


    private DialogBox createCustomizeDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Customize mode");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        CustomizeSurvivalPanel panel = new CustomizeSurvivalPanel();
        dialogContents.add(panel);
        dialogContents.setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_CENTER);

        Button closeButton = new Button("Play!", (ClickHandler) event -> {
            dialogBox.hide();
            placeController.goTo(panel.getCustomizedPlace());
        });
        dialogContents.add(closeButton);

        dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }


    public void activate(final EventBus eventBus) {
        GWT.log("byo yomi landing view");
        highScorePanel.activate(eventBus);
    }

}
