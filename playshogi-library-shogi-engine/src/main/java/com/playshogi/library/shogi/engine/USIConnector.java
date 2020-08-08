package com.playshogi.library.shogi.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class USIConnector {

    private static final Logger LOGGER = Logger.getLogger(USIConnector.class.getName());


    private static final String ENGINE_COMMAND = "./YaneuraOu-by-gcc";
    private static final File ENGINE_PATH = new File("/home/jean/shogi/engines/YaneuraOu/source/");

    public PositionEvaluation analysePosition(String sfen) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(ENGINE_COMMAND);
            processBuilder.directory(ENGINE_PATH);
            Process process = processBuilder.start();

            Scanner input = new Scanner(new InputStreamReader(process.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));

            sendCommand(output, "usi");
            readUntil(input, "usiok");
            sendCommand(output, "isready");
            readUntil(input, "readyok");
            sendCommand(output, "usinewgame");
            sendCommand(output, "position sfen " + sfen + " 0");
            sendCommand(output, "go btime 0 wtime 0 byoyomi 5000");

            PositionEvaluation evaluation = readEvaluation(input);

            sendCommand(output, "quit");
            return evaluation;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error evaluating the position " + sfen, ex);
            return null;
        }
    }

    private PositionEvaluation readEvaluation(Scanner input) {
        List<PrincipalVariation> principalVariationHistory = new ArrayList<>();

        String nextLine = "";
        do {
            nextLine = input.nextLine();

            principalVariationHistory.add(parsePrincipalVariation(nextLine));

            System.out.println("<< " + nextLine);
        } while (!nextLine.startsWith("bestmove"));

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

        return null;
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

    public static void main(String[] args) {
        System.out.println(new USIConnector().analysePosition("ln1g5/1ks2gs1l/1pp4p1/p2bpn2p/3p3P1/P1P1P1P1P/1P1P1PS2" +
                "/2KGGS1R1/LN6L b RNPbp"));
    }
}
