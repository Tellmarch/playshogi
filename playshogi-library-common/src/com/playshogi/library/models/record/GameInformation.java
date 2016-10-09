package com.playshogi.library.models.record;

public class GameInformation {

	private String sente;
	private String gote;
	private String date;
	private String venue;

	public GameInformation() {
	}

	public String getSente() {
		return sente;
	}

	public void setSente(final String sente) {
		this.sente = sente;
	}

	public String getGote() {
		return gote;
	}

	public void setGote(final String gote) {
		this.gote = gote;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(final String venue) {
		this.venue = venue;
	}

	@Override
	public String toString() {
		return "GameInformation [sente=" + sente + ", gote=" + gote + ", date=" + date + ", venue=" + venue + "]";
	}

}
