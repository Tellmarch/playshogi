package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PrincipalVariationDetails implements Serializable {

    private boolean timeout;
    private boolean noMate;
    private boolean forcedMate;
    private int numMovesBeforeMate;
    private int evaluationCP;
    private int depth;
    private int seldepth;
    private long nodes;
    private String principalVariation;

    public PrincipalVariationDetails() {
    }

    public boolean isForcedMate() {
        return forcedMate;
    }

    public void setForcedMate(boolean forcedMate) {
        this.forcedMate = forcedMate;
    }

    public int getNumMovesBeforeMate() {
        return numMovesBeforeMate;
    }

    public void setNumMovesBeforeMate(int numMovesBeforeMate) {
        this.numMovesBeforeMate = numMovesBeforeMate;
    }

    public int getEvaluationCP() {
        return evaluationCP;
    }

    public void setEvaluationCP(int evaluationCP) {
        this.evaluationCP = evaluationCP;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getSeldepth() {
        return seldepth;
    }

    public void setSeldepth(int seldepth) {
        this.seldepth = seldepth;
    }

    public long getNodes() {
        return nodes;
    }

    public void setNodes(long nodes) {
        this.nodes = nodes;
    }

    public String getPrincipalVariation() {
        return principalVariation;
    }

    public void setPrincipalVariation(String principalVariation) {
        this.principalVariation = principalVariation;
    }

    public boolean isNoMate() {
        return noMate;
    }

    public void setNoMate(final boolean noMate) {
        this.noMate = noMate;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(final boolean timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "PrincipalVariationDetails{" +
                "timeout=" + timeout +
                ", noMate=" + noMate +
                ", forcedMate=" + forcedMate +
                ", numMovesBeforeMate=" + numMovesBeforeMate +
                ", evaluationCP=" + evaluationCP +
                ", depth=" + depth +
                ", seldepth=" + seldepth +
                ", nodes=" + nodes +
                ", principalVariation='" + principalVariation + '\'' +
                '}';
    }
}
