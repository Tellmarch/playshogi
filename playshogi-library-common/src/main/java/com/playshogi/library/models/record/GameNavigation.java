package com.playshogi.library.models.record;

import com.playshogi.library.models.EditMove;
import com.playshogi.library.models.Move;
import com.playshogi.library.models.Position;
import com.playshogi.library.models.games.GameRulesEngine;

public class GameNavigation<P extends Position<P>> {

    private GameTree gameTree;
    private Node currentNode;
    private P position;
    private final GameRulesEngine<P> gameRulesEngine;

    @SuppressWarnings("unchecked")
    public GameNavigation(final GameRulesEngine<P> gameRulesEngine, final GameTree gameTree, final P startPosition) {
        this.gameRulesEngine = gameRulesEngine;
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        if (currentNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) currentNode.getMove();
            this.position = (P) editMove.getPosition();
        } else {
            this.position = startPosition;
        }
    }

    public P getPosition() {
        return position;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Move getCurrentMove() {
        return currentNode.getMove();
    }

    public boolean canMoveBack() {
        return currentNode.getParent() != null;
    }

    public boolean canMoveForward() {
        return currentNode.hasChildren();
    }

    public void moveBack() {
        if (canMoveBack()) {
            gameRulesEngine.undoMoveInPosition(position, currentNode.getMove());
            currentNode = currentNode.getParent();
        }
    }

    public Move getMainVariationMove() {
        if (canMoveForward()) {
            return currentNode.getChildren().get(0).getMove();
        } else {
            return null;
        }
    }

    public void moveForward() {
        if (canMoveForward()) {
            currentNode = currentNode.getChildren().get(0);
            gameRulesEngine.playMoveInPosition(position, currentNode.getMove());
        }
    }

    /**
     * Useful for USF processing
     */
    public void moveForwardInLastVariation() {
        if (canMoveForward()) {
            currentNode = currentNode.getChildren().get(currentNode.getChildren().size() - 1);
            gameRulesEngine.playMoveInPosition(position, currentNode.getMove());
        }
    }

    /**
     * Go the the n-th node following the last branch at every move. Useful for
     * USF processing
     */
    public void goToNodeUSF(final int moveNumber) {
        moveToStart();
        for (int j = 0; j < moveNumber; j++) {
            moveForwardInLastVariation();
        }
    }

    public void moveToStart() {
        while (canMoveBack()) {
            moveBack();
        }
    }

    public void moveToEndOfVariation() {
        while (canMoveForward()) {
            moveForward();
        }
    }

    public boolean hasMoveInCurrentPosition(final Move move) {
        return currentNode.getChildWithMove(move) != null;
    }

    public void addMove(final Move move) {
        Node childNode = currentNode.getChildWithMove(move);
        if (childNode == null) {
            Node newNode = new Node(move);
            newNode.setParent(currentNode);
            currentNode.addChild(newNode);
            currentNode = newNode;
        } else {
            currentNode = childNode;
        }
        gameRulesEngine.playMoveInPosition(position, move);
    }

    public GameTree getGameTree() {
        return gameTree;
    }

    @SuppressWarnings("unchecked")
    public void setGameTree(final GameTree gameTree) {
        moveToStart();
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        if (currentNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) currentNode.getMove();
            this.position = (P) editMove.getPosition();
        }
    }
}
