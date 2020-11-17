package com.playshogi.library.shogi.engine;

public class QueuedComputerPlay {

    private final static int TIME_MS = 100;

    private final USIConnector usiConnector;
    private volatile boolean shutdown = false;

    public QueuedComputerPlay(final EngineConfiguration engineConfiguration) {
        usiConnector = new USIConnector(engineConfiguration);
    }

    public synchronized void shutDown() {
        shutdown = true;
        usiConnector.disconnect();
    }

    public synchronized String playMove(final String sfen) {
        if (shutdown) {
            throw new IllegalStateException("QueuedTsumeSolver is shutdown");
        }

        if (!usiConnector.isConnected()) {
            usiConnector.connect();
        }

        PositionEvaluation evaluation = usiConnector.analysePosition(sfen, TIME_MS);

        return evaluation.getBestMove();
    }
}
