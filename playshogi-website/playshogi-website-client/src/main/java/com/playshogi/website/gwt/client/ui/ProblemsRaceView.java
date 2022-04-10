package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.events.collections.ListCollectionProblemsEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.puzzles.ActivityTimerEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemCollectionProgressEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserJumpedToProblemEvent;
import com.playshogi.website.gwt.client.events.races.RaceEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardButtons;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.NavigatorConfiguration;
import com.playshogi.website.gwt.client.widget.problems.PreRacePopup;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.RaceDetails;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;
import org.dominokit.domino.ui.utils.DominoElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ProblemsRaceView extends Composite {

    private static final String PROBLEMS = "problemsRace";

    interface MyEventBinder extends EventBinder<ProblemsRaceView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final Tree<ProblemDetails> problemsTree;
    private final SessionInformation sessionInformation;
    private final ScrollPanel scrollPanel;
    private final NavigationController navigationController;
    private final TextArea textArea;
    private final PreRacePopup preRacePopup;
    private final AbsolutePanel raceStatusPanel;
    private EventBus eventBus;
    private HtmlContentBuilder<HTMLElement> timerTextSeconds;
    private HtmlContentBuilder<HTMLElement> timerTextMs;

    @Inject
    public ProblemsRaceView(final SessionInformation sessionInformation) {
        this.sessionInformation = sessionInformation;
        GWT.log("Creating Problems Race view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(PROBLEMS, NavigatorConfiguration.PROBLEMS);
        gameNavigatorPanel = new GameNavigatorPanel(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigatorPanel, false);
        preRacePopup = new PreRacePopup(sessionInformation);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        problemsTree = Tree.create("Problems", null);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        scrollPanel = new ScrollPanel();
        scrollPanel.setSize("250px", "600px");
        scrollPanel.add(new ElementWidget(problemsTree.element()));
        horizontalPanel.add(scrollPanel);

        textArea = createCommentsArea();
        VerticalPanel boardAndTextPanel = new VerticalPanel();
        boardAndTextPanel.add(shogiBoard);
        boardAndTextPanel.add(textArea);

        raceStatusPanel = new AbsolutePanel();
        raceStatusPanel.setSize("800px", "400px");
        boardAndTextPanel.add(raceStatusPanel);

        horizontalPanel.add(boardAndTextPanel);

        initWidget(horizontalPanel);
    }

    private TextArea createCommentsArea() {
        final TextArea textArea;
        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        textArea.setEnabled(false);
        return textArea;
    }

    private Widget createLowerLeftPanel() {
        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.add(BoardButtons.createSettingsButton(shogiBoard));
        div.add(BoardButtons.createClearArrowsButton(shogiBoard));
        div.add(Elements.br());
        div.add(Elements.br());
        HtmlContentBuilder<HTMLElement> timerText = Elements.b();
        DominoElement.of(timerText).style()
                .setBackgroundColor(Color.WHITE.getHex())
                .setPadding("0.5em")
                .setFontSize("30px");
        timerTextSeconds = Elements.span();
        timerTextMs = Elements.span();
        timerText.add(timerTextSeconds.textContent("0:00"))
                .add(timerTextMs.textContent(".00").style("font-size:20px"));
        div.add(timerText);
        return new ElementWidget(div.element());
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ProblemsRaceView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigatorPanel.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        navigationController.activate(eventBus);
        preRacePopup.activate(eventBus);

        preRacePopup.show();
    }

    @EventHandler
    public void onListCollectionProblemsEvent(final ListCollectionProblemsEvent event) {
        GWT.log("ProblemsRaceView: handle ListCollectionProblemsEvent");

        problemsTree.setTitle(event.getCollectionDetails().getName());

        for (TreeItem<ProblemDetails> subItem : problemsTree.getSubItems()) {
            problemsTree.removeItem(subItem);
        }

        ProblemDetails[] details = event.getDetails();
        for (int i = 0; i < details.length; i++) {
            ProblemDetails detail = details[i];
            int finalI = i;
            problemsTree.appendChild(TreeItem.create("Problem " + (i + 1), detail).addClickListener(
                    evt -> eventBus.fireEvent(new UserJumpedToProblemEvent(finalI))
            ));
        }
    }

    @EventHandler
    public void onProblemCollectionProgressEvent(final ProblemCollectionProgressEvent event) {
        GWT.log("ProblemsRaceView: handle ProblemCollectionProgressEvent");
        List<TreeItem<ProblemDetails>> subItems = problemsTree.getSubItems();
        for (int i = 0; i < subItems.size(); i++) {
            TreeItem<ProblemDetails> subItem = subItems.get(i);
            switch (event.getStatuses()[i]) {
                case CURRENT:
                    subItem.style().setBackgroundColor(Color.YELLOW.getHex());
                    subItem.select();
                    if (i < 10) {
                        scrollPanel.setVerticalScrollPosition(0);
                    } else {
                        HTMLLIElement element = subItems.get(Math.max(0, i - 10)).element();
                        scrollPanel.setVerticalScrollPosition(element.offsetTop);
                    }
                    break;
                case SOLVED:
                    subItem.style().setBackgroundColor(Color.GREEN_LIGHTEN_2.getHex());
                    break;
                case FAILED:
                    subItem.style().setBackgroundColor(Color.RED_LIGHTEN_2.getHex());
                    break;
                case UNSOLVED:
                    subItem.style().setBackgroundColor(Color.GREY_LIGHTEN_4.getHex());
                    break;
            }
        }
    }

    @EventHandler
    void onActivityTimerEvent(final ActivityTimerEvent event) {
        int timeMs = (event.getTimems() % 1000 / 10);
        int timeInSeconds = event.getTimems() / 1000;
        int timeMinutes = timeInSeconds / 60;
        int timeSeconds = timeInSeconds % 60;
        timerTextSeconds.textContent(timeMinutes + ":" + (timeSeconds < 10 ? "0" + timeSeconds : timeSeconds));
        timerTextMs.textContent("." + (timeMs < 10 ? "0" + timeMs : timeMs));
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("ProblemsRaceView: handle PositionChangedEvent");

        Optional<String> comment = navigationController.getGameNavigation().getCurrentComment();
        if (comment.isPresent()) {
            textArea.setText(comment.get());
            textArea.setVisible(true);
        } else {
            textArea.setText("");
            textArea.setVisible(false);
        }
    }

    @EventHandler
    public void onRaceEvent(final RaceEvent event) {
        GWT.log("ProblemsRaceView: handle RaceEvent");
        RaceDetails raceDetails = event.getRaceDetails();
        boolean isRaceOwner = Objects.equals(sessionInformation.getUsername(), raceDetails.getOwner());
        GWT.log("is race owner: " + isRaceOwner);

        RaceDetails.ProblemStatus[][] playerProgresses = raceDetails.getPlayerProgresses();
        String[] players = raceDetails.getPlayers();

        raceStatusPanel.clear();
        for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
            raceStatusPanel.add(new HTML(players[playerIndex]), 0, 20 * playerIndex);
            String progress = "";
            for (RaceDetails.ProblemStatus status : playerProgresses[playerIndex]) {
                progress += getChar(status);
            }
            raceStatusPanel.add(new HTML(progress), 100, 20 * playerIndex);
        }
    }

    private String getChar(final RaceDetails.ProblemStatus status) {
        switch (status) {
            case ATTEMPTING:
                return ".";
            case SOLVED:
                return "O";
            case FAILED:
                return "X";
            case SKIPPED:
                return "_";
            case NOT_ATTEMPTED:
            default:
                return " ";
        }
    }
}
