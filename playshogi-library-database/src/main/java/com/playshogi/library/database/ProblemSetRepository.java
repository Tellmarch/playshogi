package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.database.models.PersistentProblem;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.features.FeatureTag;
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

    private static final String INSERT_PROBLEM_TAG = "INSERT INTO `playshogi`.`ps_problemtag` (`problem_id`, " +
            "`tag_id`) VALUES (?, ?);";

    public ProblemSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addProblemToProblemSet(final GameRecord gameRecord, final int problemSetId, final String problemName,
                                       final int authorId, final int elo, final PersistentProblem.ProblemType pbType) {
        PositionRepository rep = new PositionRepository(dbConnection);
        KifuRepository kifuRep = new KifuRepository(dbConnection);
        ProblemRepository problemRep = new ProblemRepository(dbConnection);

        int kifuId = kifuRep.saveKifu(gameRecord, problemName, authorId, PersistentKifu.KifuType.GAME);
        int problemId = problemRep.saveProblem(kifuId, 0, elo, pbType);

        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameRecord.getGameTree(),
                new ShogiInitialPositionFactory().createInitialPosition());

        int lastPositionId = rep.getOrSavePosition(gameNavigation.getPosition());

        kifuRep.saveKifuPosition(kifuId, lastPositionId);

        while (gameNavigation.canMoveForward()) {
            gameNavigation.moveForward();
            int positionId = rep.getOrSavePosition(gameNavigation.getPosition());
            kifuRep.saveKifuPosition(kifuId, positionId);
        }

        for (FeatureTag tag : FeatureTag.values()) {
            try {
                if (tag.getFeature().hasFeature(gameRecord)) {
                    LOGGER.log(Level.INFO, "Found feature: " + tag.getFeature().getName());
                    insertProblemTag(problemId, tag.getDbIndex());
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error evaluating the feature " + tag.getFeature().getName(), e);
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
