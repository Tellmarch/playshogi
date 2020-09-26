package com.playshogi.library.database.models;

public class PersistentGameSet {

    // Stored in DB as the ordinal - only add values at the end
    public enum Visibility {
        PRIVATE, UNLISTED, PUBLIC
    }

    private final int id;
    private final String name;
    private final String description;
    private final Visibility visibility;
    private final Integer ownerId;


    public PersistentGameSet(final int id, final String name, final String description, final Visibility visibility,
                             final Integer ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "PersistentGameSet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                ", ownerId=" + ownerId +
                '}';
    }
}
