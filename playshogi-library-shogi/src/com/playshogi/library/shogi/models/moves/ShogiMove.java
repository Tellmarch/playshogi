package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Move;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;

public class ShogiMove implements Move {

	private final boolean senteMoving;
	private volatile String usfString;

	public ShogiMove(final boolean senteMoving) {
		this.senteMoving = senteMoving;
	}

	public boolean isSenteMoving() {
		return senteMoving;
	}

	public String getUsfString() {
		if (usfString == null) {
			this.usfString = UsfMoveConverter.toUsfString(this);
		}
		return usfString;
	}

	@Override
	public String toString() {
		return getUsfString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (senteMoving ? 1231 : 1237);
		result = prime * result + ((getUsfString() == null) ? 0 : getUsfString().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShogiMove other = (ShogiMove) obj;
		if (senteMoving != other.senteMoving)
			return false;
		if (getUsfString() == null) {
			if (other.getUsfString() != null)
				return false;
		} else if (!getUsfString().equals(other.getUsfString()))
			return false;
		return true;
	}

}
