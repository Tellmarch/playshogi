package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.*;
import com.playshogi.library.database.models.*;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.shogi.engine.*;
import com.playshogi.library.shogi.engine.insights.GameInsights;
import com.playshogi.library.shogi.engine.insights.Mistake;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.position.PositionScore;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.KifuCollection;
import com.playshogi.website.gwt.shared.models.*;
import com.playshogi.website.gwt.shared.services.KifuService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.website.gwt.shared.models.AnalysisRequestStatus.*;

public class KifuServiceImpl extends RemoteServiceServlet implements KifuService {

    private static final Logger LOGGER = Logger.getLogger(KifuServiceImpl.class.getName());

    private final KifuRepository kifuRepository;
    private final GameSetRepository gameSetRepository;
    private final PositionRepository positionRepository;
    private final GameRepository gameRepository;
    private final LessonRepository lessonRepository;
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
        lessonRepository = new LessonRepository(dbConnection);
    }

    @Override
    public String saveKifu(final String sessionId, final String kifuUsf, final String name,
                           final KifuDetails.KifuType type) {
        LOGGER.log(Level.INFO, "saving kifu:\n" + kifuUsf);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game");
        }

        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(kifuUsf);
        String truncatedName = name.length() <= 45 ? name : name.substring(0, 45);
        int kifuId = kifuRepository.saveKifu(gameRecord, truncatedName, loginResult.getUserId(),
                KifuType.valueOf(type.name()));
        return String.valueOf(kifuId);
    }

    @Override
    public void addExistingKifuToCollection(final String sessionId, final String kifuId, final String collectionId) {
        LOGGER.log(Level.INFO, "add kifu in collection: " + kifuId + " -> " + collectionId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a kifu to a collection");
        }

        if (!gameSetRepository.saveGameFromKifuAndAddToGameSet(Integer.parseInt(collectionId),
                Integer.parseInt(kifuId), 1)) {
            throw new IllegalArgumentException("Could not add the kifu to collection");
        }
    }

    @Override
    public void saveGameAndAddToCollection(final String sessionId, final String kifuUsf, final String collectionId) {
        LOGGER.log(Level.INFO, "saving kifu in collection:\n" + kifuUsf);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game");
        }

        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(kifuUsf);
        String defaultName = getDefaultName(gameRecord);
        String name = defaultName.length() <= 45 ? defaultName : defaultName.substring(0, 45);
        if (!gameSetRepository.saveKifuAndGameToGameSet(gameRecord, Integer.parseInt(collectionId), 1, name,
                loginResult.getUserId())) {
            throw new IllegalArgumentException("Could not save the kifu in database");
        }
    }

    private String getDefaultName(final GameRecord gameRecord) {
        GameInformation info = gameRecord.getGameInformation();
        return info.getLocation() + " - " + info.getBlack() + " - " + info.getWhite() + " - " + info.getDate();
    }

    @Override
    public String getKifuUsf(final String sessionId, final String kifuId) {
        LOGGER.log(Level.INFO, "querying kifu:\n" + kifuId);

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
    public KifuDetails[] getUserKifus(final String sessionId, final String userName) {
        LOGGER.log(Level.INFO, "querying kifus for user: " + userName);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !userName.equals(loginResult.getUserName())) {
            throw new IllegalStateException("User does not have permissions for this operation");
        }

        List<PersistentKifu> userKifus = kifuRepository.getUserKifus(loginResult.getUserId());

        return userKifus.stream().map(this::getKifuDetails).toArray(KifuDetails[]::new);
    }

    private KifuDetails getKifuDetails(final PersistentKifu kifu) {
        KifuDetails details = new KifuDetails();
        details.setId(String.valueOf(kifu.getId()));
        details.setCreationDate(kifu.getCreationDate());
        details.setUpdateDate(kifu.getUpdateDate());
        details.setName(kifu.getName());
        details.setType(KifuDetails.KifuType.valueOf(kifu.getType().name()));
        return details;
    }

    @Override
    public GameCollectionDetails[] getPublicGameCollections(final String sessionId) {
        LOGGER.log(Level.INFO, "getPublicGameCollections");

        List<PersistentGameSet> problemSets = gameSetRepository.getAllPublicGameSets();

        return problemSets.stream().map(this::getCollectionDetails).toArray(GameCollectionDetails[]::new);
    }

    @Override
    public GameCollectionDetails[] getUserGameCollections(final String sessionId, final String userName) {
        LOGGER.log(Level.INFO, "getUserGameCollections");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !userName.equals(loginResult.getUserName())) {
            throw new IllegalStateException("User does not have permissions for this operation");
        }

        List<PersistentGameSet> userProblemSets = gameSetRepository.getGameSetsForUser(loginResult.getUserId());

        return userProblemSets.stream().map(this::getCollectionDetails).toArray(GameCollectionDetails[]::new);
    }

    private GameCollectionDetails getCollectionDetails(final PersistentGameSet gameSet) {
        GameCollectionDetails details = new GameCollectionDetails();
        details.setId(String.valueOf(gameSet.getId()));
        details.setName(gameSet.getName());
        details.setDescription(gameSet.getDescription());
        details.setVisibility(gameSet.getVisibility().toString().toLowerCase());
        details.setNumGames(gameSetRepository.getGamesCountFromGameSet(gameSet.getId()));
        details.setAuthor(UsersCache.INSTANCE.getUserName(gameSet.getOwnerId()));
        return details;
    }

    @Override
    public GameCollectionDetailsAndGames getGameSetKifuDetails(final String sessionId, final String gameSetId) {
        LOGGER.log(Level.INFO, "getGameSetKifuDetails:\n" + gameSetId);

        //TODO access control

        PersistentGameSet gameSet = gameSetRepository.getGameSetById(Integer.parseInt(gameSetId));
        if (gameSet == null) {
            throw new IllegalArgumentException("Invalid gameSet ID");
        }

        List<PersistentGame> games = gameRepository.getGamesFromGameSet(Integer.parseInt(gameSetId));

        GameCollectionDetailsAndGames result = new GameCollectionDetailsAndGames();
        result.setDetails(getCollectionDetails(gameSet));
        result.setGames(games.stream().map(this::createGameDetails).toArray(GameDetails[]::new));

        return result;
    }

    private GameDetails createGameDetails(final PersistentGame game) {
        GameDetails gameDetails = new GameDetails();
        gameDetails.setId(String.valueOf(game.getId()));
        gameDetails.setKifuId(String.valueOf(game.getKifuId()));
        gameDetails.setSente(game.getSenteName());
        gameDetails.setGote(game.getGoteName());
        gameDetails.setSenteId(String.valueOf(game.getSenteId()));
        gameDetails.setGoteId(String.valueOf(game.getGoteId()));
        gameDetails.setDate(String.valueOf(game.getDatePlayed()));
        //TODO venue
        return gameDetails;
    }

    @Override
    public PositionDetails getPositionDetails(final String sfen, final String gameSetId) {
        LOGGER.log(Level.INFO, "querying position details:\n" + sfen + " " + gameSetId);
        try {
            // TODO validate permissions

            int gameSetIdInt = Integer.parseInt(gameSetId);
            int positionId = positionRepository.getPositionIdBySfen(sfen);

            if (positionId == -1) {
                LOGGER.log(Level.INFO, "position not in database: \n" + sfen);
                return null;
            }

            PersistentGameSetPos stats = gameSetRepository.getGameSetPositionStats(positionId, gameSetIdInt);

            if (stats == null) {
                return null;
            }
            List<PersistentGameSetMove> moveStats = gameSetRepository.getGameSetPositionMoveStats(positionId,
                    gameSetIdInt);

            PositionMoveDetails[] details = new PositionMoveDetails[moveStats.size()];
            for (int i = 0; i < details.length; i++) {
                PersistentGameSetMove move = moveStats.get(i);
                String newSfen = positionRepository.getPositionSfenById(move.getNewPositionId());
                details[i] = new PositionMoveDetails(move.getMoveUsf(), move.getMoveOccurrences(),
                        move.getNewPositionOccurences(), move.getSenteWins(), move.getGoteWins(), newSfen);
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
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception in getPositionDetails:", ex);
            return null;
        }
    }

    @Override
    public PositionEvaluationDetails analysePosition(final String sessionId, final String sfen) {
        LOGGER.log(Level.INFO, "analyzing position:\n" + sfen);

        ShogiPosition position = SfenConverter.fromSFEN(sfen);

        if (position.hasSenteKingOnBoard()) {
            return analyseNormalPosition(sessionId, sfen);
        } else {
            return analyseTsumePosition(sessionId, position, sfen);
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

    private PositionEvaluationDetails analyseTsumePosition(final String sessionId, final ShogiPosition position,
                                                           final String sfen) {
        EscapeTsumeResult result = tsumeEscapeSolver.escapeTsume(position);
        LOGGER.log(Level.INFO, "Tsume analysis: " + result);
        PositionEvaluationDetails details = new PositionEvaluationDetails();
        EscapeTsumeDetails tsumeDetails = new EscapeTsumeDetails();
        tsumeDetails.setResult(EscapeTsumeDetails.ResultEnum.valueOf(result.getResult().name()));
        if (result.getEscapeMove() != null) {
            tsumeDetails.setEscapeMove(result.getEscapeMove().getUsfString());
        }
        details.setTsumeAnalysis(tsumeDetails);
        details.setSfen(sfen);
        return details;
    }

    private PositionEvaluationDetails convertPositionEvaluation(final PositionEvaluation evaluation) {
        PositionEvaluationDetails details = new PositionEvaluationDetails();
        details.setSfen(evaluation.getSfen());
        details.setBestMove(evaluation.getBestMove());
        details.setPonderMove(evaluation.getPonderMove());
        details.setPrincipalVariationHistory(evaluation.getPrincipalVariationsHistory().stream().map(
                this::convertPrincipalVariation).toArray(PrincipalVariationDetails[]::new));
        return details;
    }

    private GameInsightsDetails convertGameInsights(final GameInsights insights) {
        if (insights == null) {
            return null;
        }
        GameInsightsDetails details = new GameInsightsDetails();
        details.setBlackAvgCentipawnLoss(insights.getBlackAccuracy().getAverageCentipawnsLost());
        details.setWhiteAvgCentipawnLoss(insights.getWhiteAccuracy().getAverageCentipawnsLost());
        details.setBlackMistakes(insights.getBlackAccuracy().getMistakes().stream()
                .map(this::convertMistake).toArray(MistakeDetails[]::new));
        details.setWhiteMistakes(insights.getWhiteAccuracy().getMistakes().stream()
                .map(this::convertMistake).toArray(MistakeDetails[]::new));
        return details;
    }

    private MistakeDetails convertMistake(final Mistake mistake) {
        MistakeDetails details = new MistakeDetails();
        details.setComputerMove(mistake.getComputerMove());
        details.setMoveCount(mistake.getMoveCount());
        details.setMovePlayed(mistake.getMovePlayed());
        details.setPositionSfen(mistake.getPositionSfen());
        details.setScoreAfterMove(convertScore(mistake.getScoreAfterMove()));
        details.setScoreBeforeMove(convertScore(mistake.getScoreBeforeMove()));
        details.setType(MistakeDetails.Type.valueOf(mistake.getType().name()));
        return details;
    }

    private PositionScoreDetails convertScore(final PositionScore score) {
        PositionScoreDetails details = new PositionScoreDetails();
        details.setEvaluationCP(score.getEvaluationCP());
        details.setForcedMate(score.isForcedMate());
        details.setNumMovesBeforeMate(score.getNumMovesBeforeMate());
        return details;
    }

    private PrincipalVariationDetails convertPrincipalVariation(final Variation principalVariation) {
        PrincipalVariationDetails details = new PrincipalVariationDetails();
        details.setDepth(principalVariation.getDepth());
        details.setEvaluationCP(principalVariation.getScore().getEvaluationCP());
        details.setForcedMate(principalVariation.getScore().isForcedMate());
        details.setNodes(principalVariation.getNodes());
        details.setNumMovesBeforeMate(principalVariation.getScore().getNumMovesBeforeMate());
        details.setSeldepth(principalVariation.getSeldepth());
        details.setPrincipalVariation(principalVariation.getUsf());
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
        result.setEvaluationDetails(evaluation.stream().map(this::convertPositionEvaluation).toArray(PositionEvaluationDetails[]::new));
        result.setStatus(status == QueuedKifuAnalyzer.Status.IN_PROGRESS ? IN_PROGRESS : COMPLETED);
        result.setGameInsightsDetails(convertGameInsights(queuedKifuAnalyzer.getInsights(kifuUsf)));
        return result;
    }

    @Override
    public String saveGameCollection(final String sessionId, final String draftId,
                                     final GameCollectionDetails details) {
        LOGGER.log(Level.INFO, "saveGameCollection: " + draftId + " " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft collection ID");
        }

        String name = details.getName() != null ? details.getName() : collection.getName();
        String description = details.getDescription() != null ? details.getDescription() : collection.getName();
        Visibility visibility = details.getVisibility() == null ?
                Visibility.UNLISTED :
                Visibility.valueOf(details.getVisibility().toUpperCase());

        int id = gameSetRepository.saveGameSet(name, description, visibility, loginResult.getUserId());

        if (id == -1) {
            LOGGER.log(Level.INFO, "Error saving the game set");
            throw new IllegalStateException("Error saving the game set");
        }

        int i = 1;
        for (GameRecord game : collection.getKifus()) {
            gameSetRepository.saveKifuAndGameToGameSet(game, id, 1, "Game #" + (i++), loginResult.getUserId());
        }

        return String.valueOf(id);
    }

    @Override
    public void addDraftToGameCollection(final String sessionId, final String draftId, final String collectionId) {
        LOGGER.log(Level.INFO, "addDraftToGameCollection: " + draftId + " " + collectionId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft collection ID");
        }

        PersistentGameSet gameSet = gameSetRepository.getGameSetById(Integer.parseInt(collectionId));

        if (gameSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (gameSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to add games to this collection");
        }

        int i = 1;
        for (GameRecord game : collection.getKifus()) {
            gameSetRepository.saveKifuAndGameToGameSet(game, Integer.parseInt(collectionId), 1, "Game #" + (i++),
                    loginResult.getUserId());
        }
    }

    @Override
    public void saveDraftCollectionKifus(final String sessionId, final String draftId) {
        LOGGER.log(Level.INFO, "saveDraftCollectionKifus: " + draftId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft collection ID");
        }

        int i = 1;
        for (GameRecord game : collection.getKifus()) {
            kifuRepository.saveKifu(game, "Game #" + (i++), loginResult.getUserId(), KifuType.GAME);
        }
    }

    @Override
    public void updateGameCollectionDetails(final String sessionId, final GameCollectionDetails details) {
        LOGGER.log(Level.INFO, "saveGameCollectionDetails: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can update a game collection");
        }

        gameSetRepository.updateGameSet(Integer.parseInt(details.getId()), details.getName(), details.getDescription(),
                Visibility.valueOf(details.getVisibility().toUpperCase()), loginResult.getUserId());
    }


    @Override
    public void createGameCollection(final String sessionId, final GameCollectionDetails details) {
        LOGGER.log(Level.INFO, "createGameCollection: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can create a game collection");
        }

        gameSetRepository.saveGameSet(details.getName(), details.getDescription(),
                Visibility.valueOf(details.getVisibility().toUpperCase()), loginResult.getUserId());
    }

    @Override
    public void deleteGameCollection(final String sessionId, final String gameSetId) {
        LOGGER.log(Level.INFO, "deleteGameCollection: " + gameSetId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a game collection");
        }

        if (!gameSetRepository.deleteGamesetById(Integer.parseInt(gameSetId), loginResult.getUserId())) {
            throw new IllegalStateException("The user does not have permission to delete the specified game " +
                    "collection");
        }
    }

    @Override
    public void removeGameFromCollection(final String sessionId, final String gameId, final String gameSetId) {
        LOGGER.log(Level.INFO, "removeGameFromCollection: " + gameId + " - " + gameSetId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a game from a collection");
        }

        if (!gameSetRepository.deleteGameFromGameSet(Integer.parseInt(gameId), Integer.parseInt(gameSetId),
                loginResult.getUserId())) {
            throw new IllegalStateException("The user does not have permission to delete the specified game from a " +
                    "collection");
        }
    }

    @Override
    public void deleteKifu(final String sessionId, final String kifuId) {
        LOGGER.log(Level.INFO, "deleteKifu: " + kifuId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a kifu");
        }

        if (!kifuRepository.deleteKifuById(Integer.parseInt(kifuId), loginResult.getUserId())) {
            throw new IllegalArgumentException("Could not delete the Kifu");
        }
    }

    @Override
    public LessonDetails[] getAllPublicLessons(final String sessionId) {
        LOGGER.log(Level.INFO, "getAllPublicLessons");

        List<PersistentLesson> allVisibleLessons = lessonRepository.getAllVisibleLessons();
        return allVisibleLessons.stream().map(this::getLessonDetails).toArray(LessonDetails[]::new);
    }

    @Override
    public LessonDetails[] getAllLessons(final String sessionId) {
        LOGGER.log(Level.INFO, "getAllLessons");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Restricted to admins");
        }

        List<PersistentLesson> allVisibleLessons = lessonRepository.getAllLessons();
        return allVisibleLessons.stream().map(this::getLessonDetails).toArray(LessonDetails[]::new);
    }


    @Override
    public void createLesson(final String sessionId, final LessonDetails lesson) {
        LOGGER.log(Level.INFO, "getAllLessons");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Restricted to admins");
        }

        lessonRepository.saveLesson(getPersistentLesson(lesson));
    }

    private LessonDetails getLessonDetails(final PersistentLesson lesson) {
        LessonDetails details = new LessonDetails();
        details.setLessonId(String.valueOf(lesson.getId()));
        details.setIndex(lesson.getIndex());
        details.setTitle(lesson.getTitle());
        details.setDescription(lesson.getDescription());
        details.setKifuId(String.valueOf(lesson.getKifuId()));
        details.setProblemCollectionId(String.valueOf(lesson.getProblemCollectionId()));
        details.setDifficulty(lesson.getDifficulty());
        details.setTags(lesson.getTags());
        details.setPreviewSfen(lesson.getPreviewSfen());
        details.setHidden(lesson.isHidden());
        details.setLikes(lesson.getLikes());
        details.setAuthor(UsersCache.INSTANCE.getUserName(lesson.getAuthorId()));
        details.setParentLessonId(String.valueOf(lesson.getParentId()));
        return details;
    }

    private PersistentLesson getPersistentLesson(final LessonDetails details) {
        return new PersistentLesson(
                0,
                details.getKifuId() == null ? null : Integer.parseInt(details.getKifuId()),
                details.getProblemCollectionId() == null ? null : Integer.parseInt(details.getProblemCollectionId()),
                details.getParentLessonId() == null ? null : Integer.parseInt(details.getParentLessonId()),
                details.getTitle(),
                details.getDescription(),
                details.getTags(),
                details.getPreviewSfen(),
                details.getDifficulty(),
                details.getLikes(),
                UsersCache.INSTANCE.getUserId(details.getAuthor()),
                details.isHidden(),
                null,
                null,
                details.getProblemCollectionId() != null ? PersistentLesson.LessonType.PRACTICE :
                        (details.getKifuId() != null ? PersistentLesson.LessonType.LECTURE :
                                PersistentLesson.LessonType.UNSPECIFIED),
                details.getIndex()
        );
    }

    @Override
    public void updateKifuUsf(final String sessionId, final String kifuId, final String kifuUsf) {
        LOGGER.log(Level.INFO, "updateKifuUsf: " + kifuId + "\n" + kifuUsf);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a kifu");
        }

        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(kifuUsf);
        if (!kifuRepository.updateKifu(Integer.parseInt(kifuId), loginResult.getUserId(), gameRecord)) {
            throw new IllegalArgumentException("Could not update the kifu.");
        }
    }

    @Override
    public TournamentDetails getTournament(final String sessionID, final String tournamentID) {
        LOGGER.log(Level.INFO, "gettournament: " + tournamentID);
        TournamentDetails tournamentDetails = new TournamentDetails();
        tournamentDetails.setTitle("Tourney To Series");
        tournamentDetails.setDescription("The best shogi tournament on Earth");
        tournamentDetails.setOrganizer("Shogi Harbour");
        //tournamentDetails.setSeasons(new TournamentSeasonDetails[]);
        return tournamentDetails;
    }
}
