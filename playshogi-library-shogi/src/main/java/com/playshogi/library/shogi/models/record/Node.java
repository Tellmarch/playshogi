package com.playshogi.library.shogi.models.record;

import com.playshogi.library.models.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {

    private Node parent = null;
    private final List<Node> children = new ArrayList<>();

    private Move move;
    private String comment = null;

    public Node(final Move move) {
        this.move = move;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(final Node parent) {
        this.parent = parent;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(final Move move) {
        this.move = move;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(final Node node) {
        children.add(node);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasChildWithMove(final Move move) {
        return getChildWithMove(move) == null;
    }

    public Node getChildWithMove(final Move move) {
        for (Node node : children) {
            if (Objects.equals(node.getMove(), move)) {
                return node;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Node{" +
                "children.size=" + children.size() +
                ", move=" + move +
                '}';
    }
}
