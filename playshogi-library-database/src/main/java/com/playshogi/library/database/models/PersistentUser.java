package com.playshogi.library.database.models;

public class PersistentUser {

    private final int id;
    private final String username;
    private final boolean admin;

    public PersistentUser(final int id, final String username, final boolean admin) {
        this.id = id;
        this.username = username;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "PersistentUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", admin=" + admin +
                '}';
    }
}
