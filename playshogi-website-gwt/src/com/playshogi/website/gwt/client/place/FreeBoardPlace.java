package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.Random;

public class FreeBoardPlace extends Place {
	private final String boardId;

	public FreeBoardPlace() {
		this("b" + Random.nextInt());
	}

	public FreeBoardPlace(final String token) {
		this.boardId = token;
	}

	public String getBoardId() {
		return boardId;
	}

	@Prefix("Board")
	public static class Tokenizer implements PlaceTokenizer<FreeBoardPlace> {

		@Override
		public String getToken(final FreeBoardPlace place) {
			return place.getBoardId();
		}

		@Override
		public FreeBoardPlace getPlace(final String token) {
			return new FreeBoardPlace(token);
		}

	}
}
