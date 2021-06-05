package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.*;
import com.playshogi.library.database.models.*;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
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
    private final Authenticator authenticator = Authenticator.INSTANCE;

    private Map<String, Integer> highScores = new HashMap<>();

    public ProblemsServiceImpl() {
        DbConnection dbConnection = new DbConnection();
        problemRepository = new ProblemRepository(dbConnection);
        kifuRepository = new KifuRepository(dbConnection);
        userRepository = new UserRepository(dbConnection);
        gameSetRepository = new GameSetRepository(dbConnection);
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

    private ProblemDetails getProblemDetails(PersistentProblem persistentProblem, String usf) {
        ProblemDetails problemDetails = new ProblemDetails();
        problemDetails.setId("" + persistentProblem.getId());
        problemDetails.setKifuId(persistentProblem.getKifuId());
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
    public String saveProblemsCollection(final String sessionId, final String draftId) {
        LOGGER.log(Level.INFO, "saveProblemsCollection: " + draftId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Only administrators can save a problems collection");
        }

        KifuCollection collection = CollectionUploads.INSTANCE.getCollection(draftId);

        if (collection == null) {
            throw new IllegalStateException("Invalid draft connection ID");
        }

        String id = UUID.randomUUID().toString();

        ProblemsCache.INSTANCE.saveProblemsCollection(collection);

        return id;
    }

    @Override
    public ProblemCollectionDetails[] getProblemCollections(final String sessionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Only administrators can see problems collections");
        }

        return ProblemsCache.INSTANCE.getProblemsCollectionDetails();
    }

    @Override
    public ProblemCollectionDetails[] getPublicProblemCollections(final String sessionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        ArrayList<ProblemCollectionDetails> publicCollectionDetails = new ArrayList<>();

        List<PersistentGameSet> publicGameSets = gameSetRepository.getAllPublicGameSets();
        for (PersistentGameSet gameSet : publicGameSets) {
            ProblemCollectionDetails details = getCollectionDetailsFromGameSet(gameSet);
            if (details != null) {
                publicCollectionDetails.add(details);
            }
        }

        return publicCollectionDetails.toArray(new ProblemCollectionDetails[0]);
    }

    private ProblemCollectionDetails getCollectionDetailsFromGameSet(final PersistentGameSet gameSet) {
        ProblemCollectionDetails details = new ProblemCollectionDetails();
        details.setId(String.valueOf(gameSet.getId()));
        details.setName(gameSet.getName());
        details.setDescription(gameSet.getDescription());
        details.setVisibility(gameSet.getVisibility().toString().toLowerCase());
        // TODO: this is a temporary hack...
        if (details.getName().contains("Tsume") ||
                details.getName().contains("Castle") ||
                details.getName().contains("Problem")) {
            details.setType("problems");
        } else {
            return null;
        }
        return details;
    }

    @Override
    public ProblemCollectionDetailsAndProblems getProblemCollection(final String sessionId, final String collectionId) {
        LOGGER.log(Level.INFO, "getProblemCollections");

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            throw new IllegalStateException("Only administrators can see problems collections");
        }

        return ProblemsCache.INSTANCE.getProblemCollectionDetailsAndProblems(collectionId);
    }

    private static Map<String, Integer> sortByValueDesc(final Map<String, Integer> scores) {
        return scores.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

}
