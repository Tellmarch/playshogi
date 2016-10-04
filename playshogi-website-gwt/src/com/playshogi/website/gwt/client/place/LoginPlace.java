package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class LoginPlace extends Place {

	public LoginPlace() {
	}

	@Prefix("Login")
	public static class Tokenizer implements PlaceTokenizer<LoginPlace> {

		@Override
		public String getToken(final LoginPlace place) {
			return null;
		}

		@Override
		public LoginPlace getPlace(final String token) {
			return new LoginPlace();
		}

	}
}
