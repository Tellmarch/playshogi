package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ MainPagePlace.Tokenizer.class, GoodbyePlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
