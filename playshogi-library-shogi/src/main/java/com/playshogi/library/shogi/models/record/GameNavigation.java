package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.decorations.Arrow;
import com.playshogi.library.shogi.models.decorations.BoardDecorations;
import com.playshogi.library.shogi.models.decorations.Circle;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameNavigation {

    private GameTree gameTree;
    private Node currentNode;
    private ShogiPosition position;
    private final ShogiRulesEngine gameRulesEngine;

    public GameNavigation(final GameTree gameTree) {
        this(new ShogiRulesEngine(), gameTree);
    }

    public GameNavigation(final ShogiRulesEngine gameRulesEngine, final GameTree gameTree) {
        this.gameRulesEngine = gameRulesEngine;
        this.gameTree = gameTree;
        this.currentNode = gameTree.getRootNode();
        this.position = gameTree.getInitialPosition().clonePosition();
        currentNode.setVisited(true);
    }

    public ShogiPosition getPosition() {
        return position;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Player getPlayerToMove() {
        return getPosition().getPlayerToMove();
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
            currentNode.setVisited(true);
        }
    }

    public boolean hasVariations() {
        return currentNode.hasChildren() && currentNode.getChildren().size() > 1;
    }

    public boolean hasUnvisitedVariations() {
        return currentNode.hasChildren() && currentNode.getChildren().size() > 1
                && !currentNode.getChildren().stream().allMatch(Node::isVisited);
    }

    public void moveToStartOfVariation() {
        moveBack();
        while (canMoveBack() && !hasVariations()) {
            moveBack();
        }
    }

    public void moveBackToNodeWithUnvisitedOptions() {
        moveBack();
        while (canMoveBack() && !hasUnvisitedVariations()) {
            moveBack();
        }
    }

    public Move getMainVariationMove() {
        if (canMoveForward()) {
            return currentNode.getChildren().get(0).getMove();
        } else {
            return null;
        }
    }

    public Move getPreviousMove() {
        return currentNode.getMove();
    }

    public void moveForward() {
        moveForward(true);
    }

    private void moveForward(final boolean markVisited) {
        if (canMoveForward()) {
            currentNode = currentNode.getChildren().get(0);
            playMove(currentNode.getMove());
            if (markVisited) currentNode.setVisited(true);
        }
    }

    private void playMove(final Move move) {
        if (move instanceof EditMove) {
            position = ((EditMove) move).getPosition().clonePosition();
        } else if (move instanceof ShogiMove) {
            gameRulesEngine.playMoveInPosition(position, (ShogiMove) move);
        }
    }

    public Node getFirstUnvisitedVariation() {
        List<Node> children = currentNode.getChildren();
        for (int i = 1, childrenSize = children.size(); i < childrenSize; i++) {
            Node child = children.get(i);
            if (!child.isVisited()) {
                return child;
            }
        }

        if (children.isEmpty()) {
            return null;
        } else {
            return children.get(0);
        }
    }

    public void moveForwardInFirstUnvisitedVariation() {
        Node node = getFirstUnvisitedVariation();

        if (node != null) {
            currentNode = node;
            currentNode.setVisited(true);
            playMove(currentNode.getMove());
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
        currentNode.setVisited(true);
    }

    public void moveToEndOfVariation() {
        while (canMoveForward()) {
            moveForward(false);
        }
        currentNode.setVisited(true);
    }

    public void moveToNode(final Node node) {
        moveToNode(node, true);
    }

    private void moveToNode(final Node node, final boolean markVisited) {
        if (node.getParent() == null) {
            moveToStart();
        } else {
            moveToNode(node.getParent(), false);
            currentNode = node;
            playMove(node.getMove());
            if (markVisited) currentNode.setVisited(true);
        }
    }

    public boolean hasMoveInCurrentPosition(final Move move) {
        return currentNode.getChildWithMove(move) != null;
    }

    public void addMove(final Move move) {
        addMove(move, false);
    }

    public void addMove(final Move move, final MoveTiming moveTiming) {
        addMove(move, moveTiming, false);
    }

    public void addMove(final Move move, final boolean fromUser) {
        addMove(move, null, fromUser);
    }

    public void addMove(final Move move, final MoveTiming moveTiming, final boolean fromUser) {
        Node childNode = currentNode.getChildWithMove(move);
        if (childNode == null) {
            Node newNode = new Node(move);
            newNode.setTiming(moveTiming);
            if (fromUser) {
                newNode.setNew(true);
                newNode.setVisited(true);
            }
            currentNode.addChild(newNode);
            currentNode = newNode;
        } else {
            currentNode = childNode;
            if (fromUser) {
                currentNode.setVisited(true);
            }
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
            moveForward(false);
        }
        currentNode.setVisited(true);
    }

    public BoardDecorations getBoardDecorations() {
        Optional<String> objects = currentNode.getObjects();
        if (objects.isPresent()) {
            ArrayList<Arrow> arrows = new ArrayList<>();
            ArrayList<Circle> circles = new ArrayList<>();
            for (String object : objects.get().split("\n")) {
                if (object.startsWith("ARROW,")) {
                    arrows.add(Arrow.parseArrowObject(object));
                }
            }
            return new BoardDecorations(arrows, circles);
        } else {
            return null;
        }
    }
}
