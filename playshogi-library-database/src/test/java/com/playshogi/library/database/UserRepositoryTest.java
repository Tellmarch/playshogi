package com.playshogi.library.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserRepositoryTest {

    private UserRepository userRepository;

    @Before
    public void setup() {
        userRepository = new UserRepository(new DbConnection());
        userRepository.insertUser("admin", "test");
        //userRepository.insertUserPbStats(new PersistentUserProblemStats(1, 1, null, 10, true));
    }

    @Test
    public void testAuthenticateUser() {
        assertNotNull(userRepository.authenticateUser("admin", "test"));
        //userRepository.authenticateUser("admin", "test2");
        //userRepository.getUserPbStats(1);
    }
}