package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Move;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;

import java.util.Objects;

public class ShogiMove implements Move {

    private final Player player;
    private volatile String usfString;

    public ShogiMove(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Deprecated
    public boolean isSenteMoving() {
        return player == Player.BLACK;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShogiMove)) return false;
        ShogiMove shogiMove = (ShogiMove) o;
        return getPlayer() == shogiMove.getPlayer() &&
                Objects.equals(getUsfString(), shogiMove.getUsfString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer(), getUsfString());
    }
}
