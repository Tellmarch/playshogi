package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class ProblemCollectionDetailsAndProblems implements Serializable {
    private ProblemCollectionDetails details;
    private ProblemDetails[] problems;

    public ProblemCollectionDetailsAndProblems() {
    }

    public ProblemCollectionDetailsAndProblems(final ProblemCollectionDetails details,
                                               final ProblemDetails[] problems) {
        this.details = details;
        this.problems = problems;
    }

    public ProblemCollectionDetails getDetails() {
        return details;
    }

    public void setDetails(final ProblemCollectionDetails details) {
        this.details = details;
    }

    public ProblemDetails[] getProblems() {
        return problems;
    }

    public void setProblems(final ProblemDetails[] problems) {
        this.problems = problems;
    }

    @Override
    public String toString() {
        return "ProblemCollectionDetailsAndProblems{" +
                "details=" + details +
                ", problems=" + Arrays.toString(problems) +
                '}';
    }
}
