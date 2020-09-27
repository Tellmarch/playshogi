package com.playshogi.library.shogi.engine;

import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class USIConnector {

    private static final Logger LOGGER = Logger.getLogger(USIConnector.class.getName());

    private final EngineConfiguration engineConfiguration;

    private Scanner input;
    private PrintWriter output;
    private boolean connected = false;
    private Process process;

    public USIConnector(EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
    }

    public boolean connect() {
        if (connected) {
            disconnect();
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(engineConfiguration.getCommand());
            processBuilder.directory(engineConfiguration.getPath());
            process = processBuilder.start();

            input = new Scanner(new InputStreamReader(process.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));

            sendCommand(output, "usi");
            readUntil(input, "usiok");
            sendCommand(output, "isready");
            readUntil(input, "readyok");
            sendCommand(output, "usinewgame");
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
            sendCommand(output, "quit");
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

        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameTree, ShogiInitialPositionFactory.createInitialPosition());

        ShogiPosition position = gameNavigation.getPosition();

        callback.processPositionEvaluation(analysePosition(SfenConverter.toSFEN(position), timeMs));

        while (!gameNavigation.isEndOfVariation()) {
            gameNavigation.moveForward();
            position = gameNavigation.getPosition();
            callback.processPositionEvaluation(analysePosition(SfenConverter.toSFEN(position), timeMs));
        }
    }

    public PositionEvaluation analyseTsume(String sfen) {
        if (!connected) {
            throw new IllegalStateException("Engine is not connected");
        }

        LOGGER.log(Level.INFO, "Looking for mate: " + sfen);

        sendCommand(output, "position sfen " + sfen + " 0");
        sendCommand(output, "go mate 2000");

        return readTsumeResult(input);
    }

    private PositionEvaluation readTsumeResult(Scanner input) {
        List<PrincipalVariation> principalVariationHistory = new ArrayList<>();

        String nextLine;
        do {
            nextLine = input.nextLine();
            System.out.println("<< " + nextLine);
        } while (!nextLine.startsWith("checkmate"));

        String[] split = nextLine.split(" ");

        if (split[1].equals("nomate") || split[1].equals("timeout")) {
            return new PositionEvaluation(new PrincipalVariation[]{}, null, null);
        } else {
            StringBuilder variation = new StringBuilder();
            for (int i = 1; i < split.length; i++) {
                variation.append(split[i]).append(" ");
            }
            int numMoves = split.length - 1;
            PrincipalVariation principalVariation = new PrincipalVariation();
            principalVariation.setForcedMate(true);
            principalVariation.setNumMovesBeforeMate(numMoves);
            principalVariation.setPrincipalVariation(variation.toString());
            return new PositionEvaluation(new PrincipalVariation[]{principalVariation}, split[1], null);
        }
    }


    public PositionEvaluation analysePosition(final String sfen, final int timeMs) {
        if (!connected) {
            throw new IllegalStateException("Engine is not connected");
        }

        LOGGER.log(Level.INFO, "Evaluation position: " + sfen);

        sendCommand(output, "position sfen " + sfen + " 0");
        sendCommand(output, "go btime 0 wtime 0 byoyomi " + timeMs);

        return readEvaluation(input);
    }

    private PositionEvaluation readEvaluation(Scanner input) {
        List<PrincipalVariation> principalVariationHistory = new ArrayList<>();

        String nextLine;
        do {
            nextLine = input.nextLine();
            System.out.println("<< " + nextLine);

            if (nextLine.startsWith("bestmove")) {
                break;
            }

            principalVariationHistory.add(parsePrincipalVariation(nextLine));
        } while (true);

        String[] split = nextLine.split(" ");
        if (!"bestmove".equals(split[0])) {
            LOGGER.log(Level.SEVERE, "Unexpected bestmove line: " + nextLine);
        }
        String bestMove = split[1];
        String ponderMove = "";
        if (split.length == 4) {
            ponderMove = split[3];
        }

        return new PositionEvaluation(principalVariationHistory.toArray(new PrincipalVariation[0]), bestMove,
                ponderMove);
    }

    private PrincipalVariation parsePrincipalVariation(String line) {
        PrincipalVariation principalVariation = new PrincipalVariation();
        String[] split = line.split(" ");
        for (int i = 0; i < split.length; i++) {
            switch (split[i]) {
                case "info":
                case "lowerbound":
                case "upperbound":
                    continue;
                case "depth":
                    principalVariation.setDepth(Integer.parseInt(split[++i]));
                    continue;
                case "seldepth":
                    principalVariation.setSeldepth(Integer.parseInt(split[++i]));
                    continue;
                case "score":
                    String type = split[++i];
                    if ("cp".equals(type)) {
                        principalVariation.setForcedMate(false);
                        principalVariation.setEvaluationCP(Integer.parseInt(split[++i]));
                    } else if ("mate".equals(type)) {
                        principalVariation.setForcedMate(true);
                        principalVariation.setNumMovesBeforeMate(Integer.parseInt(split[++i]));
                    } else {
                        throw new IllegalArgumentException("Could not parse line: " + line);
                    }
                    continue;
                case "time":
                    principalVariation.setTimeMs(Integer.parseInt(split[++i]));
                    continue;
                case "nodes":
                    principalVariation.setNodes(Long.parseLong(split[++i]));
                    continue;
                case "nps":
                case "hashfull":
                case "multipv":
                case "currmove":
                case "currmovenumber":
                case "cpuload":
                case "string":
                    ++i;
                    continue;
                case "pv":
                    StringBuilder variation = new StringBuilder();
                    for (int j = i + 1; j < split.length; j++) {
                        variation.append(split[j]).append(" ");
                    }
                    principalVariation.setPrincipalVariation(variation.toString());
                    return principalVariation;
                default:
                    throw new IllegalArgumentException("Could not parse line: " + line + " at token " + split[i]);
            }
        }
        return principalVariation;
    }

    private void sendCommand(PrintWriter output, String command) {
        System.out.println(">> " + command);
        output.println(command);
        output.flush();
    }

    private void readUntil(Scanner input, String string) {
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
