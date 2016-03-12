package com.playshogi.library.models.record;

import java.util.ArrayList;
import java.util.List;

import com.playshogi.library.models.Move;

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

}
