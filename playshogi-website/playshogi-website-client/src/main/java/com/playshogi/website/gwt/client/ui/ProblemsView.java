package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemCollectionProgressEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserJumpedToProblemEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.shared.models.GameDetails;
import elemental2.dom.HTMLLIElement;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;

import java.util.List;

@Singleton
public class ProblemsView extends Composite {

    private static final String PROBLEMS = "problems";

    interface MyEventBinder extends EventBinder<ProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final Tree<GameDetails> problemsTree;
    private final PlaceController placeController;
    private final ScrollPanel scrollPanel;
    private EventBus eventBus;

    @Inject
    public ProblemsView(final SessionInformation sessionInformation, final PlaceController placeController) {
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
    }

    @EventHandler
    public void onListCollectionGamesEvent(final ListCollectionGamesEvent event) {
        GWT.log("ProblemsView: handle GameCollectionsEvent");

        problemsTree.setTitle(event.getCollectionDetails().getName());

        for (TreeItem<GameDetails> subItem : problemsTree.getSubItems()) {
            problemsTree.removeItem(subItem);
        }

        GameDetails[] details = event.getDetails();
        for (int i = 0; i < details.length; i++) {
            GameDetails detail = details[i];
            int finalI = i;
            problemsTree.appendChild(TreeItem.create("Problem " + (i + 1), detail).addClickListener(
                    evt -> eventBus.fireEvent(new UserJumpedToProblemEvent(finalI))
            ));
        }
    }

    @EventHandler
    public void onProblemCollectionProgressEvent(final ProblemCollectionProgressEvent event) {
        GWT.log("ProblemsView: handle ProblemCollectionProgressEvent");
        GWT.log(event.toString());
        List<TreeItem<GameDetails>> subItems = problemsTree.getSubItems();
        for (int i = 0; i < subItems.size(); i++) {
            TreeItem<GameDetails> subItem = subItems.get(i);
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

}
