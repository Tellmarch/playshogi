package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.*;
import com.playshogi.library.database.models.*;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.KifuCollection;
import com.playshogi.website.gwt.shared.models.*;
import com.playshogi.website.gwt.shared.services.ProblemsService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProblemsServiceImpl extends RemoteServiceServlet implements ProblemsService {

    private static final Logger LOGGER = Logger.getLogger(ProblemsServiceImpl.class.getName());

    private final ProblemRepository problemRepository;
    private final KifuRepository kifuRepository;
    private final UserRepository userRepository;
    private final GameSetRepository gameSetRepository;
    private final ProblemSetRepository problemSetRepository;
    private final GameRepository gameRepository;
    private final Authenticator authenticator = Authenticator.INSTANCE;

    private final Map<String, Integer> highScores = new HashMap<>();

    public ProblemsServiceImpl() {
        DbConnection dbConnection = new DbConnection();
        problemRepository = new ProblemRepository(dbConnection);
        kifuRepository = new KifuRepository(dbConnection);
        userRepository = new UserRepository(dbConnection);
        gameSetRepository = new GameSetRepository(dbConnection);
        problemSetRepository = new ProblemSetRepository(dbConnection);
        gameRepository = new GameRepository(dbConnection);
        initHighScores();
    }

    private void initHighScores() {
        highScores.put("Pro", 18);
        List<PersistentHighScore> scores = userRepository.getHighScoresForEvent("byoyomi");
        for (PersistentHighScore score : scores) {
            highScores.put(score.getName(), score.getScore());
        }
    }

    @Override
    public ProblemDetails getProblem(final String problemId) {
        LOGGER.log(Level.INFO, "getting problem: " + problemId);

        PersistentProblem persistentProblem = problemRepository.getProblemById(Integer.parseInt(problemId));

        if (persistentProblem == null) {
            LOGGER.log(Level.INFO, "Could not load problem");
            return null;
        }

        return queryProblemDetails(persistentProblem);
    }

    @Override
    public ProblemDetails getProblem(final ProblemOptions options) {
        LOGGER.log(Level.INFO, "getting problem with options: " + options);

//        return ProblemsCache.INSTANCE.getProblem(options);

        if (options.isRandom()) {
            if (options.getNumMoves() != 0) {
                return getRandomProblem(options.getNumMoves());
            } else {
                return getRandomProblem();
            }
        } else {
            String previousId = options.getPreviousProblemId();
            if (previousId == null) {
                return getProblem("1");
            } else {
                return getProblem(String.valueOf(Integer.parseInt(previousId) + 1));
            }
        }
    }

    @Override
    public ProblemDetails getRandomProblem() {
        LOGGER.log(Level.INFO, "getting random problem");

        PersistentProblem persistentProblem = problemRepository.getRandomProblem();

        if (persistentProblem == null) {
            LOGGER.log(Level.INFO, "Could not load a random problem");
            return null;
        }

        return queryProblemDetails(persistentProblem);
    }

    @Override
    public ProblemDetails getRandomProblem(int numMoves) {
        LOGGER.log(Level.INFO, "getting random problem of " + numMoves + " moves");

        PersistentProblem persistentProblem = problemRepository.getRandomProblem(numMoves);

        if (persistentProblem == null) {
            LOGGER.log(Level.INFO, "Could not load a random problem of " + numMoves + " moves");
            return null;
        }

        return queryProblemDetails(persistentProblem);
    }

    private ProblemDetails queryProblemDetails(PersistentProblem persistentProblem) {
        PersistentKifu persistentKifu = kifuRepository.getKifuById(persistentProblem.getKifuId());

        if (persistentKifu == null) {
            LOGGER.log(Level.INFO, "Could not load the problem kifu for id " + persistentProblem.getKifuId());
            return null;
        }

        String usf = UsfFormat.INSTANCE.write(persistentKifu.getKifu());
        LOGGER.log(Level.INFO, "Sending problem:\n" + usf);

        return getProblemDetails(persistentProblem, usf);
    }


    private ProblemDetails getProblemDetails(final PersistentProblem persistentProblem) {
        return getProblemDetails(persistentProblem, null);
    }

    private ProblemDetails getProblemDetails(final PersistentProblem persistentProblem, final String usf) {
        ProblemDetails problemDetails = new ProblemDetails();
        problemDetails.setId("" + persistentProblem.getId());
        problemDetails.setKifuId(String.valueOf(persistentProblem.getKifuId()));
        problemDetails.setNumMoves(persistentProblem.getNumMoves());
        problemDetails.setElo(persistentProblem.getElo());
        problemDetails.setPbType(persistentProblem.getPbType().getDescription());
        problemDetails.setUsf(usf);
        return problemDetails;
    }

    @Override
    public void saveUserProblemAttempt(String sessionId, String problemId, boolean success, int timeMs) {
        LOGGER.log(Level.INFO, "Saving pb stats for the user");
        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            PersistentUserProblemStats userProblemStats = new PersistentUserProblemStats(loginResult.getUserId(),
                    Integer.parseInt(problemId), timeMs, success);
            userRepository.insertUserPbStats(userProblemStats);
            LOGGER.log(Level.INFO, "Saved pb stats for the user: " + userProblemStats);
        } else {
            LOGGER.log(Level.INFO, "Not saving stats for guest user");
        }
    }

    @Override
    public ProblemStatisticsDetails[] getProblemStatisticsDetails(String sessionId) {
        LOGGER.log(Level.INFO, "Retrieving pb stats for the user");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            List<PersistentUserProblemStats> userPbStats = userRepository.getUserPbStats(loginResult.getUserId());
            ProblemStatisticsDetails[] stats = new ProblemStatisticsDetails[userPbStats.size()];
            for (int i = 0; i < userPbStats.size(); i++) {
                PersistentUserProblemStats problemStats = userPbStats.get(i);
                stats[i] = new ProblemStatisticsDetails();
                stats[i].setProblemId(problemStats.getProblemId());
                stats[i].setAttemptedDate(problemStats.getAttemptedDate());
                stats[i].setCorrect(problemStats.getCorrect());
                stats[i].setTimeSpentMs(problemStats.getTimeSpentMs());
            }
            LOGGER.log(Level.INFO, "Retrieved pb stats for the user: " + Arrays.toString(stats));
            return stats;
        } else {
            LOGGER.log(Level.INFO, "No stats for guest user");
            return new ProblemStatisticsDetails[0];
        }
    }

    @Override
    public void saveHighScore(final String userName, final int score) {
        LOGGER.log(Level.INFO, "Saving high score: " + userName + " " + score);
        try {
            if (userName == null) {
                return;
            }
            String sanitizedUserName = userName.length() > 20 ? userName.substring(0, 20) : userName;
            if (!highScores.containsKey(sanitizedUserName) || highScores.get(sanitizedUserName) < score) {
                highScores.put(sanitizedUserName, score);
            }
            userRepository.insertUserHighScore(userName, score, null, "byoyomi");
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception in saveHighScore:", ex);
        }
    }

    @Override
    public SurvivalHighScore[] getHighScores() {

        List<SurvivalHighScore> survivalHighScores = new ArrayList<>();

        Map<String, Integer> map = sortByValueDesc(highScores);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                survivalHighScores.add(new SurvivalHighScore(entry.getKey(), entry.getValue()));
            }
            if (survivalHighScores.size() >= 20) break;
        }

        return survivalHighScores.toArray(new SurvivalHighScore[0]);
    }

    @Override
    public void saveCollectionTime(final String sessionId, final String collectionId, final int timeMs,
                                   final boolean complete, final int solved) {
        LOGGER.log(Level.INFO, "Saving pb stats for the user");
        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult != null && loginResult.isLoggedIn()) {
            PersistentUserProblemSetStats userProblemStats = new PersistentUserProblemSetStats(loginResult.getUserId(),
                    Integer.parseInt(collectionId), timeMs, complete, solved);
            userRepository.insertUserPbSetStats(userProblemStats);
            LOGGER.log(Level.INFO, "Saved pbset stats for the user: " + userProblemStats);
        } else {
            LOGGER.log(Level.INFO, "Not saving stats for guest user");
        }
    }

    @Override
    public String saveProblemsCollection(final String sessionId, final String draftId,
                                         final ProblemCollectionDetails details) {
        LOGGER.log(Level.INFO, "saveProblemsCollection: " + draftId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged-in users can save a problems collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft connection ID");
        }

        String name = details.getName() != null ? details.getName() : collection.getName();
        String description = details.getDescription() != null ? details.getDescription() : collection.getName();
        Visibility visibility = details.getVisibility() == null ?
                Visibility.UNLISTED :
                Visibility.valueOf(details.getVisibility().toUpperCase());
        int userId = loginResult.getUserId();
        int difficulty = details.getDifficulty();

        int id = problemSetRepository.saveProblemSet(name, description, visibility, userId, difficulty,
                details.getTags());

        if (id == -1) {
            LOGGER.log(Level.INFO, "Error saving the problem set");
            throw new IllegalStateException("Error saving the problem set");
        }

        int i = 1;
        for (GameRecord game : collection.getKifus()) {
            problemSetRepository.addProblemToProblemSet(game, id, "Problem #" + (i++), loginResult.getUserId(), 0,
                    PersistentProblem.ProblemType.UNSPECIFIED, false);
        }

        return String.valueOf(id);
    }

    @Override
    public void addDraftToProblemCollection(final String sessionId, final String draftId, final String collectionId) {
        LOGGER.log(Level.INFO, "addDraftToProblemCollection: " + draftId + " " + collectionId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save a problems collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft connection ID");
        }

        PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));

        if (problemSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (problemSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to add problems to this collection");
        }

        int i = 1;
        for (GameRecord game : collection.getKifus()) {
            problemSetRepository.addProblemToProblemSet(game, Integer.parseInt(collectionId), "Problem #" + (i++),
                    loginResult.getUserId(), 0, PersistentProblem.ProblemType.UNSPECIFIED, false);
        }
    }

    @Override
    public ProblemCollectionDetails[] getProblemCollections(final String sessionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Only administrators can see problems collections");
        }

        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public ProblemCollectionDetails[] getPublicProblemCollections(final String sessionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        List<PersistentProblemSet> problemSets = problemSetRepository.getAllPublicProblemSets();

        LoginResult loginResult = authenticator.checkSession(sessionId);
        Integer userId = loginResult != null && loginResult.isLoggedIn() ? loginResult.getUserId() : null;

        return problemSets.stream()
                .map((PersistentProblemSet persistentProblemSet) ->
                        getProblemCollectionDetails(persistentProblemSet, userId))
                .toArray(ProblemCollectionDetails[]::new);
    }

    @Override
    public ProblemCollectionDetails[] getUserProblemCollections(final String sessionId, final String userName) {
        LOGGER.log(Level.INFO, "getUserProblemCollections");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !userName.equals(loginResult.getUserName())) {
            throw new IllegalStateException("User does not have permissions for this operation");
        }

        List<PersistentProblemSet> userProblemSets = problemSetRepository.getUserProblemSets(loginResult.getUserId());

        return userProblemSets.stream()
                .map((PersistentProblemSet persistentProblemSet) ->
                        getProblemCollectionDetails(persistentProblemSet, loginResult.getUserId()))
                .toArray(ProblemCollectionDetails[]::new);
    }

    private ProblemCollectionDetails getProblemCollectionDetails(final PersistentProblemSet persistentProblemSet,
                                                                 final Integer userId) {
        ProblemCollectionDetails details = new ProblemCollectionDetails();
        details.setName(persistentProblemSet.getName());
        details.setDescription(persistentProblemSet.getDescription());
        details.setId(String.valueOf(persistentProblemSet.getId()));
        details.setVisibility(persistentProblemSet.getVisibility().name());
        details.setTags(persistentProblemSet.getTags());
        details.setDifficulty(persistentProblemSet.getDifficulty());
        fillLeaderBoard(persistentProblemSet, details, userId);
        details.setNumProblems(problemSetRepository.getProblemsCountFromProblemSet(persistentProblemSet.getId()));
        details.setAuthor(UsersCache.INSTANCE.getUserName(persistentProblemSet.getOwnerId()));
        return details;
    }

    private void fillLeaderBoard(final PersistentProblemSet persistentProblemSet,
                                 final ProblemCollectionDetails details, final Integer userId) {
        List<PersistentUserProblemSetStats> highScores =
                userRepository.getCollectionHighScores(persistentProblemSet.getId());

        String[] leaderboardNames = new String[highScores.size()];
        String[] leaderboardScores = new String[highScores.size()];

        for (int i = 0; i < highScores.size(); i++) {
            leaderboardNames[i] = highScores.get(i).getUserName();
            Integer ms = highScores.get(i).getTimeSpentMs();
            leaderboardScores[i] = formatSpeedrunTime(ms);
        }

        details.setLeaderboardNames(leaderboardNames);
        details.setLeaderboardScores(leaderboardScores);

        if (userId != null) {
            PersistentUserProblemSetStats score =
                    userRepository.getCollectionHighScoreForUser(persistentProblemSet.getId(), userId);
            if (score != null) {
                details.setUserHighScore(formatSpeedrunTime(score.getTimeSpentMs()));
                details.setUserSolved(score.getSolved());
            }
        }
    }

    private String formatSpeedrunTime(final Integer ms) {
        return String.format(
                "%d:%02d.%03d",
                ms / 60000,
                (ms / 1000) % 60,
                ms % 1000);
    }

    @Override
    public ProblemCollectionDetailsAndProblems getProblemCollection(final String sessionId, final String collectionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        PersistentProblemSet gameSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));
        if (gameSet == null) {
            throw new IllegalArgumentException("Invalid problem collection ID");
        }

        List<PersistentProblem> games = problemSetRepository.getProblemsFromProblemSet(Integer.parseInt(collectionId));

        ProblemCollectionDetailsAndProblems result = new ProblemCollectionDetailsAndProblems();

        LoginResult loginResult = authenticator.checkSession(sessionId);
        Integer userId = loginResult != null && loginResult.isLoggedIn() ? loginResult.getUserId() : null;

        result.setDetails(getProblemCollectionDetails(gameSet, userId));
        result.setProblems(games.stream().map(this::getProblemDetails).toArray(ProblemDetails[]::new));

        return result;
    }

    @Override
    public void deleteProblemCollection(final String sessionId, final String problemSetId,
                                        final boolean alsoDeleteKifus) {
        LOGGER.log(Level.INFO, "deleteProblemCollection: " + problemSetId + " - with problems: " + alsoDeleteKifus);
        int setId = Integer.parseInt(problemSetId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a problem collection");
        }

        List<PersistentProblem> problems = problemSetRepository.getProblemsFromProblemSet(setId);

        if (!problemSetRepository.deleteProblemsetById(setId, loginResult.getUserId())) {
            throw new IllegalStateException("The user does not have permission to delete the specified problem " +
                    "collection");
        }

        for (PersistentProblem problem : problems) {
            problemRepository.deleteProblemById(problem.getId());

            if (alsoDeleteKifus) {
                kifuRepository.deleteKifuById(problem.getKifuId(), loginResult.getUserId());
            }
        }

    }

    @Override
    public void updateProblemCollectionDetails(final String sessionId,
                                               final ProblemCollectionDetails details) {
        LOGGER.log(Level.INFO, "updateProblemCollectionDetails: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can update a problem collection");
        }

        problemSetRepository.updateProblemSet(Integer.parseInt(details.getId()), details.getName(),
                details.getDescription(), Visibility.valueOf(details.getVisibility().toUpperCase()),
                details.getDifficulty(), details.getTags(), loginResult.getUserId());
    }

    private static Map<String, Integer> sortByValueDesc(final Map<String, Integer> scores) {
        return scores.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    @Override
    public void createProblemCollection(final String sessionId, final ProblemCollectionDetails details) {
        LOGGER.log(Level.INFO, "createProblemCollection: " + details);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can create a problem collection");
        }

        problemSetRepository.saveProblemSet(details.getName(), details.getDescription(),
                Visibility.valueOf(details.getVisibility().toUpperCase()), loginResult.getUserId(),
                details.getDifficulty(), details.getTags());

    }

    @Override
    public void saveProblemAndAddToCollection(final String sessionId, final String usf, final String collectionId) {
        LOGGER.log(Level.INFO, "saveProblemAndAddToCollection: " + usf);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a problem to a collection");
        }

        PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));

        if (problemSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (problemSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to add problems to this collection");
        }

        List<GameRecord> gameRecords = UsfFormat.INSTANCE.read(usf);
        int i = 1;
        for (GameRecord gameRecord : gameRecords) {
            problemSetRepository.addProblemToProblemSet(gameRecord, problemSet.getId(), "Problem #" + (i++),
                    loginResult.getUserId(),
                    0, PersistentProblem.ProblemType.UNSPECIFIED, false);
        }
    }

    @Override
    public void removeProblemFromCollection(final String sessionId, final String problemId, final String collectionId) {
        LOGGER.log(Level.INFO, "removeProblemFromCollection: " + problemId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a problem to a collection");
        }

        PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));

        if (problemSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (problemSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to remove problems to this collection");
        }

        problemSetRepository.deleteProblemFromProblemSet(Integer.parseInt(problemId), problemSet.getId(),
                loginResult.getUserId());
    }

    @Override
    public void addExistingKifuToProblemCollection(final String sessionId, final String kifuId,
                                                   final String collectionId) {
        LOGGER.log(Level.INFO, "addExistingKifuToProblemCollection: " + kifuId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a problem to a collection");
        }

        PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));

        if (problemSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (problemSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to add problems to this collection");
        }

        problemSetRepository.addKifuToProblemSet(problemSet.getId(), 0, PersistentProblem.ProblemType.UNSPECIFIED,
                Integer.parseInt(kifuId));
    }

    @Override
    public void convertGameCollection(final String sessionId, final String gameCollectionId) {
        LOGGER.log(Level.INFO, "convertGameCollection: " + gameCollectionId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a problem to a collection");
        }

        PersistentGameSet gameSet = gameSetRepository.getGameSetById(Integer.parseInt(gameCollectionId));

        if (gameSet == null) {
            throw new IllegalStateException("Invalid collection ID");
        }

        if (gameSet.getOwnerId() != loginResult.getUserId()) {
            throw new IllegalStateException("No permission to convert this collection");
        }

        List<PersistentGame> games = gameRepository.getGamesFromGameSet(Integer.parseInt(gameCollectionId));

        int problemSetId = problemSetRepository.saveProblemSet(gameSet.getName(), gameSet.getDescription(),
                gameSet.getVisibility(), loginResult.getUserId(),
                3, new String[0]);

        for (PersistentGame game : games) {
            problemSetRepository.addKifuToProblemSet(problemSetId, 0, PersistentProblem.ProblemType.UNSPECIFIED,
                    game.getKifuId());
        }

    }
}
