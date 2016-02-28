package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Position;
import com.playshogi.library.shogi.models.shogivariant.ShogiVariant;

public class ShogiPosition implements Position {

	private boolean senteToPlay;
	private ShogiBoardState shogiBoardState;
	private KomadaiState senteKomadai;
	private KomadaiState goteKomadai;

	public ShogiPosition(final ShogiVariant shogiVariant) {
		shogiBoardState = new ShogiBoardState(shogiVariant.getBoardWidth(), shogiVariant.getBoardHeight());
		senteToPlay = true;
		goteKomadai = new KomadaiState();
		senteKomadai = new KomadaiState();
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

}
