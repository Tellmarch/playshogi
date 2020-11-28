package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QueuedTsumeSolver {

    private static final Logger LOGGER = Logger.getLogger(QueuedTsumeSolver.class.getName());

    private final USIConnector usiConnector;
    private volatile boolean shutdown = false;

    public QueuedTsumeSolver(final EngineConfiguration engineConfiguration) {
        usiConnector = new USIConnector(engineConfiguration);
    }

    public synchronized PositionEvaluation analyseTsume(final ShogiPosition position) {
        return analyseTsume(SfenConverter.toSFEN(position));
    }

    public synchronized PositionEvaluation analyseTsume(final String sfen) {
        if (shutdown) {
            throw new IllegalStateException("QueuedTsumeSolver is shutdown");
        }

        if (!usiConnector.isConnected()) {
            usiConnector.connect();
        }

        try {
            return usiConnector.analyseTsume(sfen);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting computer tsume evaluation", ex);
            usiConnector.disconnect();
            throw new IllegalStateException("Could not get computer tsume move in position " + sfen);
        }
    }

    public synchronized void shutDown() {
        shutdown = true;
        usiConnector.disconnect();
    }
}
