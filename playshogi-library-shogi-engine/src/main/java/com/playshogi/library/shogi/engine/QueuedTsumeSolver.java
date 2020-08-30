package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class QueuedTsumeSolver {

    private final EngineConfiguration engineConfiguration;
    private final USIConnector usiConnector;
    private volatile boolean shutdown = false;

    public QueuedTsumeSolver(final EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
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

        return usiConnector.analyseTsume(sfen);
    }

    public void shutDown() {
        shutdown = true;
        usiConnector.disconnect();
    }
}
