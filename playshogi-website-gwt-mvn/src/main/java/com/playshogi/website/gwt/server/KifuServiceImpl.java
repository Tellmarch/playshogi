package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.GameSetRepository;
import com.playshogi.library.database.KifuRepository;
import com.playshogi.library.database.PositionRepository;
import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentGameSetMove;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.engine.PositionEvaluation;
import com.playshogi.library.shogi.engine.PrincipalVariation;
import com.playshogi.library.shogi.engine.USIConnector;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.shared.models.*;
import com.playshogi.website.gwt.shared.services.KifuService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KifuServiceImpl extends RemoteServiceServlet implements KifuService {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(KifuServiceImpl.class.getName());

    private final KifuRepository kifuRepository;
    private final GameSetRepository gameSetRepository;
    private final PositionRepository positionRepository;
    private final Authenticator authenticator = Authenticator.INSTANCE;

    public KifuServiceImpl() {
        DbConnection dbConnection = new DbConnection();
        gameSetRepository = new GameSetRepository(dbConnection);
        positionRepository = new PositionRepository(dbConnection);
        kifuRepository = new KifuRepository(dbConnection);
    }

    @Override
    public String saveKifu(final String sessionId, final String kifuUsf) {
        LOGGER.log(Level.INFO, "saving kifu:\n" + kifuUsf);
        GameRecord gameRecord = UsfFormat.INSTANCE.read(kifuUsf);
        String name = UUID.randomUUID().toString();
        int kifuId = kifuRepository.saveKifu(gameRecord, name, 1, KifuType.GAME);
        return String.valueOf(kifuId);
    }

    @Override
    public String getKifuUsf(final String sessionId, final String kifuId) {
        LOGGER.log(Level.INFO, "querying kifu:\n" + kifuId);

        // TODO validate session

        GameRecord gameRecord = kifuRepository.getKifuById(Integer.parseInt(kifuId)).getKifu();
        if (gameRecord == null) {
            LOGGER.log(Level.INFO, "invalid kifu id:\n" + kifuId);
            return null;
        } else {
            String usf = UsfFormat.INSTANCE.write(gameRecord);
            LOGGER.log(Level.INFO, "found kifu:\n" + usf);
            return usf;
        }
    }

    @Override
    public KifuDetails[] getAvailableKifuDetails(final String sessionId) {
        Map<String, GameRecord> gameRecords = Collections.emptyMap();
        List<KifuDetails> result = new ArrayList<>(gameRecords.size());
        for (Entry<String, GameRecord> entry : gameRecords.entrySet()) {
            result.add(createKifuDetails(entry.getKey(), entry.getValue()));
        }
        return result.toArray(new KifuDetails[0]);
    }

    private KifuDetails createKifuDetails(final String key, final GameRecord value) {
        GameInformation gameInformation = value.getGameInformation();

        KifuDetails kifuDetails = new KifuDetails();
        kifuDetails.setId(key);
        kifuDetails.setSente(gameInformation.getSente());
        kifuDetails.setGote(gameInformation.getGote());
        kifuDetails.setVenue(gameInformation.getVenue());
        kifuDetails.setDate(gameInformation.getDate());
        return kifuDetails;
    }

    @Override
    public PositionDetails getPositionDetails(final String sfen, final int gameSetId) {
        LOGGER.log(Level.INFO, "querying position details:\n" + sfen + " " + gameSetId);
        // TODO validate permissions

        int positionId = positionRepository.getPositionIdBySfen(sfen);

        if (positionId == -1) {
            LOGGER.log(Level.INFO, "position not in database: \n" + sfen);
            return null;
        }

        PersistentGameSetPos stats = gameSetRepository.getGameSetPositionStats(positionId, gameSetId);
        List<PersistentGameSetMove> moveStats = gameSetRepository.getGameSetPositionMoveStats(positionId, gameSetId);

        PositionMoveDetails[] details = new PositionMoveDetails[moveStats.size()];
        for (int i = 0; i < details.length; i++) {
            PersistentGameSetMove move = moveStats.get(i);
            String newSfen = positionRepository.getPositionSfenById(move.getNewPositionId());
            details[i] = new PositionMoveDetails(move.getMoveUsf(), move.getMoveOccurrences(),
                    move.getPositionOccurences(), move.getSenteWins(), move.getGoteWins(), newSfen);
        }

        List<PersistentGame> gamesForPosition = kifuRepository.getGamesForPosition(positionId);

        SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

        String[] kifuIds = new String[gamesForPosition.size()];
        String[] kifuDescs = new String[gamesForPosition.size()];
        for (int i = 0; i < kifuIds.length; i++) {
            PersistentGame persistentGame = gamesForPosition.get(i);
            kifuIds[i] = String.valueOf(persistentGame.getKifuId());
            kifuDescs[i] = persistentGame.getSenteName() + " - " + persistentGame.getGoteName();
            if (persistentGame.getDatePlayed() != null) {
                kifuDescs[i] += " (" + yearDateFormat.format(persistentGame.getDatePlayed()) + ")";
            }
        }

        return new PositionDetails(stats.getTotal(), stats.getSenteWins(), stats.getGoteWins(), details, kifuIds,
                kifuDescs);
    }

    @Override
    public PositionEvaluationDetails analysePosition(String sessionId, String sfen) {
        LOGGER.log(Level.INFO, "analyzing position:\n" + sfen);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            USIConnector usiConnector = new USIConnector();
            PositionEvaluation evaluation = usiConnector.analysePosition(sfen);
            LOGGER.log(Level.INFO, "Position analysis: " + evaluation);

            return convertPositionEvaluation(evaluation);
        } else {
            LOGGER.log(Level.INFO, "Position analysis is only available for logged-in users");
            return null;
        }
    }

    private PositionEvaluationDetails convertPositionEvaluation(final PositionEvaluation evaluation) {
        PositionEvaluationDetails details = new PositionEvaluationDetails();
        details.setBestMove(evaluation.getBestMove());
        details.setPonderMove(evaluation.getPonderMove());
        details.setPrincipalVariationHistory(Arrays.stream(evaluation.getPrincipalVariationHistory()).map(
                this::convertPrincipalVariation).toArray(PrincipalVariationDetails[]::new));
        return details;
    }

    private PrincipalVariationDetails convertPrincipalVariation(final PrincipalVariation principalVariation) {
        PrincipalVariationDetails details = new PrincipalVariationDetails();
        details.setDepth(principalVariation.getDepth());
        details.setEvaluationCP(principalVariation.getEvaluationCP());
        details.setForcedMate(principalVariation.isForcedMate());
        details.setNodes(principalVariation.getNodes());
        details.setNumMovesBeforeMate(principalVariation.getNumMovesBeforeMate());
        details.setSeldepth(principalVariation.getSeldepth());
        details.setPrincipalVariation(principalVariation.getPrincipalVariation());
        return details;
    }
}
