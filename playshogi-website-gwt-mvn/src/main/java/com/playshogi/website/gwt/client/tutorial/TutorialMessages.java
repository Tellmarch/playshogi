package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.i18n.client.Messages;

public interface TutorialMessages extends Messages {

    @DefaultMessage("Back")
    String back();

    @DefaultMessage("Next")
    String next();

    @DefaultMessage("Try again")
    String tryAgain();

    @DefaultMessage("Success! Click \"Next\" to go to the next lesson.")
    String success();

    @DefaultMessage("Welcome to this introduction to Shogi Rules! In this interactive tutorial, you will learn how to" +
            " recognize and move the shogi pieces.\n" +
            "The tutorial can be done with tradionational Japanese pieces, or international graphics - feel free to " +
            "change between those at any point with the buttons on the left of the board!")
    String intro();

    @DefaultMessage("Introduction")
    String introTitle();

    @DefaultMessage("The King is your most important piece. If it is captured, you lose the game!\n" +
            "It can move one square in any direction.")
    String kingIntro();

    @DefaultMessage("The King")
    String kingTitle();

    @DefaultMessage("Time to practice: move the king until you capture the opponent pawn. Do not let it capture you!")
    String kingPractice();

    @DefaultMessage("The pawn is able to capture your king! Remember - when you lose your king, you lose the game.\n" +
            "Click \"Try again\" to give it another try.")
    String kingPracticeFailed();

    @DefaultMessage("Success! In Shogi, you can capture a piece simply by moving to its square. Unlike chess there is" +
            " no special movement for captures. Click \"Next\" to go to the next lesson.")
    String kingPracticeSuccess();

    @DefaultMessage("The Rook is your strongest attacking piece.\n" +
            "It can move any number of squares vertically or horizontally, but it can not jump above other pieces.")
    String rookIntro();

    @DefaultMessage("The Rook")
    String rookTitle();

    @DefaultMessage("Time to practice: this time, try to capture all the opponent pawns with your Rook - but do not " +
            "let them capture you, or go too far down the board!")
    String rookPractice();

    @DefaultMessage("The pawn is able to capture your rook! It is better to save such a strong piece.\n" +
            "Click \"Try again\" to give it another try.")
    String rookPracticeFailed();

    @DefaultMessage("The pawns are already at the last row! Try to capture them before that happens.\n" +
            "Click \"Try again\" to give it another try.")
    String rookPracticeFailed2();

    @DefaultMessage("Work in progress")
    String inProgress();

    @DefaultMessage("The Pawn")
    String pawnTitle();

    @DefaultMessage("The Lance")
    String lanceTitle();

    @DefaultMessage("The Knight")
    String knightTitle();

    @DefaultMessage("The Silver General")
    String silverTitle();

    @DefaultMessage("The Gold General")
    String goldTitle();

    @DefaultMessage("The Bishop")
    String bishopTitle();
}
