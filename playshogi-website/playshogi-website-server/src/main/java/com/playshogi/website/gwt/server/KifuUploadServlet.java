package com.playshogi.website.gwt.server;

import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.io.Closeables;
import com.playshogi.library.shogi.files.ScannerLineReader;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.psn.PsnFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.KifuCollection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 2)
public class KifuUploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(KifuUploadServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/html;charset=UTF-8");

        String charset = request.getParameter("charset");
        if (Strings.isNullOrEmpty(charset)) {
            charset = StandardCharsets.UTF_8.name();
        }
        String collectionId = request.getParameter("collectionId");
        boolean returnUsf = "true".equalsIgnoreCase(request.getParameter("returnUsf"));
        boolean returnSummary = "true".equalsIgnoreCase(request.getParameter("returnSummary"));

        LOGGER.info("doPost {return = " + returnUsf + " - collectionId = " + collectionId + " }");

        for (Part part : request.getParts()) {
            String fileName = part.getSubmittedFileName();
            if (fileName != null) {
                LOGGER.info("Processing file " + fileName);
                processFilePart(response, returnUsf, part, returnSummary, charset, collectionId);
            }
        }
    }

    private void processFilePart(final HttpServletResponse response, final boolean returnUsf, final Part filePart,
                                 final boolean returnSummary, final String charset, final String collectionId) throws IOException {
        InputStream inputStream = filePart.getInputStream();

        try (PrintWriter writer = response.getWriter()) {
            try {
                final String fileName = filePart.getSubmittedFileName();
                if (fileName == null) {
                    throw new IllegalArgumentException("Request did not include a file");
                }

                if (returnUsf) {
                    readBackKifu(inputStream, writer, fileName, charset);
                } else {
                    importCollection(inputStream, writer, fileName, returnSummary, charset, collectionId);
                }

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Problems during file upload.", ex);
                writer.println("ERROR: Error uploading the file: " + ex.getMessage());
            } finally {
                Closeables.closeQuietly(inputStream);
            }
        }
    }

    private void readBackKifu(InputStream inputStream, PrintWriter writer, String fileName, final String charset) throws IOException {
        List<GameRecord> records = readGameRecords(inputStream, fileName, charset);
        for (GameRecord record : records) {
            String usf = UsfFormat.INSTANCE.write(record);
            writer.println("SUCCESS:" + usf);
            LOGGER.log(Level.INFO, "Successfully read kifu: " + fileName + " " + usf);
        }
    }

    private void importCollection(final InputStream inputStream, final PrintWriter writer, final String fileName,
                                  final boolean returnDetails, final String charset, final String collectionId) throws IOException {
        List<GameRecord> records = readGameRecords(inputStream, fileName, charset);

        String actualId;
        if (Strings.isNullOrEmpty(collectionId) || "new".equals(collectionId)) {
            actualId = CollectionUploads.INSTANCE.addCollection(new KifuCollection("New Collection", records));
        } else {
            actualId = collectionId;
            CollectionUploads.INSTANCE.addOrMergeCollection(actualId, new KifuCollection("New Collection", records));
        }

        writer.println("COLLECTION:" + actualId);
        if (returnDetails) {
            KifuCollection collection = CollectionUploads.INSTANCE.getCollection(actualId);
            for (GameRecord kifus : collection.getKifus()) {
                writer.println("KIFU:" + kifus.getGameInformation().getSummaryString());
            }

        }
    }

    private List<GameRecord> readGameRecords(final InputStream inputStream, final String fileName,
                                             final String charset) throws IOException {
        LOGGER.log(Level.INFO, "Reading kifus from: " + fileName);
        if (fileName.endsWith(".kif")) {
            return KifFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream, charset)));
        } else if (fileName.endsWith(".psn") || fileName.endsWith(".txt")) {
            return PsnFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream, charset)));
        } else if (fileName.endsWith(".usf")) {
            return UsfFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream, charset)));
        } else if (fileName.endsWith(".zip")) {
            return readGameRecordsFromZip(inputStream, charset);
        } else {
            throw new IllegalArgumentException("Unrecognized file format: " + fileName);
        }
    }

    private List<GameRecord> readGameRecordsFromZip(final InputStream inputStream, final String charset) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;

        List<GameRecord> records = new ArrayList<>();

        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                List<GameRecord> gameRecords = readGameRecords(zipInputStream, entry.getName(), charset);
                for (GameRecord gameRecord : gameRecords) {
                    LOGGER.log(Level.INFO,
                            "Successfully read kifu: " + entry.getName() + " " + UsfFormat.INSTANCE.write(gameRecord));
                    records.add(gameRecord);
                }
            }
        }
        return records;
    }
}
