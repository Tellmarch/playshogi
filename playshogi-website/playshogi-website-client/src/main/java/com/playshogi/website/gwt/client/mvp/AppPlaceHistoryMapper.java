package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.place.*;

@Singleton
@WithTokenizers({MainPagePlace.Tokenizer.class, TsumePlace.Tokenizer.class, FreeBoardPlace.Tokenizer.class,
        ProblemStatisticsPlace.Tokenizer.class, ByoYomiLandingPlace.Tokenizer.class,
        ByoYomiPlace.Tokenizer.class, LoginPlace.Tokenizer.class, ProblemPlace.Tokenizer.class,
        OpeningsPlace.Tokenizer.class, ViewKifuPlace.Tokenizer.class, LinksPlace.Tokenizer.class,
        TutorialPlace.Tokenizer.class, GameCollectionsPlace.Tokenizer.class, PlayPlace.Tokenizer.class,
        ProblemsPlace.Tokenizer.class, KifuEditorPlace.Tokenizer.class, PreviewKifuPlace.Tokenizer.class,
        UserKifusPlace.Tokenizer.class, ManageProblemsPlace.Tokenizer.class, PublicCollectionsPlace.Tokenizer.class,
        MyCollectionsPlace.Tokenizer.class, CollectionHelpPlace.Tokenizer.class, CollectionPagePlace.Tokenizer.class,
        LessonsPlace.Tokenizer.class, ManageLessonsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
