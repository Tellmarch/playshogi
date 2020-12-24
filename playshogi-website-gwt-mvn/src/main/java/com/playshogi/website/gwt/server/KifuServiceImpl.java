package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.*;
import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentGameSet;
import com.playshogi.library.database.models.PersistentGameSetMove;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.models.record.GameCollection;
import com.playshogi.library.models.record.GameInformation;
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

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a game");
        }

        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(kifuUsf);
        String name = UUID.randomUUID().toString();
        int kifuId = kifuRepository.saveKifu(gameRecord, name, 1, KifuType.GAME);
        return String.valueOf(kifuId);
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
        String name = defaultName.length() <= 40 ? defaultName : defaultName.substring(0, 40);
        if (!gameSetRepository.addGameToGameSet(gameRecord, Integer.parseInt(collectionId), 1, name,
                loginResult.getUserId())) {
            throw new IllegalArgumentException("Could not save the kifu in database");
        }
    }

    private String getDefaultName(final GameRecord gameRecord) {
        GameInformation info = gameRecord.getGameInformation();
        return info.getVenue() + " - " + info.getSente() + " - " + info.getGote() + " - " + info.getDate();
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
    public GameCollectionDetailsList getGameCollections(final String sessionId) {

        ArrayList<GameCollectionDetails> publicCollectionDetails = new ArrayList<>();

        List<PersistentGameSet> publicGameSets = gameSetRepository.getAllPublicGameSets();
        for (PersistentGameSet gameSet : publicGameSets) {
            publicCollectionDetails.add(getCollectionDetails(gameSet));
        }

        ArrayList<GameCollectionDetails> userCollectionDetails = new ArrayList<>();

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            List<PersistentGameSet> userGameSets = gameSetRepository.getGameSetsForUser(loginResult.getUserId());
            for (PersistentGameSet gameSet : userGameSets) {
                userCollectionDetails.add(getCollectionDetails(gameSet));
            }
        }

        GameCollectionDetailsList result = new GameCollectionDetailsList();
        result.setPublicCollections(publicCollectionDetails.toArray(new GameCollectionDetails[0]));
        result.setMyCollections(userCollectionDetails.toArray(new GameCollectionDetails[0]));

        return result;
    }

    private GameCollectionDetails getCollectionDetails(final PersistentGameSet gameSet) {
        GameCollectionDetails details = new GameCollectionDetails();
        details.setId(String.valueOf(gameSet.getId()));
        details.setName(gameSet.getName());
        details.setDescription(gameSet.getDescription());
        details.setVisibility(gameSet.getVisibility().toString().toLowerCase());
        // TODO: this is a temporary hack...
        if (details.getName().contains("Tsume") || details.getName().contains("Castle")) {
            details.setType("problems");
        } else {
            details.setType("games");
        }
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
        result.setGames(games.stream().map(this::createKifuDetails).toArray(GameDetails[]::new));

        return result;
    }

    private GameDetails createKifuDetails(final PersistentGame game) {
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

        int id = gameSetRepository.saveGameSet(collection.getName(), collection.getName(),
                PersistentGameSet.Visibility.PRIVATE, loginResult.getUserId());

        if (id == -1) {
            LOGGER.log(Level.INFO, "Error saving the game set");
            throw new IllegalStateException("Error saving the game set");
        }

        int i = 1;
        for (GameRecord game : collection.getGames()) {
            gameSetRepository.addGameToGameSet(game, id, 1, "Game #" + (i++), loginResult.getUserId());
        }

        return String.valueOf(id);
    }

    @Override
    public void updateGameCollectionDetails(final String sessionId, final GameCollectionDetails details) {
        LOGGER.log(Level.INFO, "saveGameCollectionDetails: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can update a game collection");
        }

        gameSetRepository.updateGameSet(Integer.parseInt(details.getId()), details.getName(), details.getDescription(),
                PersistentGameSet.Visibility.valueOf(details.getVisibility().toUpperCase()), loginResult.getUserId());
    }


    @Override
    public void createGameCollection(final String sessionId, final GameCollectionDetails details) {
        LOGGER.log(Level.INFO, "createGameCollection: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can create a game collection");
        }

        gameSetRepository.saveGameSet(details.getName(), details.getDescription(),
                PersistentGameSet.Visibility.valueOf(details.getVisibility().toUpperCase()), loginResult.getUserId());
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
}
