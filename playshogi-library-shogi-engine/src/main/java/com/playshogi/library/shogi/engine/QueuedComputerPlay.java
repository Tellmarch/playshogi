package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.usi.UsiMoveConverter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QueuedComputerPlay {

    private static final Logger LOGGER = Logger.getLogger(QueuedComputerPlay.class.getName());

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

        try {
            PositionEvaluation evaluation = usiConnector.analysePosition(sfen, TIME_MS);
            return UsiMoveConverter.fromPsnToUsfSTring(evaluation.getBestMove(), sfen);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting computer evaluation", ex);
            usiConnector.disconnect();
            throw new IllegalStateException("Could not get computer move in position " + sfen);
        }
    }
}
