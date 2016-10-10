package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class KifuDetails implements Serializable {

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<KifuDetails> KEY_PROVIDER = new ProvidesKey<KifuDetails>() {
		@Override
		public Object getKey(final KifuDetails item) {
			return item == null ? null : item.getId();
		}
	};

	private String id;
	private String sente;
	private String gote;
	private String senteId;
	private String goteId;
	private String venue;
	private String date;

	public KifuDetails() {
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
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

	public String getSenteId() {
		return senteId;
	}

	public void setSenteId(final String senteId) {
		this.senteId = senteId;
	}

	public String getGoteId() {
		return goteId;
	}

	public void setGoteId(final String goteId) {
		this.goteId = goteId;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(final String venue) {
		this.venue = venue;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "KifuDetails [id=" + id + ", sente=" + sente + ", gote=" + gote + ", senteId=" + senteId + ", goteId="
				+ goteId + ", venue=" + venue + ", date=" + date + "]";
	}

}
