package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class GameCollectionDetails implements Serializable {
    private String id;
    private String name;

    public GameCollectionDetails() {
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

    @Override
    public String toString() {
        return "GameCollectionDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
