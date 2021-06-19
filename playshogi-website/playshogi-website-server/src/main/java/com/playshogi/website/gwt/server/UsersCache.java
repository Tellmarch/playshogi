package com.playshogi.website.gwt.server;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.UserRepository;
import com.playshogi.library.database.models.PersistentUser;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public enum UsersCache {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(UsersCache.class.getName());

    private final UserRepository userRepository = new UserRepository(new DbConnection());

    private final ConcurrentHashMap<Integer, PersistentUser> users = new ConcurrentHashMap<>();

    public final String getUserName(final int userId) {
        if (!users.containsKey(userId)) {
            PersistentUser user = userRepository.getUserById(userId);
            if (user == null) {
                return null;
            }
            users.put(userId, user);
        }
        return users.get(userId).getUsername();
    }
}
