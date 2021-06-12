package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.themes.Theme;

@Singleton
public class ProblemView extends Composite {

    private static final String PROBLEMS = "problems";

    interface MyEventBinder extends EventBinder<ProblemView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private EventBus eventBus;

    @Inject
    public ProblemView(final SessionInformation sessionInformation) {
        GWT.log("Creating Problem view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, false);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);
        gameNavigator.getNavigatorConfiguration().setProblemMode(true);

        initWidget(shogiBoard);
    }

    private FlowPanel createLowerLeftPanel() {

        FlowPanel panel = new FlowPanel();
        panel.add(new ElementWidget(org.dominokit.domino.ui.button.Button.createPrimary(Icons.ALL.settings_mdi())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .setTooltip("Settings")
                .addClickListener(e -> shogiBoard.getBoardSettingsPanel().showInDialog()).element()));
        panel.add(new ElementWidget(org.dominokit.domino.ui.button.Button.createPrimary(Icons.ALL.do_not_disturb_alt())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .addClickListener(e -> eventBus.fireEvent(new ClearDecorationsEvent()))
                .setTooltip("Clear arrows")
                .style().setMarginLeft("1em").element()));
        return panel;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ProblemsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
    }

}
