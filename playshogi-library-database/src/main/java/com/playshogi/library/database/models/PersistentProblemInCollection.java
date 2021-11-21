package com.playshogi.library.database.models;

public class PersistentProblemInCollection {
    private PersistentProblem problem;
    private int problemSetId;
    private int index;
    private boolean hidden;

    public PersistentProblemInCollection(final PersistentProblem problem, final int problemSetId, final int index,
                                         final boolean hidden) {
        this.problem = problem;
        this.problemSetId = problemSetId;
        this.index = index;
        this.hidden = hidden;
    }

    public PersistentProblem getProblem() {
        return problem;
    }

    public int getProblemSetId() {
        return problemSetId;
    }

    public int getIndex() {
        return index;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        return "PersistentProblemInCollection{" +
                "problem=" + problem +
                ", problemSetId=" + problemSetId +
                ", index=" + index +
                ", hidden=" + hidden +
                '}';
    }
}
