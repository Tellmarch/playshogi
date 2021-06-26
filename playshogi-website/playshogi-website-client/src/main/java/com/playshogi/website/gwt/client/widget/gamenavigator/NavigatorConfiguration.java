package com.playshogi.website.gwt.client.widget.gamenavigator;

public class NavigatorConfiguration {

    public static final NavigatorConfiguration PROBLEMS = new NavigatorConfiguration(true, false);
    public static final NavigatorConfiguration LESSONS = new NavigatorConfiguration(false, true);

    private final boolean problemMode;
    private final boolean fireVisitedProgress;

    public NavigatorConfiguration() {
        this(false, false);
    }

    public NavigatorConfiguration(final boolean problemMode, final boolean fireVisitedProgress) {
        this.problemMode = problemMode;
        this.fireVisitedProgress = fireVisitedProgress;
    }

    public boolean isProblemMode() {
        return problemMode;
    }

    public boolean isFireVisitedProgress() {
        return fireVisitedProgress;
    }

    @Override
    public String toString() {
        return "NavigatorConfiguration{" +
                "problemMode=" + problemMode +
                ", visitedProgress=" + fireVisitedProgress +
                '}';
    }

}
