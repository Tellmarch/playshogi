package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionProblemsEvent;
import com.playshogi.website.gwt.client.events.puzzles.*;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardButtons;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;
import org.dominokit.domino.ui.utils.DominoElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.List;

@Singleton
public class ProblemsView extends Composite {

    private static final String PROBLEMS = "problems";
    private HtmlContentBuilder<HTMLElement> timerTextSeconds;
    private HtmlContentBuilder<HTMLElement> timerTextMs;

    interface MyEventBinder extends EventBinder<ProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final Tree<ProblemDetails> problemsTree;
    private final SessionInformation sessionInformation;
    private final PlaceController placeController;
    private final ScrollPanel scrollPanel;
    private HtmlContentBuilder<HTMLElement> timerText;
    private EventBus eventBus;
    private Button startTimedRun;
    private Button stopTimedRun;
    private TreeItem<ProblemDetails> current;

    @Inject
    public ProblemsView(final SessionInformation sessionInformation, final PlaceController placeController) {
        this.sessionInformation = sessionInformation;
        this.placeController = placeController;
        GWT.log("Creating Problems view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, false);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);
        gameNavigator.getNavigatorConfiguration().setProblemMode(true);


        problemsTree = Tree.create("Problems", null);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        scrollPanel = new ScrollPanel();
        scrollPanel.setSize("250px", "600px");
        scrollPanel.add(new ElementWidget(problemsTree.element()));
        horizontalPanel.add(scrollPanel);
        horizontalPanel.add(shogiBoard);

        initWidget(horizontalPanel);
    }

    private Widget createLowerLeftPanel() {
        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.add(BoardButtons.createSettingsButton(shogiBoard));
        div.add(BoardButtons.createClearArrowsButton(shogiBoard));
        div.add(Elements.br());
        startTimedRun = Button.createPrimary(Icons.ALL.timer()).setContent("Start timed " +
                "run")
                .addClickListener(evt -> {
                    if (!sessionInformation.isLoggedIn()) {
                        Window.alert("You are not logged in - your score will not be saved.");
                    }
                    eventBus.fireEvent(new StartTimedRunEvent());
                    startTimedRun.hide();
                    stopTimedRun.show();
                    timerText.hidden(false);
                })
                .style().setMarginTop("3em").setMarginBottom("3em")
                .get();
        stopTimedRun = Button.createDanger(Icons.ALL.timer_off()).setContent("Stop timer")
                .addClickListener(evt -> {
                    eventBus.fireEvent(new StopTimedRunEvent());
                    stopTimedRun.hide();
                    startTimedRun.show();
                })
                .style().setMarginTop("3em").setMarginBottom("3em")
                .get();
        stopTimedRun.hide();
        div.add(startTimedRun);
        div.add(stopTimedRun);
        div.add(Elements.br());
        timerText = Elements.b();
        DominoElement.of(timerText).style()
                .setBackgroundColor(Color.WHITE.getHex())
                .setPadding("0.5em")
                .setFontSize("30px");
        timerTextSeconds = Elements.span();
        timerTextMs = Elements.span();
        timerText.add(timerTextSeconds.textContent("0:00"))
                .add(timerTextMs.textContent(".00").style("font-size:20px"));
        timerText.hidden(true);
        div.add(timerText);
        return new ElementWidget(div.element());
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ProblemsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        timerText.hidden(true);
        startTimedRun.show();
        stopTimedRun.hide();
    }

    @EventHandler
    public void onListCollectionProblemsEvent(final ListCollectionProblemsEvent event) {
        GWT.log("ProblemsView: handle ListCollectionProblemsEvent");

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
        GWT.log("ProblemsView: handle ProblemCollectionProgressEvent");
        List<TreeItem<ProblemDetails>> subItems = problemsTree.getSubItems();
        for (int i = 0; i < subItems.size(); i++) {
            TreeItem<ProblemDetails> subItem = subItems.get(i);
            switch (event.getStatuses()[i]) {
                case CURRENT:
                    subItem.style().setBackgroundColor(Color.YELLOW.getHex());
                    subItem.select();
                    current = subItem;
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

}
