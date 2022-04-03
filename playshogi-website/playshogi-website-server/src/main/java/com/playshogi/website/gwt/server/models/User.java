package com.playshogi.website.gwt.server.models;

import com.playshogi.website.gwt.server.controllers.UsersCache;

import java.util.Objects;

public class User {
    private final int userId;
    private volatile String userName;

    public User(final int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        if (userName == null) {
            userName = UsersCache.INSTANCE.getUserName(userId);
        }
        return userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
