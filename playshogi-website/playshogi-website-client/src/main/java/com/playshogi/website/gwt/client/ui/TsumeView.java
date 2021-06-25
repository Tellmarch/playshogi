package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardButtons;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemOptionsPanel2;
import org.dominokit.domino.ui.Typography.Paragraph;
import org.dominokit.domino.ui.animations.Animation;
import org.dominokit.domino.ui.animations.Transition;
import org.dominokit.domino.ui.dialogs.MessageDialog;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.themes.Theme;

@Singleton
public class TsumeView extends Composite {

    private static final String TSUME = "tsume";

    private static final int[] MOVES = {3, 5, 7, 9, 11, 13};

    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final ProblemOptionsPanel2 problemOptionsPanel;
    private final NavigationController navigationController;

    @Inject
    public TsumeView(final SessionInformation sessionInformation) {
        GWT.log("Creating tsume view");
        shogiBoard = new ShogiBoard(TSUME, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(TSUME, true);
        gameNavigatorPanel = new GameNavigatorPanel(TSUME);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigatorPanel, true);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);

        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());

        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(shogiBoard);
        problemOptionsPanel = new ProblemOptionsPanel2(MOVES);
        panel.add(problemOptionsPanel);

        initWidget(panel);
    }

    private FlowPanel createLowerLeftPanel() {

        FlowPanel panel = new FlowPanel();
        panel.add(BoardButtons.createSettingsWidget(shogiBoard));
        panel.add(BoardButtons.createClearArrowsWidget(shogiBoard));
        panel.add(new ElementWidget(org.dominokit.domino.ui.button.Button.createPrimary(Icons.ALL.help_outline())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .addClickListener(e -> getHelpDialog().open())
                .setTooltip("What is TsumeShogi?")
                .style().setMarginLeft("1em").element()));
        return panel;
    }

    private MessageDialog getHelpDialog() {
        Icon help =
                Icons.ALL.help().style().add(Styles.font_72, Styles.m_b_15, Color.GREEN.getStyle()).get();
        return MessageDialog.createMessage(
                "What is tsumeshogi?",
                "")
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
                .appendChild(Paragraph.create(" ").alignLeft())
                .appendChild(Paragraph.create("Futile interposition:").alignLeft().bold())
                .appendChild(Paragraph.create(" * When the king is checked from a distance (e.g. by a rook), it often" +
                        " happens that White can try to interpose pieces, but they would just be taken immediately " +
                        "without changing the outcome. In that case, such interposition moves are not included in the" +
                        " solution, and do not count for the problem number of moves.").alignLeft())
                .addOpenListener(
                        () ->
                                Animation.create(help)
                                        .duration(400)
                                        .repeat(2)
                                        .transition(Transition.PULSE)
                                        .animate())
                .appendHeaderChild(help);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigatorPanel.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        problemOptionsPanel.activate(eventBus);
        navigationController.activate(eventBus);
    }

}
