package com.playshogi.website.gwt.server.controllers;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.UserRepository;
import com.playshogi.library.database.models.PersistentUser;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public enum UsersCache {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(UsersCache.class.getName());

    private final UserRepository userRepository = new UserRepository(new DbConnection());

    private final ConcurrentHashMap<Integer, PersistentUser> persistentUsers = new ConcurrentHashMap<>();

    public final String getUserName(final int userId) {
        if (!persistentUsers.containsKey(userId)) {
            PersistentUser user = userRepository.getUserById(userId);
            if (user == null) {
                return null;
            }
            persistentUsers.put(userId, user);
        }
        return persistentUsers.get(userId).getUsername();
    }

    public Integer getUserId(final String author) {
        // TODO
        return null;
    }
}
