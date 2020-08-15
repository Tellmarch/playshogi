package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Position;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.shogivariant.ShogiVariant;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShogiPosition implements Position<ShogiPosition> {

    private boolean senteToPlay;
    private ShogiBoardState shogiBoardState;
    private KomadaiState senteKomadai;
    private KomadaiState goteKomadai;

    public ShogiPosition() {
        this(ShogiVariant.NORMAL_SHOGI);
    }

    public ShogiPosition(final ShogiVariant shogiVariant) {
        shogiBoardState = new ShogiBoardStateImpl(shogiVariant.getBoardWidth(), shogiVariant.getBoardHeight());
        senteToPlay = true;
        goteKomadai = new KomadaiState();
        senteKomadai = new KomadaiState();
    }

    public ShogiPosition(final boolean senteToPlay, final ShogiBoardState shogiBoardState,
                         final KomadaiState senteKomadai, final KomadaiState goteKomadai) {
        this.senteToPlay = senteToPlay;
        this.shogiBoardState = shogiBoardState;
        this.senteKomadai = senteKomadai;
        this.goteKomadai = goteKomadai;
    }

    public boolean isSenteToPlay() {
        return senteToPlay;
    }

    public void setSenteToPlay(final boolean senteToPlay) {
        this.senteToPlay = senteToPlay;
    }

    public ShogiBoardState getShogiBoardState() {
        return shogiBoardState;
    }

    public void setShogiBoardState(final ShogiBoardState shogiBoardState) {
        this.shogiBoardState = shogiBoardState;
    }

    public KomadaiState getSenteKomadai() {
        return senteKomadai;
    }

    public void setSenteKomadai(final KomadaiState senteKomadai) {
        this.senteKomadai = senteKomadai;
    }

    public KomadaiState getGoteKomadai() {
        return goteKomadai;
    }

    public void setGoteKomadai(final KomadaiState goteKomadai) {
        this.goteKomadai = goteKomadai;
    }

    public Optional<Piece> getPieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square);
    }

    public boolean hasSentePieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square).isPresent() && shogiBoardState.getPieceAt(square).get().isSentePiece();
    }

    public boolean hasGotePieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square).isPresent() && !shogiBoardState.getPieceAt(square).get().isSentePiece();
    }

    /**
     *
     * @return list of squares of the board
     */
    public List<Square> getAllSquares(){
        List<Square> squares = new ArrayList<>();
        for (int row = 1; row <= shogiBoardState.getLastRow(); row++) {
            for (int column = 1; column <= shogiBoardState.getLastColumn(); column++) {
                squares.add(Square.of(column,row));
            }
        }
        return squares;
    }

    @Override
    public String toString() {
        return shogiBoardState.toString();
    }

    @Override
    public ShogiPosition clonePosition() {
        // TODO Auto-generated method stub
        return null;
    }

}
