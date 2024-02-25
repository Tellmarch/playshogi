package com.playshogi.library.shogi.files;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.svg.SVGConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

// generates many diagrams at once from one file into SVGs
public class BatchDiagramGenerator {

    public static void main(String[] args) throws IOException {
        // takes file with many sfens, one in each line
        File file = new File("C:\\Users\\Oneye\\Documents\\SFEN for book\\sfen.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int exportedFileEnumeration = 0; // saves in files with increasing numbers in its name
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                System.out.println(line);
                try {
                    ShogiPosition shogiPosition = SfenConverter.fromSFEN(line); // get position
                    String svg = SVGConverter.toSVG(shogiPosition); // convert into SVG then save it
                    System.out.println(svg);
                    exportedFileEnumeration++;
                    writeToFile(svg, exportedFileEnumeration);
                }catch (Exception ignored){}
            }
        }
    }

    private static void writeToFile(final String svg, final int fileSuffix) throws IOException {
        File file = new File("C:\\Users\\Oneye\\Documents\\SFEN for book\\diagrams\\Diagram"+fileSuffix+".svg");

        FileWriter wr = new FileWriter(file);
        wr.write(svg);
        wr.flush();
        wr.close();
    }

}
