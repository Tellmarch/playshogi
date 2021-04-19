package com.playshogi.website.gwt.server;

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

        String returnUsf = request.getParameter("returnUsf");
        String collectionId = request.getParameter("collectionId");

        LOGGER.info("doPost {return = " + returnUsf + " - collectionId = " + collectionId + " }");

        final Part filePart = request.getPart("file");
        InputStream inputStream = filePart.getInputStream();

        try (PrintWriter writer = response.getWriter()) {
            try {
                final String fileName = getFileName(filePart);
                if (fileName == null) {
                    throw new IllegalArgumentException("Request did not include a file");
                }

                if ("true".equals(returnUsf)) {
                    readBackKifu(inputStream, writer, fileName);
                } else {
                    importCollection(inputStream, writer, fileName);
                }

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Problems during file upload.", ex);
                writer.println("ERROR: Error uploading the file: " + ex.getMessage());
            } finally {
                Closeables.closeQuietly(inputStream);
            }
        }
    }

    private void readBackKifu(InputStream inputStream, PrintWriter writer, String fileName) throws IOException {
        List<GameRecord> records = readGameRecords(inputStream, fileName);
        for (GameRecord record : records) {
            String usf = UsfFormat.INSTANCE.write(record);
            writer.println("SUCCESS:" + usf);
            LOGGER.log(Level.INFO, "Successfully imported kifu: " + fileName + " " + usf);
        }
    }

    private void importCollection(final InputStream inputStream, final PrintWriter writer, final String fileName) throws IOException {
        List<GameRecord> records = readGameRecords(inputStream, fileName);

        String collectionId = CollectionUploads.INSTANCE.addCollection(new KifuCollection("New Collection", records));

        writer.println("COLLECTION:" + collectionId);
    }

    private List<GameRecord> readGameRecords(final InputStream inputStream, final String fileName) throws IOException {
        LOGGER.log(Level.INFO, "Reading kifus from: " + fileName);
        if (fileName.endsWith(".kif")) {
            return KifFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream)));
        } else if (fileName.endsWith(".psn") || fileName.endsWith(".txt")) {
            return PsnFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream)));
        } else if (fileName.endsWith(".usf")) {
            return UsfFormat.INSTANCE.read(new ScannerLineReader(new Scanner(inputStream)));
        } else if (fileName.endsWith(".zip")) {
            return readGameRecordsFromZip(inputStream);
        } else {
            throw new IllegalArgumentException("Unrecognized file format: " + fileName);
        }
    }

    private List<GameRecord> readGameRecordsFromZip(final InputStream inputStream) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;

        List<GameRecord> records = new ArrayList<>();

        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                List<GameRecord> gameRecords = readGameRecords(zipInputStream, entry.getName());
                for (GameRecord gameRecord : gameRecords) {
                    LOGGER.log(Level.INFO,
                            "Successfully read kifu: " + entry.getName() + " " + UsfFormat.INSTANCE.write(gameRecord));
                    records.add(gameRecord);
                }
            }
        }
        return records;
    }

    private String getFileName(final Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
