package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public class GameNavigation {

    private GameTree gameTree;
    private Node currentNode;
    private ShogiPosition position;
    private final ShogiRulesEngine gameRulesEngine;

    public GameNavigation(final ShogiRulesEngine gameRulesEngine, final GameTree gameTree,
                          final ShogiPosition startPosition) {
        this.gameRulesEngine = gameRulesEngine;
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        if (currentNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) currentNode.getMove();
            this.position = (ShogiPosition) editMove.getPosition().clonePosition();
        } else {
            this.position = (ShogiPosition) startPosition.clonePosition();
        }
    }

    public ShogiPosition getPosition() {
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

    public boolean isEndOfVariation() {
        return !currentNode.hasChildren() || getMainVariationMove().isEndMove();
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

    public void setGameTree(final GameTree gameTree, final ShogiPosition startingPosition, final int goToMove) {
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        this.position = (ShogiPosition) startingPosition.clonePosition();

        for (int i = 0; i < goToMove; i++) {
            moveForward();
        }
    }
}
