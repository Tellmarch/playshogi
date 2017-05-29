package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.Random;

/**
 * A board with free editing, that can be shared with a unique URL
 */
public class OpeningsPlace extends Place {
	private final String boardId;

	public OpeningsPlace() {
		this("b" + Random.nextInt());
	}

	public OpeningsPlace(final String token) {
		this.boardId = token;
	}

	public String getBoardId() {
		return boardId;
	}

	@Prefix("Openings")
	public static class Tokenizer implements PlaceTokenizer<OpeningsPlace> {

		@Override
		public String getToken(final OpeningsPlace place) {
			return place.getBoardId();
		}

		@Override
		public OpeningsPlace getPlace(final String token) {
			return new OpeningsPlace(token);
		}

	}
}
