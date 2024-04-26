package com.playshogi.website.gwt.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.*;
import com.playshogi.website.gwt.client.events.kifu.ArrowDrawnEvent;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.widget.gamenavigator.NavigatorConfiguration;

public class NavigationController {


    interface MyEventBinder extends EventBinder<NavigationController> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final String activityId;
    private final NavigatorConfiguration navigatorConfiguration;
    private final GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), new GameTree());

    private EventBus eventBus;

    public NavigationController(final String activityId) {
        this(activityId, new NavigatorConfiguration());
    }

    public NavigationController(final String activityId, final NavigatorConfiguration navigatorConfiguration) {
        GWT.log(activityId + ": Creating NavigationController");
        this.activityId = activityId;
        this.navigatorConfiguration = navigatorConfiguration;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Navigation controller");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    private void fireNodeChanged() {
        eventBus.fireEvent(new NodeChangedEvent());
    }

    private void firePositionChanged(final boolean triggeredByUser) {
        GWT.log(activityId + " GameNavigator: firing position changed");
        eventBus.fireEvent(new PositionChangedEvent(gameNavigation.getPosition(),
                gameNavigation.getBoardDecorations(), gameNavigation.getPreviousMove(), triggeredByUser));
    }

    public GameNavigation getGameNavigation() {
        return gameNavigation;
    }

    public ReadOnlyShogiPosition getPosition() {
        return gameNavigation.getPosition();
    }

    public void fireProgress() {
        if (navigatorConfiguration.isFireVisitedProgress()) {
            GameTree.VisitedProgress progress = gameNavigation.getGameTree().getPercentVisited();
            eventBus.fireEvent(new VisitedProgressEvent(progress.visited, progress.total));
        }
    }

    @EventHandler
    public void onGameTreeChanged(final GameTreeChangedEvent gameTreeChangedEvent) {
        GWT.log(activityId + " NavigationController: Handling game tree changed event - move " + gameTreeChangedEvent.getGoToMove());
        gameNavigation.setGameTree(gameTreeChangedEvent.getGameTree(), gameTreeChangedEvent.getGoToMove());
        firePositionChanged(false);
        fireProgress();
    }

    @EventHandler
    public void onEditMovePlayed(final EditMovePlayedEvent event) {
        GWT.log(activityId + " NavigationController: Handling EditMovePlayedEvent");
        gameNavigation.addMove(event.getMove(), true);
        firePositionChanged(true);
        eventBus.fireEvent(new NewVariationPlayedEvent(gameNavigation.getPosition()));
    }

    @EventHandler
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        GWT.log(activityId + " NavigationController: Handling move played event");
        ShogiMove move = movePlayedEvent.getMove();
        GWT.log("Move played: " + move.toString());
        boolean existingMove = gameNavigation.hasMoveInCurrentPosition(move);

        gameNavigation.addMove(move, true);
        if (!existingMove) {
            GWT.log("New variation");
            eventBus.fireEvent(new NewVariationPlayedEvent(gameNavigation.getPosition()));
        } else if (gameNavigation.isEndOfVariation()) {
            eventBus.fireEvent(new EndOfVariationReachedEvent(gameNavigation.getPosition(),
                    gameNavigation.getCurrentNode().isNew(), gameNavigation.getCurrentNode().isWrongAnswer()));
            fireNodeChanged();
        } else if (navigatorConfiguration.isProblemMode() &&
                gameNavigation.getPosition().getPlayerToMove() != gameNavigation.getGameTree().getInitialPosition().getPlayerToMove()) {
            gameNavigation.moveForward();
            if (gameNavigation.isEndOfVariation()) {
                eventBus.fireEvent(new EndOfVariationReachedEvent(gameNavigation.getPosition(),
                        gameNavigation.getCurrentNode().isNew(), gameNavigation.getCurrentNode().isWrongAnswer()));
            }
            fireNodeChanged();
        }

        firePositionChanged(true);
        fireProgress();
    }

    @EventHandler
    public void onInsertVariationEvent(final InsertVariationEvent event) {
        GWT.log(activityId + " NavigationController: handling InsertVariationEvent");

        String[] usfMoves = event.getSelectedVariation().getPrincipalVariation().trim().split(" ");
        for (String usfMove : usfMoves) {
            gameNavigation.addMove(UsfMoveConverter.fromUsfString(usfMove, gameNavigation.getPosition()), true);
        }
        for (int i = 0; i < usfMoves.length - 1; i++) {
            gameNavigation.moveBack();
        }

        eventBus.fireEvent(new NewVariationPlayedEvent(gameNavigation.getPosition()));
        firePositionChanged(true);
    }

    @EventHandler
    public void onClearDecorations(final ClearDecorationsEvent event) {
        GWT.log(activityId + " NavigationController: Handling ClearDecorationsEvent");
        gameNavigation.getCurrentNode().setObjects(null);
        firePositionChanged(true);
    }

    @EventHandler
    public void onArrowDrawnEvent(final ArrowDrawnEvent event) {
        GWT.log(activityId + " NavigationController: Handling ArrowDrawnEvent");
        gameNavigation.getCurrentNode().addArrow(event.getArrow());
    }


    @EventHandler
    public void onNavigateToStart(final NavigateToStartEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateToStartEvent");
        gameNavigation.moveToStart();
        eventBus.fireEvent(new UserNavigatedBackEvent());
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateToEnd(final NavigateToEndEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateToEndEvent");
        gameNavigation.moveToEndOfVariation();
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateForward(final NavigateForwardEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateForwardEvent");
        gameNavigation.moveForward();
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateBack(final NavigateBackEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateBackEvent");
        gameNavigation.moveBack();
        eventBus.fireEvent(new UserNavigatedBackEvent());
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateNext(final NavigateNextEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateNextEvent");
        if (gameNavigation.canMoveForward()) {
            gameNavigation.moveForwardInFirstUnvisitedVariation();
        } else {
            gameNavigation.moveBackToNodeWithUnvisitedOptions();
        }
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateStartVariation(final NavigateStartVariationEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateStartVariationEvent");
        gameNavigation.moveToStartOfVariation();
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }

    @EventHandler
    public void onNavigateStartVariation(final NavigateStartUnvisitedVariationEvent event) {
        GWT.log(activityId + " NavigationController: Handling NavigateStartVariationEvent");
        gameNavigation.moveBackToNodeWithUnvisitedOptions();
        firePositionChanged(true);
        fireNodeChanged();
        fireProgress();
    }
}
