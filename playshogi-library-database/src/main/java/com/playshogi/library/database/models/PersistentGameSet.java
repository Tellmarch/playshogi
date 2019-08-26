package com.playshogi.library.database.models;

public class PersistentGameSet {

    private final int id;
    private final String name;

    public PersistentGameSet(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PersistentGameSet [id=" + id + ", name=" + name + "]";
    }

}
