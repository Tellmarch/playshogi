package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MyGamesPlace extends Place {

	public MyGamesPlace() {
	}

	@Prefix("MyGames")
	public static class Tokenizer implements PlaceTokenizer<MyGamesPlace> {

		@Override
		public String getToken(final MyGamesPlace place) {
			return null;
		}

		@Override
		public MyGamesPlace getPlace(final String token) {
			return new MyGamesPlace();
		}

	}

}
