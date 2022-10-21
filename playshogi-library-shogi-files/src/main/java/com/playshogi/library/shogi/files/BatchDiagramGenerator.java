package com.playshogi.library.shogi.files;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.svg.SVGConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class BatchDiagramGenerator {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Oneye\\Documents\\SFEN for book\\sfen.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int counter = 0;
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                System.out.println(line);
                try {
                    ShogiPosition shogiPosition = SfenConverter.fromSFEN(line);
                    String svg = SVGConverter.toSVG(shogiPosition);
                    System.out.println(svg);
                    counter++;
                    writeToFile(svg, counter);
                }catch (Exception ignored){}
            }
        }
    }

    private static void writeToFile(final String svg, final int counter) throws IOException {
        File file = new File("C:\\Users\\Oneye\\Documents\\SFEN for book\\diagrams\\Diagram"+counter+".svg");

        FileWriter wr = new FileWriter(file);
        wr.write(svg);
        wr.flush();
        wr.close();
    }

}
