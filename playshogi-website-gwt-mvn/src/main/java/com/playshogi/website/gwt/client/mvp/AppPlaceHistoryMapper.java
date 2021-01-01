package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.place.*;

@Singleton
@WithTokenizers({MainPagePlace.Tokenizer.class, TsumePlace.Tokenizer.class, FreeBoardPlace.Tokenizer.class,
        ProblemStatisticsPlace.Tokenizer.class, ByoYomiLandingPlace.Tokenizer.class,
        ByoYomiPlace.Tokenizer.class, LoginPlace.Tokenizer.class, NewKifuPlace.Tokenizer.class,
        OpeningsPlace.Tokenizer.class, ViewKifuPlace.Tokenizer.class, LinksPlace.Tokenizer.class,
        TutorialPlace.Tokenizer.class, GameCollectionsPlace.Tokenizer.class, PlayPlace.Tokenizer.class,
        ProblemsPlace.Tokenizer.class, ProblemEditorPlace.Tokenizer.class, PreviewKifuPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
