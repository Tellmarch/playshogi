package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.website.gwt.shared.services.UserService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void saveLessonProgress(final String sessionId, final String lessonId, final int timeMs,
                                   final boolean complete, final int percentage, final int rating) {

        LOGGER.log(Level.INFO, "saveLessonProgress:" + lessonId + " - " + percentage);

    }
}
