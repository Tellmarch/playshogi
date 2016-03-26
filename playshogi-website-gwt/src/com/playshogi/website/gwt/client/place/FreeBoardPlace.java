package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class FreeBoardPlace extends Place {
	private final String helloName;

	public FreeBoardPlace(final String token) {
		this.helloName = token;
	}

	public String getHelloName() {
		return helloName;
	}

	public static class Tokenizer implements PlaceTokenizer<FreeBoardPlace> {

		@Override
		public String getToken(final FreeBoardPlace place) {
			return place.getHelloName();
		}

		@Override
		public FreeBoardPlace getPlace(final String token) {
			return new FreeBoardPlace(token);
		}

	}
}
