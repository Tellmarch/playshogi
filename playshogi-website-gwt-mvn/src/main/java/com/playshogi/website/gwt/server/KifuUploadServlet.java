package com.playshogi.website.gwt.server;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.ScannerLineReader;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.psn.PsnFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig
public class KifuUploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(KifuUploadServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/html;charset=UTF-8");

        final Part filePart = request.getPart("file");

        try (PrintWriter writer = response.getWriter()) {
            try (Scanner scanner = new Scanner(filePart.getInputStream())) {
                final String fileName = getFileName(filePart);
                if (fileName == null) {
                    throw new IllegalArgumentException("Request did not include a file");
                }

                GameRecord record = readGameRecord(scanner, fileName);
                String message = "Successfully imported kifu: " + fileName + " " + UsfFormat.INSTANCE.write(record);
                writer.println(message);
                LOGGER.log(Level.INFO, message);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Problems during file upload.", ex);
                writer.println("Error uploading the file: " + ex.getMessage());
            }
        }
    }

    private String getFileName(final Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private GameRecord readGameRecord(final Scanner scanner, final String fileName) {
        if (fileName.endsWith(".kif")) {
            return KifFormat.INSTANCE.read(new ScannerLineReader(scanner));
        } else if (fileName.endsWith(".psn")) {
            return PsnFormat.INSTANCE.read(new ScannerLineReader(scanner));
        } else if (fileName.endsWith(".usf")) {
            return UsfFormat.INSTANCE.read(new ScannerLineReader(scanner));
        } else {
            throw new IllegalArgumentException("Unrecognized file format: " + fileName);
        }
    }

}
