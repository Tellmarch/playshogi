package com.playshogi.website.gwt.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.UserRepository;
import com.playshogi.library.database.models.PersistentUserLessonProgress;
import com.playshogi.website.gwt.server.controllers.Authenticator;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.UserService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    private final Authenticator authenticator = Authenticator.INSTANCE;
    private final UserRepository userRepository;

    public UserServiceImpl() {
        DbConnection dbConnection = new DbConnection();
        userRepository = new UserRepository(dbConnection);
    }

    @Override
    public void saveLessonProgress(final String sessionId, final String lessonId, final int timeMs,
                                   final boolean complete, final int percentage, final Integer rating) {

        LOGGER.log(Level.INFO, "saveLessonProgress:" + lessonId + " - " + percentage);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can save lesson progress");
        }

        userRepository.insertUserLessonProgress(new PersistentUserLessonProgress(loginResult.getUserId(),
                Integer.parseInt(lessonId), null, timeMs, complete, percentage, rating));
    }
}
