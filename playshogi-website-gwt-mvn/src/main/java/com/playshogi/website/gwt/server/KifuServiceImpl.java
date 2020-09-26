package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.*;
import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentGameSet;
import com.playshogi.library.database.models.PersistentGameSetMove;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.models.record.GameCollection;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.engine.*;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.shared.models.*;
import com.playshogi.website.gwt.shared.services.KifuService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.website.gwt.shared.models.AnalysisRequestStatus.*;

public class KifuServiceImpl extends RemoteServiceServlet implements KifuService {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(KifuServiceImpl.class.getName());

    private final KifuRepository kifuRepository;
    private final GameSetRepository gameSetRepository;
    private final PositionRepository positionRepository;
    private final GameRepository gameRepository;
    private final Authenticator authenticator = Authenticator.INSTANCE;

    private final TsumeEscapeSolver tsumeEscapeSolver =
            new TsumeEscapeSolver(new QueuedTsumeSolver(EngineConfiguration.TSUME_ENGINE));
    private final QueuedKifuAnalyzer queuedKifuAnalyzer = new QueuedKifuAnalyzer(EngineConfiguration.NORMAL_ENGINE);

    public KifuServiceImpl() {
        DbConnection dbConnection = new DbConnection();
        gameSetRepository = new GameSetRepository(dbConnection);
        positionRepository = new PositionRepository(dbConnection);
        kifuRepository = new KifuRepository(dbConnection);
        gameRepository = new GameRepository(dbConnection);
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
    public GameCollectionDetails[] getGameCollections(final String sessionId) {

        ArrayList<GameCollectionDetails> gameCollectionDetails = new ArrayList<>();

        List<PersistentGameSet> allGameSets = gameSetRepository.getAllGameSets();
        for (PersistentGameSet gameSet : allGameSets) {
            GameCollectionDetails details = new GameCollectionDetails();
            details.setId(String.valueOf(gameSet.getId()));
            details.setName(gameSet.getName());

            gameCollectionDetails.add(details);
        }

        return gameCollectionDetails.toArray(new GameCollectionDetails[0]);
    }

    @Override
    public KifuDetails[] getGameSetKifuDetails(final String sessionId, final String gameSetId) {
        LOGGER.log(Level.INFO, "getGameSetKifuDetails:\n" + gameSetId);

        List<PersistentGame> games = gameRepository.getGamesFromGameSet(Integer.parseInt(gameSetId));

        return games.stream().map(this::createKifuDetails).toArray(KifuDetails[]::new);
    }

    private KifuDetails createKifuDetails(final PersistentGame game) {
        KifuDetails kifuDetails = new KifuDetails();
        kifuDetails.setId(String.valueOf(game.getKifuId()));
        kifuDetails.setSente(game.getSenteName());
        kifuDetails.setGote(game.getGoteName());
        kifuDetails.setSenteId(String.valueOf(game.getSenteId()));
        kifuDetails.setGoteId(String.valueOf(game.getGoteId()));
        kifuDetails.setDate(String.valueOf(game.getDatePlayed()));
        //TODO venue
        return kifuDetails;
    }

    @Override
    public PositionDetails getPositionDetails(final String sfen, final String gameSetId) {
        LOGGER.log(Level.INFO, "querying position details:\n" + sfen + " " + gameSetId);
        // TODO validate permissions

        int gameSetIdInt = Integer.parseInt(gameSetId);
        int positionId = positionRepository.getPositionIdBySfen(sfen);

        if (positionId == -1) {
            LOGGER.log(Level.INFO, "position not in database: \n" + sfen);
            return null;
        }

        PersistentGameSetPos stats = gameSetRepository.getGameSetPositionStats(positionId, gameSetIdInt);
        List<PersistentGameSetMove> moveStats = gameSetRepository.getGameSetPositionMoveStats(positionId, gameSetIdInt);

        PositionMoveDetails[] details = new PositionMoveDetails[moveStats.size()];
        for (int i = 0; i < details.length; i++) {
            PersistentGameSetMove move = moveStats.get(i);
            String newSfen = positionRepository.getPositionSfenById(move.getNewPositionId());
            details[i] = new PositionMoveDetails(move.getMoveUsf(), move.getMoveOccurrences(),
                    move.getPositionOccurences(), move.getSenteWins(), move.getGoteWins(), newSfen);
        }

        // TODO remove special case once database is properly populated
        List<PersistentGame> gamesForPosition = gameSetIdInt == 1 ? kifuRepository.getGamesForPosition(positionId) :
                kifuRepository.getGamesForPosition(positionId, gameSetIdInt);

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
    public PositionEvaluationDetails analysePosition(final String sessionId, final String sfen) {
        LOGGER.log(Level.INFO, "analyzing position:\n" + sfen);

        ShogiPosition position = SfenConverter.fromSFEN(sfen);

        if (position.hasSenteKingOnBoard()) {
            return analyseNormalPosition(sessionId, sfen);
        } else {
            return analyseTsumePosition(sessionId, position);
        }
    }

    private PositionEvaluationDetails analyseNormalPosition(final String sessionId, final String sfen) {
        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            USIConnector usiConnector = new USIConnector(EngineConfiguration.NORMAL_ENGINE);
            usiConnector.connect();
            PositionEvaluation evaluation = usiConnector.analysePosition(sfen, 5000);
            usiConnector.disconnect();
            LOGGER.log(Level.INFO, "Position analysis: " + evaluation);

            return convertPositionEvaluation(evaluation);
        } else {
            LOGGER.log(Level.INFO, "Position analysis is only available for logged-in users");
            return null;
        }
    }

    private PositionEvaluationDetails analyseTsumePosition(final String sessionId, final ShogiPosition position) {
        EscapeTsumeResult result = tsumeEscapeSolver.escapeTsume(position);
        LOGGER.log(Level.INFO, "Tsume analysis: " + result);
        PositionEvaluationDetails details = new PositionEvaluationDetails();
        details.setTsumeAnalysis(result.toPrettyString());
        return details;
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

    @Override
    public AnalysisRequestStatus requestKifuAnalysis(final String sessionId, final String kifuUsf) {
        LOGGER.log(Level.INFO, "requestKifuAnalysis:\n" + kifuUsf);

        QueuedKifuAnalyzer.Status status = queuedKifuAnalyzer.getStatus(kifuUsf);

        if (status != QueuedKifuAnalyzer.Status.NOT_STARTED) {
            LOGGER.log(Level.INFO, "Kifu analysis was already requested");
            return fromStatus(status);
        }

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            if (queuedKifuAnalyzer.getQueueSize() > 5) {
                return QUEUE_TOO_LONG;
            }

            queuedKifuAnalyzer.analyzeKifu(kifuUsf);
            LOGGER.log(Level.INFO, "Queued kifu analysis for " + kifuUsf);
            return QUEUED;
        } else {
            LOGGER.log(Level.INFO, "Kifu analysis is only available for logged-in users");
            return NOT_ALLOWED;
        }
    }

    private AnalysisRequestStatus fromStatus(QueuedKifuAnalyzer.Status status) {
        switch (status) {
            case QUEUED:
                return QUEUED;
            case IN_PROGRESS:
                return IN_PROGRESS;
            case COMPLETED:
                return COMPLETED;
            case NOT_STARTED:
            default:
                throw new IllegalStateException("Can not convert status " + status);
        }
    }

    @Override
    public AnalysisRequestResult getKifuAnalysisResults(final String sessionId, final String kifuUsf) {
        LOGGER.log(Level.INFO, "getKifUAnalysisResults:\n" + kifuUsf);

        QueuedKifuAnalyzer.Status status = queuedKifuAnalyzer.getStatus(kifuUsf);

        if (status == QueuedKifuAnalyzer.Status.NOT_STARTED) {
            LOGGER.log(Level.INFO, "Kifu analysis was not requested");
            return new AnalysisRequestResult(NOT_REQUESTED);
        }

        if (status == QueuedKifuAnalyzer.Status.QUEUED) {
            LOGGER.log(Level.INFO, "Kifu analysis  in queue");
            AnalysisRequestResult result = new AnalysisRequestResult(QUEUED);
            result.setQueuePosition(queuedKifuAnalyzer.getQueuedPosition(kifuUsf));
            return result;
        }

        List<PositionEvaluation> evaluation = queuedKifuAnalyzer.getEvaluation(kifuUsf);

        AnalysisRequestResult result = new AnalysisRequestResult();
        result.setDetails(evaluation.stream().map(this::convertPositionEvaluation).toArray(PositionEvaluationDetails[]::new));
        result.setStatus(status == QueuedKifuAnalyzer.Status.IN_PROGRESS ? IN_PROGRESS : COMPLETED);
        return result;
    }

    @Override
    public String saveGameCollection(final String sessionId, final String draftId) {
        LOGGER.log(Level.INFO, "saveGameCollection: " + draftId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game collection");
        }

        GameCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            for (Entry<String, GameCollection> c : CollectionUploads.INSTANCE.getCollections().entrySet()) {
                LOGGER.log(Level.INFO, "Existing draft collection: " + c.getKey() + " " + c.getValue().getName());
            }
            throw new IllegalStateException("Invalid draft connection ID");
        }

        int id = gameSetRepository.saveGameSet(collection.getName());

        int i = 1;
        for (GameRecord game : collection.getGames()) {
            gameSetRepository.addGameToGameSet(game, id, 1, "Game #" + (i++), loginResult.getUserId());
        }

        return String.valueOf(id);
    }

    @Override
    public void saveGameCollectionDetails(final String sessionId, final GameCollectionDetails gameCollectionDetails) {
        LOGGER.log(Level.INFO, "saveGameCollectionDetails: " + gameCollectionDetails);


    }
}
