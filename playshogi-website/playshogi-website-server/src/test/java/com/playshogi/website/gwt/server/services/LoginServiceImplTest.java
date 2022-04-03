package com.playshogi.website.gwt.server.services;

import org.junit.Ignore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class LoginServiceImplTest {

    @org.junit.Test
    public void validateUserName() {
        LoginServiceImpl loginService = new LoginServiceImpl();
        assertTrue(loginService.validateUserName("_abcDEF24"));
        assertFalse(loginService.validateUserName("ab"));
        assertFalse(loginService.validateUserName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertFalse(loginService.validateUserName("<br>"));
    }
}