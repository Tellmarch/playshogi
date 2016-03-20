package com.playshogi.library.models;

public class EditMove implements Move {
	private final Position<?> position;

	public EditMove(final Position<?> position) {
		this.position = position;
	}

	public Position<?> getPosition() {
		return position;
	}

}
