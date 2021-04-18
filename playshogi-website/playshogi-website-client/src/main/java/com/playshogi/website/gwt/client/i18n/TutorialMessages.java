package com.playshogi.website.gwt.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface TutorialMessages extends Messages {

    @DefaultMessage("Back")
    String back();

    @DefaultMessage("Next")
    String next();

    @DefaultMessage("Try again")
    String tryAgain();

    @DefaultMessage("Success!<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String success();

    @DefaultMessage("Welcome to this introduction to Shogi Rules! In this interactive tutorial, you will learn how to" +
            " recognize and move the shogi pieces.<br/><br/>" +
            "The tutorial can be done with traditional Japanese pieces, or international graphics - feel free to " +
            "change between those at any point with the buttons on the left of the board!<br/><br/>" +
            "When you are ready, click on the Next! button to continue.")
    String intro();

    @DefaultMessage("Introduction")
    String introTitle();

    @DefaultMessage("The King is your most important piece. If it is captured, you lose the game!<br/>" +
            "It can move one square in any direction. Try it now!<br/><br/>" +
            "When you are ready, click on the Next! button to continue.")
    String kingIntro();

    @DefaultMessage("The King")
    String kingTitle();

    @DefaultMessage("Time to practice: move the king until you capture the opponent pawn. Do not let it capture you!")
    String kingPractice();

    @DefaultMessage("The pawn is able to capture your king! Remember - when you lose your king, you lose the game" +
            ".<br/>" +
            "Click \"Try again\" to give it another try.")
    String kingPracticeFailed();

    @DefaultMessage("Success! In Shogi, you can capture a piece simply by moving to its square. The piece always " +
            "captures in the same way as it moves.<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String kingPracticeSuccess();

    @DefaultMessage("The Rook is your strongest attacking piece.<br/>" +
            "It can move any number of squares vertically or horizontally, but it can not jump above other pieces" +
            ".<br/><br/>" +
            "When promoting, it also gains the ability to move diagonally by one square.")
    String rookIntro();

    @DefaultMessage("The Rook")
    String rookTitle();

    @DefaultMessage("Time to practice: this time, try to capture all the opponent pawns with your Rook - but do not " +
            "let them capture you, or go too far down the board!")
    String rookPractice();

    @DefaultMessage("The pawn is able to capture your rook! It is better to save such a strong piece.<br/>" +
            "Click \"Try again\" to give it another try.")
    String rookPracticeFailed();

    @DefaultMessage("The pawns are already at the last row! Try to capture them before that happens.<br/>" +
            "Click \"Try again\" to give it another try.")
    String rookPracticeFailed2();

    @DefaultMessage("Work in progress")
    String inProgress();

    @DefaultMessage("The Pawn")
    String pawnTitle();

    @DefaultMessage("The Pawn is the most basic soldier in your army - they have very limited movements, only able to" +
            " move forward by one square. <br/>" +
            "If they manage to infiltrate the opponent camp, they will have a chance to be promoted for glory.<br/>" +
            "Try moving your pawn a few times!<br/><br/>" +
            "Tip: the pawn is the smallest piece in size as well, and there are always a lot of them around, making " +
            "it easy to identify on the board.")
    String pawnIntro();

    @DefaultMessage("The Lance")
    String lanceTitle();

    @DefaultMessage("The Lance is similar to the rook, but can only move forward.<br/><br/>" +
            "As all the minor pieces, when promoted it gets the movements of a Gold general.")
    String lanceIntro();


    @DefaultMessage("The starting Lances can be more difficult to use for beginners.<br/>" +
            "Once you have one in hand however, a lot of possibilities appear.<br/>" +
            "In this position you can force a bishop capture with a well-placed lance drop!")
    String lanceExercise();

    @DefaultMessage("Wrong answer - Click \"Try again\" to give it another try.")
    String lancePracticeFailed();

    @DefaultMessage("Success! If the bishop tries to move, that would leave the king vulnerable to the lance.<br/>" +
            "As a result you will be able to capture the bishop next move!<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String lancePracticeSuccess();

    @DefaultMessage("The Knight")
    String knightTitle();

    @DefaultMessage("The Knight has a very limited movement in Shogi - it can only move to two squares.<br/>" +
            "However, it has the unique ability to jump above other pieces, making it a key piece in attack" +
            ".<br/><br/>" +
            "As all the minor pieces, when promoted it gets the movements of a Gold general.")
    String knightIntro();

    @DefaultMessage("Time to practice: can you spot the knight move that lets you attack 2 of your opponent pieces?")
    String knightPractice();

    @DefaultMessage("Wrong answer - Click \"Try again\" to give it another try.")
    String knightPracticeFailed();

    @DefaultMessage("Success! The knight jumped to attack the Silver General and Bishop, and will be able to capture " +
            "one of the two.<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String knightPracticeSuccess();

    @DefaultMessage("The Silver General")
    String silverTitle();

    @DefaultMessage("Close cousin to the Gold general, the Silver general can move diagonally back, but not to the " +
            "side or backward.<br/><br/>" +
            "As all the minor pieces, when promoted it gets the movements of a Gold general.<br/><br/>" +
            "Tip: remember that you can recognize the Gold general 金 by its big hat? The silver general has the same " +
            "hat, but smaller.")
    String silverIntro();

    @DefaultMessage("Time to practice: move your silver general from the bottom left to the right to capture the " +
            "tokin, without moving other pieces!")
    String silverPractice();

    @DefaultMessage("Click \"Try again\" to give it another try.")
    String silverPracticeFailed();

    @DefaultMessage("Success! The Silver general agility makes it an ideal attacking piece.<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String silverPracticeSuccess();

    @DefaultMessage("The Gold General")
    String goldTitle();

    @DefaultMessage("The Gold General, closest to the King, has almost the same movements.<br/>" +
            "However, it can not move back diagonally.<br/><br/>" +
            "Tip: you can recognize the piece by its big \"hat\".")
    String goldIntro();

    @DefaultMessage("Time to practice: use your Gold Generals in hand to capture the opponent King!<br/><br/>" +
            "If you are having trouble, you can reset the position with the \"Try again\" button.")
    String goldPractice();

    @DefaultMessage("Click \"Try again\" to give it another try.")
    String goldPracticeFailed();

    @DefaultMessage("Success! The king can not run anymore, this is what is called Checkmate.<br/>" +
            "If you manage to checkmate the opponent king, you win the game!<br/>" +
            "The Gold general is usually a good choice to try and checkmate.<br/><br/>" +
            "Click \"Next\" to go to the next lesson.")
    String goldPracticeSuccess();

    @DefaultMessage("The Bishop")
    String bishopTitle();

    @DefaultMessage("The Bishop is your second strongest piece, after the Rook.<br/>" +
            "It can move diagonally any number of squares.<br/><br/>" +
            "When promoting, it also gains the ability to move sideway by one square.")
    String bishopIntro();

    @DefaultMessage("Time to practice: find the square which lets your bishop attack 2 pieces at once!")
    String bishopPractice();

    @DefaultMessage("White can escape the bishop.<br/>" +
            "Click \"Try again\" to give it another try.")
    String bishopPracticeFailed();

    @DefaultMessage("Success! From this square, the bishop threatens both the king and the rook. White will not be " +
            "able to save both. <br/><br/>" +
            " Click \"Next\" to go to the next lesson.")
    String bishopPracticeSuccess();

    @DefaultMessage("Pieces can be promoted in a (usually) stronger piece when they reach the opponent camp.<br/>" +
            "Each promotion zone consists of the last 3 rows of the board.<br/>" +
            "Promotion is only mandatory when not promoting would leave the piece with no possible moves.")
    String promotionIntro();

    @DefaultMessage("Promotion")
    String promotionTitle();

    @DefaultMessage("Capture and Drops")
    String captureAndDropsTitle();

    @DefaultMessage("One of the most exciting rule in Shogi is the \"Drop rule\".<br/>" +
            "When you capture a piece, it is put aside, on the piece stand area to the right of the board.<br/>" +
            "When it is your move again, you then have a choice to drop a piece from the piece stand to the board, " +
            "instead of a regular move.<br/><br/>" +
            "Try capturing the pawn, then dropping it back to the board.")
    String captureAndDropsIntro();

    @DefaultMessage("Practice")
    String practiceTitle();

    @DefaultMessage("Time to practice what you learned! This time you will play a real game of Shogi against a " +
            "computer." +
            "<br/>It is very common to teach Shogi by playing Handicap games.<br/><br/>Let us start with the naked " +
            "king handicap.")
    String practiceIntro1();

    @DefaultMessage("Level 2/4 <br/>This time, the computer starts with his pawns too.")
    String practiceIntro2();

    @DefaultMessage("Level 3/4 <br/>Now, the computer starts with three pawns in hand instead. Be careful, this one " +
            "can be tricky!")
    String practiceIntro3();

    @DefaultMessage("Level 4/4 <br/>Finally, the computer has gold generals to support the king. This can be " +
            "difficult for a beginner!")
    String practiceIntro4();

    @DefaultMessage("Good job! Time to increase the difficulty a bit.")
    String practiceNextLevel();

    @DefaultMessage("Well played! You have completed this tutorial.<br/><br/>" +
            "If you want to keep practicing against the computer, head to the Play section.<br/>" +
            "To learn about Shogi tactics, head to the Problems section.<br/>" +
            "To learn from actual games, head to the Game Collections section.<br/>")
    String practiceSuccess();

    @DefaultMessage("All minor pieces (pawn, lance, knight, silver general) can promote and get the movements of the " +
            "Gold general.<br/>" +
            "Time to practice: You only have one pawn on the board - is it enough to checkmate the opponent king?")
    String promotionPractice();

    @DefaultMessage("Special rules")
    String specialTitle();

    @DefaultMessage("We are almost done with the shogi rules! Before we move to practice, there are a a couple " +
            "additional rules to learn.<br/><br/>" +
            "Click on the Next! button to continue.")
    String specialIntro();

    @DefaultMessage("Special rules (1/4): Nifu")
    String nifuTitle();

    @DefaultMessage("Because there are so many pawns, it was decided to put some limit to where we can drop them" +
            ".<br/>" +
            "The most important rule is that it is never allowed to have two unpromoted pawn in the same column.<br/>" +
            "In a tournament, if you drop a pawn in such a position, you instantly lose the game!<br/><br/>" +
            "Try dropping the pawn that you have in hand without breaking the rules.")
    String nifuIntro();

    @DefaultMessage("Special rules (2/4): No Pawn Drop mates!")
    String pawnDropMateTitle();

    @DefaultMessage("Another restriction is that you can not drop a pawn directly in a checkmate!<br/>" +
            "Dropping check is fine, and pushing a pawn to checkmate is also allowed.<br/><br/>" +
            "This position is checkmate in 3 moves - make sure to drop the pieces in the right order!")
    String pawnDropMateIntro();


    @DefaultMessage("Special rules (3/4): Repetition")
    String repetitionTitle();

    @DefaultMessage("Draws are very rare in Shogi - if a position is repeated 4 times, the game ends in a draw. " +
            "In that case, the players will play another game with opposite colors, to decide on a winner.<br/><br/>" +
            "Repeating the position 4 times with continuous checks is not allowed and would be an illegal " +
            "move!<br/><br/>" +
            "Click on the Next! button to continue.")
    String repetitionIntro();

    @DefaultMessage("Special rules (4/4): Impasse")
    String impasseTitle();

    @DefaultMessage("In rare situations, it may become impossible to mate either King, when they escaped to the " +
            "opponent " +
            "camp and are protected by promoted pieces. " +
            "In that case, whoever has the most pieces wins - Rooks and Bishops count 5 points each, all" +
            " other pieces are 1 point.<br/><br/>" +
            "That situation is very rare, so no need to remember the exact rule for now.<br/><br/>" +
            "Click on the Next! button to continue.")
    String impasseIntro();
}
