package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class ProblemCollectionDetails implements Serializable {
    private String id;
    private String name;
    private String description;
    private String visibility;
    private String type;
    private int numProblems;

    public ProblemCollectionDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public int getNumProblems() {
        return numProblems;
    }

    public void setNumProblems(final int numProblems) {
        this.numProblems = numProblems;
    }

    @Override
    public String toString() {
        return "ProblemCollectionDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", type='" + type + '\'' +
                ", numProblems=" + numProblems +
                '}';
    }
}
