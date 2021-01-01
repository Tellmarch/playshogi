package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Node {

    private Node parent = null;
    private final List<Node> children = new ArrayList<>();

    private Move move;
    private String comment = null;
    private MoveAnnotation annotation = MoveAnnotation.NONE;
    private Integer evaluation = null;

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

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getFirstChild() {
        return children.get(0);
    }

    public MoveAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(final MoveAnnotation annotation) {
        this.annotation = annotation;
    }

    public Optional<Integer> getEvaluation() {
        return Optional.ofNullable(evaluation);
    }

    public void setEvaluation(final Integer evaluation) {
        this.evaluation = evaluation;
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
                ", comment=" + comment +
                ", annotation=" + annotation.getShortString() +
                '}';
    }
}
