package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.database.models.PersistentProblem;
import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProblemSetRepository {

    private static final Logger LOGGER = Logger.getLogger(ProblemSetRepository.class.getName());

    private final DbConnection dbConnection;

    private static final String INSERT_PROBLEM_TAG = "INSERT INTO `playshogi`.`ps_problemtag` (`problem_id`, `tag_id`) VALUES (?, ?);";

    public ProblemSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addProblemToProblemSet(final GameRecord gameRecord, final int problemSetId, final String problemName, final int authorId, final int elo, final PersistentProblem.ProblemType pbType) {
        PositionRepository rep = new PositionRepository(dbConnection);
        KifuRepository kifuRep = new KifuRepository(dbConnection);
        ProblemRepository problemRep = new ProblemRepository(dbConnection);

        int kifuId = kifuRep.saveKifu(gameRecord, problemName, authorId, PersistentKifu.KifuType.GAME);
        int problemId = problemRep.saveProblem(kifuId, 0, elo, pbType);

        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(), gameRecord.getGameTree(),
                new ShogiInitialPositionFactory().createInitialPosition());

        int lastPositionId = rep.getOrSavePosition(gameNavigation.getPosition());

        kifuRep.saveKifuPosition(kifuId, lastPositionId);

        Move lastMove = null;
        while (gameNavigation.canMoveForward()) {
            lastMove = gameNavigation.getMainVariationMove();
            gameNavigation.moveForward();
            int positionId = rep.getOrSavePosition(gameNavigation.getPosition());
            kifuRep.saveKifuPosition(kifuId, positionId);
        }

        if (lastMove != null) {
            if (lastMove instanceof DropMove) {
                DropMove dropMove = (DropMove) lastMove;
                if (dropMove.getPieceType() == PieceType.GOLD) {
                    ShogiPosition position = gameNavigation.getPosition();
                    Square dropSquare = dropMove.getToSquare();
                    if (dropSquare.getRow() > 1 && position.getPieceAt(Square.of(dropSquare.getColumn(), dropSquare.getRow() - 1)) == Piece.GOTE_KING) {
                        System.out.println("GOLD DROP AT THE HEAD!!!!!");
                        insertProblemTag(problemId, 1);
                    }
                }
            }
        }


    }


    public void insertProblemTag(final int problemId, final int tagId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEM_TAG)) {
            preparedStatement.setInt(1, problemId);
            preparedStatement.setInt(2, tagId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the problem tag in db", e);
        }
    }


}
