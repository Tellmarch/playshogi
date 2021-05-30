package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
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
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.shared.models.GameDetails;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;

@Singleton
public class ProblemsView extends Composite {

    private static final String PROBLEMS = "problems";
    private final PlaceController placeController;

    interface MyEventBinder extends EventBinder<ProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final Tree<GameDetails> problemsTree;

    @Inject
    public ProblemsView(final SessionInformation sessionInformation, final PlaceController placeController) {
        this.placeController = placeController;
        GWT.log("Creating Problems view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, false);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);
        gameNavigator.getNavigatorConfiguration().setProblemMode(true);


        problemsTree = Tree.create("Problems", null);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setSize("250px", "600px");
        scrollPanel.add(new ElementWidget(problemsTree.element()));
        horizontalPanel.add(scrollPanel);
        horizontalPanel.add(shogiBoard);

        initWidget(horizontalPanel);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ProblemsView");
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
            problemsTree.appendChild(TreeItem.create("Problem " + (i + 1), detail).addClickListener(evt -> placeController.goTo(new ProblemsPlace(event.getCollectionDetails().getId(), finalI))));
        }
    }

}
