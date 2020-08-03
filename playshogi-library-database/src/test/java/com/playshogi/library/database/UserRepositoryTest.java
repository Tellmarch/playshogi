package com.playshogi.library.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = new UserRepository(new DbConnection());
        userRepository.insertUser("admin", "test");
        //userRepository.insertUserPbStats(new PersistentUserProblemStats(1, 1, null, 10, true));
    }

    @Test
    void testAuthenticateUser() {
        assertNotNull(userRepository.authenticateUser("admin", "test"));
        //userRepository.authenticateUser("admin", "test2");
        //userRepository.getUserPbStats(1);
    }
}