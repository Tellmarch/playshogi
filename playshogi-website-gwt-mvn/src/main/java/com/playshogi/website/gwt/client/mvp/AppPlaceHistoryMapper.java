package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.place.*;

@Singleton
@WithTokenizers({MainPagePlace.Tokenizer.class, TsumePlace.Tokenizer.class, FreeBoardPlace.Tokenizer.class,
        MyGamesPlace.Tokenizer.class,
        LoginPlace.Tokenizer.class, NewKifuPlace.Tokenizer.class, OpeningsPlace.Tokenizer.class,
        ViewKifuPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
