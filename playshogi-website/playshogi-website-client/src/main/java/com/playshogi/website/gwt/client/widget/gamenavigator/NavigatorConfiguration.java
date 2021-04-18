package com.playshogi.website.gwt.client.widget.gamenavigator;

public class NavigatorConfiguration {

    private boolean problemMode = false;

    public NavigatorConfiguration() {
    }

    public boolean isProblemMode() {
        return problemMode;
    }

    public void setProblemMode(final boolean problemMode) {
        this.problemMode = problemMode;
    }

    @Override
    public String toString() {
        return "NavigatorConfiguration [problemMode=" + problemMode + "]";
    }

}
