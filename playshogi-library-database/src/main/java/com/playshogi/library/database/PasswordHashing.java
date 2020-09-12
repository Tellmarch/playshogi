package com.playshogi.library.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class PasswordHashing {

    private static final String SALT = "7NoJpO81MYIKDLFIqOnWpCe0C+1M4r8GT1W9RMUW7mu+zUYgTREo+TGd02tUMHE8QV9" +
            "+jOkya1r4djFZeEzEGA";

    static String hash(final String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String salted = password + SALT;
        byte[] bytes = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }

}
