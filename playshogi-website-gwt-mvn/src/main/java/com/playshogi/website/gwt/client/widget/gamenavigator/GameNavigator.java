package com.playshogi.website.gwt.client.widget.gamenavigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.*;

import java.util.Objects;

public class GameNavigator extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<GameNavigator> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Button firstButton;
    private final Button previousButton;
    private final Button nextButton;
    private final Button lastButton;
    private final GameNavigation gameNavigation;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

    private EventBus eventBus;

    private final NavigatorConfiguration navigatorConfiguration;

    private final String activityId;

    public GameNavigator(final String activityId) {
        this(activityId, new NavigatorConfiguration(), new GameNavigation(new ShogiRulesEngine(), new GameTree()));
    }

    public GameNavigator(final String activityId, final GameNavigation gameNavigation) {
        this(activityId, new NavigatorConfiguration(), gameNavigation);
    }

    private GameNavigator(final String activityId, final NavigatorConfiguration navigatorConfiguration,
                          final GameNavigation gameNavigation) {
        GWT.log(activityId + ": Creating game navigator");

        this.activityId = activityId;
        this.navigatorConfiguration = navigatorConfiguration;
        this.gameNavigation = gameNavigation;

        firstButton = new Button("<<");
        previousButton = new Button("<");
        nextButton = new Button(">");
        lastButton = new Button(">>");

        firstButton.addClickHandler(this);
        previousButton.addClickHandler(this);
        nextButton.addClickHandler(this);
        lastButton.addClickHandler(this);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(firstButton);
        horizontalPanel.add(previousButton);
        horizontalPanel.add(nextButton);
        horizontalPanel.add(lastButton);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Game Navigator");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
        Scheduler.get().scheduleDeferred(() -> {
            GWT.log(activityId + ": Game Navigator deferred execution");
            firePositionChanged(false);
        });
    }

    public void reset(final Handicap handicap) {
        reset(ShogiInitialPositionFactory.createInitialPosition(handicap));
    }

    public void reset(final ShogiPosition position) {
        gameNavigation.setGameTree(new GameTree(position), 0);

        firePositionChanged(false);
    }

    @EventHandler
    public void onGameTreeChanged(final GameTreeChangedEvent gameTreeChangedEvent) {
        GWT.log(activityId + " GameNavigator: Handling game tree changed event - move " + gameTreeChangedEvent.getGoToMove());
        GameTree gameTree = gameTreeChangedEvent.getGameTree();
        gameNavigation.setGameTree(gameTree, gameTreeChangedEvent.getGoToMove());

        firePositionChanged(false);
    }

    @EventHandler
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        GWT.log(activityId + " GameNavigator: Handling move played event");
        ShogiMove move = movePlayedEvent.getMove();
        GWT.log("Move played: " + move.toString());
        boolean existingMove = gameNavigation.hasMoveInCurrentPosition(move);
        boolean mainMove = Objects.equals(gameNavigation.getMainVariationMove(), move);

        gameNavigation.addMove(move);
        if (!existingMove) {
            GWT.log("New variation");
            boolean positionCheckmate = shogiRulesEngine.isPositionCheckmate(gameNavigation.getPosition());
            if (move instanceof DropMove) {
                DropMove dropMove = (DropMove) move;
                if (dropMove.getPieceType() == PieceType.PAWN) {
                    positionCheckmate = false;
                }
            }
            GWT.log("Checkmate: " + positionCheckmate);
            eventBus.fireEvent(new NewVariationPlayedEvent(positionCheckmate));
        } else if (gameNavigation.isEndOfVariation()) {
            eventBus.fireEvent(new EndOfVariationReachedEvent(mainMove));
            // } else if (isSenteToPlay() &&
            // !boardConfiguration.isPlaySenteMoves()) {
            // gameNavigation.moveForward();
        } else if (gameNavigation.getPosition().getPlayerToMove() == Player.WHITE && navigatorConfiguration.isProblemMode()) {
            gameNavigation.moveForward();
        }

        firePositionChanged(true);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == firstButton) {
            gameNavigation.moveToStart();
            eventBus.fireEvent(new UserNavigatedBackEvent());
        } else if (source == nextButton) {
            gameNavigation.moveForward();
        } else if (source == previousButton) {
            gameNavigation.moveBack();
            eventBus.fireEvent(new UserNavigatedBackEvent());
        } else if (source == lastButton) {
            gameNavigation.moveToEndOfVariation();
        }
        firePositionChanged(true);
    }

    private void firePositionChanged(final boolean triggeredByUser) {
        GWT.log(activityId + " GameNavigator: firing position changed");
        eventBus.fireEvent(new PositionChangedEvent(gameNavigation.getPosition(), triggeredByUser));
    }

    public NavigatorConfiguration getNavigatorConfiguration() {
        return navigatorConfiguration;
    }

    public GameNavigation getGameNavigation() {
        return gameNavigation;
    }

    public void addMove(final ShogiMove move, final boolean fromUser) {
        gameNavigation.addMove(move);
        firePositionChanged(fromUser);
    }

}
