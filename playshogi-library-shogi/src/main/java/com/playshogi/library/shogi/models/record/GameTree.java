package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;

public class GameTree {

    private final Node rootNode;

    public GameTree() {
        this(new Node(null));
    }

    public GameTree(final ReadOnlyShogiPosition startingPosition) {
        this(new Node(new EditMove(startingPosition)));
    }

    public GameTree(final Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public ReadOnlyShogiPosition getInitialPosition() {
        if (rootNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) rootNode.getMove();
            return editMove.getPosition();
        } else {
            return ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION;
        }
    }

    public void setInitialPosition(final ReadOnlyShogiPosition startingPosition) {
        rootNode.setMove(new EditMove(startingPosition));
    }

    public void cleanUpInitialPosition() {
        if (rootNode.getMove() instanceof EditMove &&
                ((EditMove) rootNode.getMove()).getPosition().isDefaultStartingPosition()) {
            rootNode.setMove(null);
        }
    }

    public int getMainVariationLength() {
        int res = 0;
        Node n = rootNode;
        while (n.hasChildren()) {
            n = n.getFirstChild();
            if (!(n.getMove() instanceof SpecialMove)) {
                res++;
            }
        }
        return res;
    }

    public VisitedProgress getPercentVisited() {
        VisitedProgress progress = new VisitedProgress();
        getProgress(progress, rootNode);
        return progress;
    }

    public static class VisitedProgress {
        public int visited;
        public int total;
    }

    private void getProgress(final VisitedProgress progress, final Node node) {
        if (!node.isNew()) progress.total++;
        if (!node.isNew() && node.isVisited()) progress.visited++;
        for (Node child : node.getChildren()) {
            getProgress(progress, child);
        }
    }
}
