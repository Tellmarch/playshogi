package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemOptionsPanelBeta;
import org.dominokit.domino.ui.Typography.Paragraph;
import org.dominokit.domino.ui.animations.Animation;
import org.dominokit.domino.ui.animations.Transition;
import org.dominokit.domino.ui.dialogs.MessageDialog;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.utils.DominoWindow;

@Singleton
public class TsumeView extends Composite {

    private static final String TSUME = "tsume";

    private static final int[] MOVES = {1, 3, 5, 7, 9, 11, 13, 15};

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final ProblemOptionsPanelBeta problemOptionsPanelBeta;

    @Inject
    public TsumeView() {
        GWT.log("Creating tsume view");
        shogiBoard = new ShogiBoard(TSUME);
        gameNavigator = new GameNavigator(TSUME);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, true);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);

        Button whatIsTsumeshogi = new Button("What is tsumeshogi?", (ClickHandler) clickEvent -> {
            Icon help =
                    Icons.ALL.help().style().add(Styles.font_72, Styles.m_b_15, Color.GREEN.getStyle()).get();
            MessageDialog customHeaderContent =
                    MessageDialog.createMessage(
                            "What is tsumeshogi?",
                            "",
                            () -> Notification.create("Dialog closed").show())
                            .appendChild(Paragraph.create("Tsumeshogi are checkmate problems, where you have to " +
                                    "capture the king.").bold())
                            .appendChild(Paragraph.create("Additionally, you have to follow the rules:").alignLeft())
                            .appendChild(Paragraph.create(" * each move needs to be a check (threatening to " +
                                    "capture the king),").alignLeft())
                            .appendChild(Paragraph.create(" * the king side escapes most efficiently, making the " +
                                    "longest possible line, while").alignLeft())
                            .appendChild(Paragraph.create(" * forcing the attacking side to use the most (ideally" +
                                    " all) pieces in hand.").alignLeft())
                            .appendChild(Paragraph.create(" ").alignLeft())
                            .appendChild(Paragraph.create("Examples:").alignLeft().bold())
                            .appendChild(Paragraph.create(" * a tsume in 1 move is one attacking move leading to " +
                                    "mate.").alignLeft())
                            .appendChild(Paragraph.create(" * a tsume in 3 moves is a check, a defensive move, " +
                                    "finally checkmate.").alignLeft())
                            .appendChild(Paragraph.create("This pattern follows the same way for longer moves. " +
                                    "Tsumeshogi always have an odd number of moves.").alignLeft())
                            .addOpenListener(
                                    () ->
                                            Animation.create(help)
                                                    .duration(400)
                                                    .repeat(2)
                                                    .transition(Transition.PULSE)
                                                    .animate())
                            .appendHeaderChild(help);
            customHeaderContent.open();
        });
        FlowPanel lowerLeftPanel = new FlowPanel();
        lowerLeftPanel.add(whatIsTsumeshogi);

        shogiBoard.setLowerLeftPanel(lowerLeftPanel);

        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        gameNavigator.getNavigatorConfiguration().setProblemMode(true);

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(shogiBoard);
        problemOptionsPanelBeta = new ProblemOptionsPanelBeta(MOVES);
        panel.add(problemOptionsPanelBeta);

        initWidget(panel);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        problemOptionsPanelBeta.activate(eventBus);
    }

}
