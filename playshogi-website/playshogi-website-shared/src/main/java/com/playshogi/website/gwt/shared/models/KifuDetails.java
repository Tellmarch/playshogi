package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Date;

public class KifuDetails implements Serializable {
    public enum KifuType {
        GAME,
        PROBLEM,
        LESSON
    }

    private String id;
    private String name;
    private Date creationDate;
    private Date updateDate;
    private KifuType type;

    public KifuDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(final Date updateDate) {
        this.updateDate = updateDate;
    }

    public KifuType getType() {
        return type;
    }

    public void setType(final KifuType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "KifuDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", type='" + type + '\'' +
                '}';
    }
}
