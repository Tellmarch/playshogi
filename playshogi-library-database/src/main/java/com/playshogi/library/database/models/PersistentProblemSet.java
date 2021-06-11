package com.playshogi.library.database.models;

import java.util.Arrays;

public class PersistentProblemSet {

    private final int id;
    private final String name;
    private final String description;
    private final Visibility visibility;
    private final Integer ownerId;
    private final Integer difficulty;
    private final String[] tags;


    public PersistentProblemSet(final int id, final String name, final String description, final Visibility visibility,
                                final Integer ownerId, final Integer difficulty, final String[] tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.ownerId = ownerId;
        this.difficulty = difficulty;
        this.tags = tags;
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

    public Integer getDifficulty() {
        return difficulty;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "PersistentProblemSet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                ", ownerId=" + ownerId +
                ", difficulty=" + difficulty +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
