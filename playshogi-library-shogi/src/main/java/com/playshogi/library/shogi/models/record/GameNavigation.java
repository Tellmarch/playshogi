package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Optional;

public class GameNavigation {

    private GameTree gameTree;
    private Node currentNode;
    private ShogiPosition position;
    private final ShogiRulesEngine gameRulesEngine;

    public GameNavigation(final ShogiRulesEngine gameRulesEngine, final GameTree gameTree) {
        this.gameRulesEngine = gameRulesEngine;
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        this.position = gameTree.getInitialPosition().clonePosition();
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

    public Optional<String> getCurrentComment() {
        return currentNode.getComment();
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
            Move move = currentNode.getMove();
            if (move instanceof EditMove) {
                moveToNode(currentNode.getParent());
            } else if (move instanceof ShogiMove) {
                gameRulesEngine.undoMoveInPosition(position, (ShogiMove) move);
            }
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
            playMove(currentNode.getMove());
        }
    }

    private void playMove(final Move move) {
        if (move instanceof EditMove) {
            position = ((EditMove) move).getPosition().clonePosition();
        } else if (move instanceof ShogiMove) {
            gameRulesEngine.playMoveInPosition(position, (ShogiMove) move);
        }
    }

    /**
     * Useful for USF processing
     */
    public void moveForwardInLastVariation() {
        if (canMoveForward()) {
            currentNode = currentNode.getChildren().get(currentNode.getChildren().size() - 1);
            playMove(currentNode.getMove());
        }
    }

    /**
     * Go the the n-th node following the last branch at every move. Useful for
     * USF processing
     */
    public void goToNodeUSF(final int nodeNumber) {
        moveToStart();
        for (int j = 0; j < nodeNumber; j++) {
            moveForwardInLastVariation();
        }
    }

    public void moveToStart() {
        position = gameTree.getInitialPosition().clonePosition();
        currentNode = gameTree.getRootNode();
    }

    public void moveToEndOfVariation() {
        while (canMoveForward()) {
            moveForward();
        }
    }

    public void moveToNode(final Node node) {
        if (node.getParent() == null) {
            moveToStart();
        } else {
            moveToNode(node.getParent());
            currentNode = node;
            playMove(node.getMove());
        }
    }

    public boolean hasMoveInCurrentPosition(final Move move) {
        return currentNode.getChildWithMove(move) != null;
    }

    public void addMove(final Move move) {
        Node childNode = currentNode.getChildWithMove(move);
        if (childNode == null) {
            Node newNode = new Node(move);
            currentNode.addChild(newNode);
            currentNode = newNode;
        } else {
            currentNode = childNode;
        }
        playMove(move);
    }

    public GameTree getGameTree() {
        return gameTree;
    }

    public void setGameTree(final GameTree gameTree, final int goToMove) {
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        this.position = gameTree.getInitialPosition().clonePosition();

        for (int i = 0; i < goToMove; i++) {
            moveForward();
        }
    }
}
