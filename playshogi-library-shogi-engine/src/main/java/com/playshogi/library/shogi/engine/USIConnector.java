package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usi.UsiMoveConverter;
import com.playshogi.library.shogi.models.position.PositionScore;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class USIConnector {

    private static final Logger LOGGER = Logger.getLogger(USIConnector.class.getName());

    private final EngineConfiguration engineConfiguration;

    private UsiLineReader input;
    private PrintWriter output;
    private boolean connected = false;
    private Process process;

    public USIConnector(final EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
    }

    public boolean connect() {
        LOGGER.log(Level.INFO, "Connecting to the engine: " + engineConfiguration);
        if (connected) {
            disconnect();
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(engineConfiguration.getCommand());
            processBuilder.directory(engineConfiguration.getPath());
            process = processBuilder.start();

            input = new UsiLineReader(new Scanner(new InputStreamReader(process.getInputStream())));
            output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));

            sendCommand("usi");
            readUntil("usiok");

            for (String option : engineConfiguration.getOptions()) {
                sendCommand(option);
            }

            sendCommand("isready");
            readUntil("readyok");
            sendCommand("usinewgame");
            connected = true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to the engine", ex);
            connected = false;
        }
        return connected;
    }

    public void disconnect() {
        if (!connected) {
            return;
        }
        try {
            sendCommand("quit");
        } catch (Exception ignored) {
        }
        if (process != null && process.isAlive()) {
            process.destroy();
            process = null;
        }
        doClose(input);
        doClose(output);
        connected = false;
    }

    public interface AnalysisCallback {
        void processPositionEvaluation(PositionEvaluation evaluation);
    }

    public void analyzeKifu(final GameTree gameTree, final int timeMs, final AnalysisCallback callback) {
        if (!connected) {
            throw new IllegalStateException("Engine is not connected");
        }

        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

        ShogiPosition position = gameNavigation.getPosition();

        callback.processPositionEvaluation(analysePosition(SfenConverter.toSFEN(position), timeMs));

        while (!gameNavigation.isEndOfVariation()) {
            gameNavigation.moveForward();
            position = gameNavigation.getPosition();
            callback.processPositionEvaluation(analysePosition(SfenConverter.toSFEN(position), timeMs));
        }
    }

    public List<PositionEvaluation> analyzeKifu(final GameTree gameTree, final int timeMs) {
        ArrayList<PositionEvaluation> evaluations = new ArrayList<>();
        analyzeKifu(gameTree, timeMs, evaluations::add);
        return evaluations;
    }

    public PositionEvaluation analyseTsume(final String sfen) {
        if (!connected) {
            throw new IllegalStateException("Engine is not connected");
        }

        LOGGER.log(Level.INFO, "Looking for mate: " + sfen);

        sendCommand("position sfen " + sfen + " 0");
        sendCommand("go mate 2000");

        return readTsumeResult(sfen);
    }

    private PositionEvaluation readTsumeResult(final String sfen) {
        String nextLine;
        do {
            nextLine = input.nextLine();
            System.out.println("<< " + nextLine);
        } while (!nextLine.startsWith("checkmate"));

        String[] split = nextLine.split(" ");

        if (split[1].equals("nomate") || split[1].equals("timeout")) {
            return new PositionEvaluation(sfen, Collections.emptyList(), null, null);
        } else {
            Player player = SfenConverter.extractPlayer(sfen);
            StringBuilder variation = new StringBuilder();
            for (int i = 1; i < split.length; i++) {
                variation.append(UsiMoveConverter.fromPsnToUsfSTring(split[i], player)).append(" ");
                player = player.opposite();
            }
            int numMoves = split.length - 1;
            Variation principalVariation = new Variation();
            principalVariation.setScore(PositionScore.mateIn(numMoves));
            principalVariation.setUsf(variation.toString());
            String bestMove = UsiMoveConverter.fromPsnToUsfSTring(split[1], sfen);
            return new PositionEvaluation(sfen, Collections.singletonList(new MultiVariations(principalVariation)),
                    bestMove, null);
        }
    }


    public PositionEvaluation analysePosition(final String sfen, final int timeMs) {
        if (!connected) {
            throw new IllegalStateException("Engine is not connected");
        }

        LOGGER.log(Level.INFO, "Evaluation position: " + sfen);

        sendCommand("position sfen " + sfen + " 0");
        sendCommand("go btime 0 wtime 0 byoyomi " + timeMs);

        return UsiEvaluationReader.readEvaluation(input, sfen, engineConfiguration.getMultiPV());
    }


    private void sendCommand(String command) {
        System.out.println(">> " + command);
        output.println(command);
        output.flush();
    }

    private void readUntil(String string) {
        String nextLine;
        do {
            nextLine = input.nextLine();
            System.out.println("<< " + nextLine);
        } while (!string.equals(nextLine));
    }

    private static void doClose(Closeable input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
