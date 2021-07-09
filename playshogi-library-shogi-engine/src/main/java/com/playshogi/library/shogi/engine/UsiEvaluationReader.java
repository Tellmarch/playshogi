package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usi.UsiMoveConverter;
import com.playshogi.library.shogi.models.formats.util.LineReader;
import com.playshogi.library.shogi.models.position.PositionScore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsiEvaluationReader {

    private static final Logger LOGGER = Logger.getLogger(UsiEvaluationReader.class.getName());

    public static PositionEvaluation readEvaluation(final LineReader input, final String sfen, final int multiPV,
                                                    final boolean logOutput) {
        List<MultiVariations> variationsHistory = new ArrayList<>();

        String nextLine = input.nextLine();
        if (logOutput) {
            System.out.println("<< " + nextLine);
        }
        boolean extraLine = false;

        while (!nextLine.startsWith("bestmove")) {

            if (!nextLine.startsWith("info")) {
                throw new IllegalStateException("Unexpected line (expected info): " + nextLine);
            }

            List<Variation> variations = new ArrayList<>(multiPV);

            variations.add(parsePrincipalVariation(nextLine, sfen));

            if (nextLine.contains("multipv 1")) {
                for (int pv = 2; pv <= multiPV; pv++) {
                    nextLine = input.nextLine();
                    if (logOutput) {
                        System.out.println("<< " + nextLine);
                    }
                    if (!nextLine.contains("multipv " + pv)) {
                        extraLine = true;
                        break;
                    }
                    variations.add(parsePrincipalVariation(nextLine, sfen));
                }
            }

            variationsHistory.add(new MultiVariations(variations));

            if (!extraLine) {
                nextLine = input.nextLine();
                if (logOutput) {
                    System.out.println("<< " + nextLine);
                }
            } else {
                extraLine = false;
            }
        }

        String[] split = nextLine.split(" ");
        if (!"bestmove".equals(split[0])) {
            LOGGER.log(Level.SEVERE, "Unexpected bestmove line: " + nextLine);
        }
        String bestMove = UsiMoveConverter.fromUsiToUsfSTring(split[1], sfen);
        String ponderMove = "";
        if (split.length == 4) {
            ponderMove = UsiMoveConverter.fromUsiToUsfSTring(split[3], sfen);
        }

        return new PositionEvaluation(sfen, variationsHistory, bestMove,
                ponderMove);
    }

    public static Variation parsePrincipalVariation(final String line, final String sfen) {
        Variation principalVariation = new Variation();
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
                        principalVariation.setScore(PositionScore.fromScore(Integer.parseInt(split[++i])));
                    } else if ("mate".equals(type)) {
                        principalVariation.setScore(PositionScore.mateIn(Integer.parseInt(split[++i])));
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
                    Player player = SfenConverter.extractPlayer(sfen);
                    StringBuilder variation = new StringBuilder();
                    for (int j = i + 1; j < split.length; j++) {
                        variation.append(UsiMoveConverter.fromUsiToUsfSTring(split[j], player)).append(" ");
                        player = player.opposite();
                    }
                    principalVariation.setUsf(variation.toString());
                    return principalVariation;
                default:
                    throw new IllegalArgumentException("Could not parse line: " + line + " at token " + split[i]);
            }
        }
        return principalVariation;
    }
}
