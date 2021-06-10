package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

import java.util.Arrays;

public class SaveDraftCollectionEvent extends GenericEvent {

    public enum Type {
        KIFUS,
        GAMES,
        PROBLEMS,
    }

    private final String id;
    private final String title;
    private final String description;
    private final String visibility;
    private final Integer difficulty;
    private final String[] tags;
    private final Type type;

    public SaveDraftCollectionEvent(final String id, final String title, final String description,
                                    final String visibility, final Integer difficulty, final String[] tags,
                                    final Type type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.difficulty = difficulty;
        this.tags = tags;
        this.type = type;
    }

    public static SaveDraftCollectionEvent ofKifus(final String id) {
        return new SaveDraftCollectionEvent(id, null, null, null, null, null, Type.KIFUS);
    }

    public static SaveDraftCollectionEvent ofProblems(final String id, final String title, final String description,
                                                      final String visibility, final Integer difficulty,
                                                      final String[] tags) {
        return new SaveDraftCollectionEvent(id, title, description, visibility, difficulty, tags, Type.PROBLEMS);
    }

    public static SaveDraftCollectionEvent ofGames(final String id, final String title, final String description,
                                                   final String visibility) {
        return new SaveDraftCollectionEvent(id, title, description, visibility, null, null, Type.GAMES);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVisibility() {
        return visibility;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public String[] getTags() {
        return tags;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SaveDraftCollectionEvent{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", difficulty=" + difficulty +
                ", tags=" + Arrays.toString(tags) +
                ", type=" + type +
                '}';
    }
}
