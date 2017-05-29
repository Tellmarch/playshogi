package com.playshogi.library.database.models;

import java.util.Date;

import com.playshogi.library.models.record.GameRecord;

public class PersistentKifu {

	public static enum KifuType {
		GAME(1, "Game"), PROBLEM(2, "Problem");

		private final int dbInt;
		private final String description;

		private KifuType(final int dbInt, final String description) {
			this.dbInt = dbInt;
			this.description = description;
		}

		public int getDbInt() {
			return dbInt;
		}

		public String getDescription() {
			return description;
		}

		public static KifuType fromDbInt(final int dbInt) {
			switch (dbInt) {
			case 1:
				return GAME;
			case 2:
				return PROBLEM;
			default:
				throw new IllegalArgumentException("Unknown kifu type: " + dbInt);
			}

		}
	}

	private final int id;
	private final String name;
	private final GameRecord kifu;
	private final Date creationDate;
	private final Date updateDate;
	private final KifuType type;
	private final int authorId;

	public PersistentKifu(final int id, final String name, final GameRecord kifu, final Date creationDate, final Date updateDate, final KifuType type,
			final int authorId) {
		this.id = id;
		this.name = name;
		this.kifu = kifu;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.type = type;
		this.authorId = authorId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public GameRecord getKifu() {
		return kifu;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public KifuType getType() {
		return type;
	}

	public int getAuthorId() {
		return authorId;
	}

}
