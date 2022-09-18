package com.playshogi.library.shogi.models.formats.svg;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.kif.KifUtils;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.Square;

// import java.io.BufferedWriter;
// import java.io.FileWriter;
// import java.io.IOException;
import java.util.Optional;

public class SVGConverter {

    private static final int MARGIN_LEFT = 70;
    private static final int SQUARE_WIDTH = 44;
    private static final int MARGIN_TOP = 10;
    private static final int SQUARE_HEIGHT = 48;
    private static final int THIN_LINE_WIDTH = 1;
    private static final int THICK_LINE_WIDTH = 3;

    public static String toSVG(final ReadOnlyShogiPosition position) {
        StringBuffer svg = new StringBuffer();

        svg.append("<svg viewBox=\"0 0 600 500\" width=\"600\" height=\"500\" version=\"1.1\" xmlns=\"http://www.w3" +
                ".org/2000/svg\">");
        svg.append("<desc>Japanese Chess - Shogi Board - Diagram from PlayShogi.com</desc>");
        svg.append("<desc id=\"sfen\">" + SfenConverter.toSFEN(position) + "</desc>");

        appendStyles(svg);

        svg.append("\n<g id=\"all\">\n");

        appendBoard(svg);
        appendPieces(position, svg);

        appendBlackPiecesInHand(position, svg);
        appendWhitePiecesInHand(position, svg);

        svg.append("</g>\n"); // all


        svg.append("</svg>");

        return svg.toString();
    }


    private static void appendStyles(final StringBuffer svg) {
        svg.append("<style>");
        svg.append(".white-piece {\n" +
                "\t\tfont-family:-apple-system, BlinkMacSystemFont, \"Helvetica Neue\", \"Segoe UI\",\"Noto Sans " +
                "Japanese\",\"ヒラギノ角ゴ ProN W3\", Meiryo, sans-serif;\n" +
                "\t\tfont-size: 38px;\n" +
                "\t\tfill: #000;\n" +
                "\t\ttext-anchor: middle;\n" +
                "\t}\n");
        svg.append(".black-piece {\n" +
                "\t\tfont-family:-apple-system, BlinkMacSystemFont, \"Helvetica Neue\", \"Segoe UI\",\"Noto Sans " +
                "Japanese\",\"ヒラギノ角ゴ ProN W3\", Meiryo, sans-serif;\n" +
                "\t\tfont-size: 38px;\n" +
                "\t\tfill: #000;\n" +
                "\t\ttext-anchor: middle;\n" +
                "\t}\n");
        svg.append(".hand {\n" +
                "\t\tfont-family:-apple-system, BlinkMacSystemFont, \"Helvetica Neue\", \"Segoe UI\",\"Noto Sans " +
                "Japanese\",\"ヒラギノ角ゴ ProN W3\", Meiryo, sans-serif;\n" +
                "\t\tfont-size: 30px;\n" +
                "\t\tfill: #000;\n" +
                "\t}\n");
        svg.append("</style>");
    }

    private static void appendBoard(final StringBuffer svg) {
        svg.append("<g id=\"board\" fill=\"none\" stroke=\"#000\">\n");


        int boardWidth = 9 * SQUARE_WIDTH;
        int boardHeight = 9 * SQUARE_HEIGHT;

        int top = MARGIN_TOP;
        int left = MARGIN_LEFT;

        for (int c = 1; c <= 8; c++) {
            int x = MARGIN_LEFT + (SQUARE_WIDTH) * c;
            svg.append("<path d=\"m" + x + " " + top + "v" + boardHeight + "\"></path>");
        }

        for (int r = 1; r <= 8; r++) {
            int y = MARGIN_TOP + (SQUARE_HEIGHT) * r;
            svg.append("<path d=\"m" + left + " " + y + "h" + boardWidth + "\"></path>");
        }


        svg.append("<rect xmlns=\"http://www.w3.org/2000/svg\" x=\"" + MARGIN_LEFT + "\" y=\"" + MARGIN_TOP + "\" " +
                "width=\"" + boardWidth + "\" height=\"" + boardHeight + "\" " +
                "stroke-width=\"" + THICK_LINE_WIDTH + "\"/>\n");

        svg.append("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"" + (MARGIN_LEFT + (SQUARE_WIDTH) * 3) + "\" " +
                "cy=\"" + (MARGIN_TOP + (SQUARE_HEIGHT) * 3) + "\" r=\"3\" fill=\"#000\"/>");
        svg.append("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"" + (MARGIN_LEFT + (SQUARE_WIDTH) * 6) + "\" " +
                "cy=\"" + (MARGIN_TOP + (SQUARE_HEIGHT) * 3) + "\" r=\"3\" fill=\"#000\"/>");
        svg.append("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"" + (MARGIN_LEFT + (SQUARE_WIDTH) * 3) + "\" " +
                "cy=\"" + (MARGIN_TOP + (SQUARE_HEIGHT) * 6) + "\" r=\"3\" fill=\"#000\"/>");
        svg.append("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"" + (MARGIN_LEFT + (SQUARE_WIDTH) * 6) + "\" " +
                "cy=\"" + (MARGIN_TOP + (SQUARE_HEIGHT) * 6) + "\" r=\"3\" fill=\"#000\"/>");


        svg.append("</g>\n"); // board
    }

    private static void appendPieces(final ReadOnlyShogiPosition position, final StringBuffer svg) {
        svg.append("<g id=\"pieces\">\n");

        for (Square square : position.getAllSquares()) {
            Optional<Piece> piece = position.getPieceAt(square);
            if (piece.isPresent()) {
                int x = MARGIN_LEFT + SQUARE_WIDTH * (9 - square.getColumn());
                int y = MARGIN_TOP + SQUARE_HEIGHT * square.getRow();
                String whitePieceClass = "\"white-piece\" transform=\"rotate(180," + x + "," + y + ")\"";
                String blackPieceClass = "\"black-piece\"";
                String pieceClass = piece.get().isBlackPiece() ? blackPieceClass : whitePieceClass;
                double dx = piece.get().isBlackPiece() ? 22 : 22 - SQUARE_WIDTH;
                double dy = piece.get().isBlackPiece() ? -9 : -9 + SQUARE_HEIGHT;

                svg.append("<text x=\"")
                        .append(x)
                        .append("\" y=\"")
                        .append(y)
                        .append("\" dy=\"" + dy + "\" dx=\"" + dx + "\" class=")
                        .append(pieceClass)
                        .append(">")
                        .append(KifUtils.getOneCharJapanesePieceSymbol(piece.get()))
                        .append("</text>\n");
            }
        }


        svg.append("</g>\n"); // pieces
    }


    private static void appendBlackPiecesInHand(final ReadOnlyShogiPosition position, final StringBuffer svg) {
        svg.append("<g id=\"black-hand\">");

        StringBuilder hand = new StringBuilder("☗");
        for (PieceType pieceType : PieceType.STRONGEST_TO_WEAKEST) {
            int numPieces = position.getSenteKomadai().getPiecesOfType(pieceType);
            if (numPieces == 0) continue;
            hand.append(KifUtils.getJapanesePieceSymbol(pieceType));
            if (numPieces > 1) {
                hand.append(KifUtils.getJapaneseNumberString(numPieces));
            }
        }

        if(hand.length() == 1) {
            hand.append(" なし");
        }

        int x = MARGIN_LEFT + 9 * SQUARE_WIDTH + 10;
        for (int i = 0; i < hand.length(); i++) {
            char c = hand.charAt(i);
            int y = 50 + i * 32;
            svg.append("<text text-anchor=\"start\" x=\"")
                    .append(x)
                    .append("\" y=\"")
                    .append(y)
                    .append("\" class=\"hand\"><tspan>")
                    .append(c)
                    .append("</tspan></text>");
        }

        svg.append("</g>\n"); // black hand
    }


    private static void appendWhitePiecesInHand(final ReadOnlyShogiPosition position, final StringBuffer svg) {
        svg.append("<g id=\"white-hand\" transform=\"scale(-1, -1)\">");

        StringBuilder hand = new StringBuilder("☖");
        for (PieceType pieceType : PieceType.STRONGEST_TO_WEAKEST) {
            int numPieces = position.getGoteKomadai().getPiecesOfType(pieceType);
            if (numPieces == 0) continue;
            hand.append(KifUtils.getJapanesePieceSymbol(pieceType));
            if (numPieces > 1) {
                hand.append(KifUtils.getJapaneseNumberString(numPieces));
            }
        }

        if(hand.length() == 1) {
            hand.append(" なし");
        }

        int x = - (MARGIN_LEFT - 10);
        for (int i = 0; i < hand.length(); i++) {
            char c = hand.charAt(i);
            int y = - (9 * SQUARE_HEIGHT - 30 - i * 32);
            svg.append("<text text-anchor=\"start\" x=\"")
                    .append(x)
                    .append("\" y=\"")
                    .append(y)
                    .append("\" class=\"hand\"><tspan>")
                    .append(c)
                    .append("</tspan></text>");
        }

        svg.append("</g>\n"); // black hand
    }


//     public static void main(String[] args) throws IOException {
//         String svg = SVGConverter.toSVG(SfenConverter.fromSFEN("ln1gk2n1/1rs3g1+L/3pppsp1/p1p3p2/1p5P1/2P6/PPSPPPPs1" +
//                 "/2GK4p/LN3G1NR w 2BLP "));
// //        System.out.println(svg);

//         BufferedWriter writer = new BufferedWriter(new FileWriter("/home/jfortin/test.svg"));
//         writer.write(svg);

//         writer.close();
//     }
}
