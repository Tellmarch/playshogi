package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ViewKifuPlace extends Place {

	private final String kifuId;

	public ViewKifuPlace() {
		this(null);
	}

	public ViewKifuPlace(final String kifuId) {
		this.kifuId = kifuId;
	}

	public String getKifuId() {
		return kifuId;
	}

	@Prefix("ViewKifu")
	public static class Tokenizer implements PlaceTokenizer<ViewKifuPlace> {

		@Override
		public String getToken(final ViewKifuPlace place) {
			return place.getKifuId();
		}

		@Override
		public ViewKifuPlace getPlace(final String token) {
			return new ViewKifuPlace(token);
		}

	}

}
