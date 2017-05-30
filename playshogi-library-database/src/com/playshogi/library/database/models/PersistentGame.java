package com.playshogi.library.database.models;

import java.util.Date;

public class PersistentGame {

	private final int id;
	private final int kifuId;
	private final Integer senteId;
	private final Integer goteId;
	private final String senteName;
	private final String goteName;
	private final Date datePlayed;
	private final int venueId;
	private final String description;

	public PersistentGame(final int id, final int kifuId, final Integer senteId, final Integer goteId, final String senteName, final String goteName,
			final Date datePlayed, final int venueId, final String description) {
		this.id = id;
		this.kifuId = kifuId;
		this.senteId = senteId;
		this.goteId = goteId;
		this.senteName = senteName;
		this.goteName = goteName;
		this.datePlayed = datePlayed;
		this.venueId = venueId;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public int getKifuId() {
		return kifuId;
	}

	public Integer getSenteId() {
		return senteId;
	}

	public Integer getGoteId() {
		return goteId;
	}

	public String getSenteName() {
		return senteName;
	}

	public String getGoteName() {
		return goteName;
	}

	public Date getDatePlayed() {
		return datePlayed;
	}

	public int getVenueId() {
		return venueId;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "PersistentGame [id=" + id + ", kifuId=" + kifuId + ", senteId=" + senteId + ", goteId=" + goteId + ", senteName=" + senteName + ", goteName="
				+ goteName + ", datePlayed=" + datePlayed + ", venueId=" + venueId + ", description=" + description + "]";
	}

}
