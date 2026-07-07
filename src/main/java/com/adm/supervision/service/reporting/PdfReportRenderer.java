package com.adm.supervision.service.reporting;

import java.awt.Color;
import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * Renders a {@link ReportDocument} as a styled, paginated PDF: a title band, a summary
 * card and a bordered data table with a dark header row and alternating row shading,
 * with the header and footer repeated on every page.
 */
final class PdfReportRenderer {

    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.LETTER.getHeight();
    private static final float MARGIN = 42f;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;
    private static final float TOP_Y = PAGE_HEIGHT - MARGIN;
    private static final float MIN_CONTENT_Y = 64f;
    private static final float HEADER_ROW_HEIGHT = 22f;
    private static final float TABLE_ROW_HEIGHT = 18f;
    private static final float CELL_PADDING = 6f;
    private static final float SUMMARY_ROW_HEIGHT = 15f;
    private static final float SUMMARY_PADDING = 10f;

    private static final Color COLOR_NAVY = new Color(11, 31, 58);
    private static final Color COLOR_ACCENT = new Color(37, 99, 235);
    private static final Color COLOR_TEXT = new Color(15, 23, 42);
    private static final Color COLOR_TEXT_SOFT = new Color(100, 116, 139);
    private static final Color COLOR_ROW_ALT = new Color(240, 245, 255);
    private static final Color COLOR_BORDER = new Color(226, 232, 240);
    private static final Color COLOR_SUMMARY_BG = new Color(248, 250, 252);

    private static final DateTimeFormatter GENERATION_STAMP = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.FRENCH);

    private final PDDocument document;
    private final ReportDocument content;
    private final PDFont regularFont;
    private final PDFont boldFont;
    private final PDFont italicFont;

    private PDPageContentStream stream;
    private float cursorY;
    private int pageNumber;

    PdfReportRenderer(PDDocument document, ReportDocument content) {
        this.document = document;
        this.content = content;
        this.regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        this.boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        this.italicFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
    }

    void render() throws IOException {
        startNewPage();
        drawTitleBand();
        drawSummaryCard();
        drawTable();
        stream.close();
    }

    private void startNewPage() throws IOException {
        if (stream != null) {
            stream.close();
        }
        pageNumber++;
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        stream = new PDPageContentStream(document, page);
        cursorY = TOP_Y;
        drawFooter();
    }

    private void drawFooter() throws IOException {
        stream.setStrokingColor(COLOR_BORDER);
        stream.setLineWidth(0.75f);
        stream.moveTo(MARGIN, MARGIN);
        stream.lineTo(PAGE_WIDTH - MARGIN, MARGIN);
        stream.stroke();

        String left = "ADM Supervision - " + content.title();
        String right = "Page " + pageNumber;
        drawText(regularFont, 7.5f, COLOR_TEXT_SOFT, MARGIN, MARGIN - 14, left);
        float rightWidth = textWidth(regularFont, right, 7.5f);
        drawText(regularFont, 7.5f, COLOR_TEXT_SOFT, PAGE_WIDTH - MARGIN - rightWidth, MARGIN - 14, right);
    }

    private void drawTitleBand() throws IOException {
        drawText(boldFont, 18f, COLOR_NAVY, MARGIN, cursorY - 18, content.title());
        cursorY -= 28f;

        stream.setStrokingColor(COLOR_ACCENT);
        stream.setLineWidth(2f);
        stream.moveTo(MARGIN, cursorY);
        stream.lineTo(MARGIN + 90f, cursorY);
        stream.stroke();
        cursorY -= 16f;

        String meta = "Genere le " + GENERATION_STAMP.format(Instant.now().atZone(ZoneId.systemDefault()));
        drawText(regularFont, 8.5f, COLOR_TEXT_SOFT, MARGIN, cursorY - 8.5f, meta);
        cursorY -= 22f;
    }

    private void drawSummaryCard() throws IOException {
        List<String[]> items = new ArrayList<>();
        for (String line : content.summaryLines()) {
            int separator = line.indexOf(": ");
            if (separator > -1) {
                items.add(new String[] { line.substring(0, separator), line.substring(separator + 2) });
            } else {
                items.add(new String[] { "", line });
            }
        }
        if (items.isEmpty()) {
            return;
        }

        int columns = 2;
        int rows = (items.size() + columns - 1) / columns;
        float boxHeight = rows * SUMMARY_ROW_HEIGHT + 2 * SUMMARY_PADDING;
        ensureSpace(boxHeight + 18f);

        float boxTop = cursorY;
        stream.setNonStrokingColor(COLOR_SUMMARY_BG);
        stream.addRect(MARGIN, boxTop - boxHeight, CONTENT_WIDTH, boxHeight);
        stream.fill();
        stream.setStrokingColor(COLOR_BORDER);
        stream.setLineWidth(0.75f);
        stream.addRect(MARGIN, boxTop - boxHeight, CONTENT_WIDTH, boxHeight);
        stream.stroke();

        float columnWidth = CONTENT_WIDTH / columns;
        for (int i = 0; i < items.size(); i++) {
            int column = i % columns;
            int row = i / columns;
            float baseline = boxTop - SUMMARY_PADDING - (row + 1) * SUMMARY_ROW_HEIGHT + 4f;
            float x = MARGIN + SUMMARY_PADDING + column * columnWidth;
            String label = items.get(i)[0];
            String value = items.get(i)[1];
            if (!label.isEmpty()) {
                String labelText = label.toUpperCase(Locale.FRENCH) + " : ";
                drawText(boldFont, 7.5f, COLOR_TEXT_SOFT, x, baseline, labelText);
                x += textWidth(boldFont, labelText, 7.5f);
            }
            drawText(regularFont, 9f, COLOR_TEXT, x, baseline, value);
        }

        cursorY = boxTop - boxHeight - 18f;
    }

    private void drawTable() throws IOException {
        List<String> headers = content.headers();
        List<List<String>> rows = content.rows();
        if (headers.isEmpty()) {
            return;
        }

        float[] widths = computeColumnWidths(headers, rows);
        ensureSpace(HEADER_ROW_HEIGHT + TABLE_ROW_HEIGHT);
        drawTableHeader(headers, widths);

        if (rows.isEmpty()) {
            drawText(
                italicFont,
                9f,
                COLOR_TEXT_SOFT,
                MARGIN + CELL_PADDING,
                cursorY - TABLE_ROW_HEIGHT + 5f,
                "Aucune ligne de donnees pour ce rapport."
            );
            cursorY -= TABLE_ROW_HEIGHT;
            return;
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            if (cursorY - TABLE_ROW_HEIGHT < MIN_CONTENT_Y) {
                startNewPage();
                drawTableHeader(headers, widths);
            }
            drawTableRow(rows.get(rowIndex), widths, rowIndex % 2 == 0);
        }
    }

    private void drawTableHeader(List<String> headers, float[] widths) throws IOException {
        stream.setNonStrokingColor(COLOR_NAVY);
        stream.addRect(MARGIN, cursorY - HEADER_ROW_HEIGHT, CONTENT_WIDTH, HEADER_ROW_HEIGHT);
        stream.fill();

        float x = MARGIN;
        float baseline = cursorY - HEADER_ROW_HEIGHT + (HEADER_ROW_HEIGHT - 9f) / 2f + 1f;
        for (int i = 0; i < headers.size(); i++) {
            String label = truncateToWidth(boldFont, headers.get(i), 9f, widths[i] - 2 * CELL_PADDING);
            drawText(boldFont, 9f, Color.WHITE, x + CELL_PADDING, baseline, label);
            x += widths[i];
        }
        cursorY -= HEADER_ROW_HEIGHT;
    }

    private void drawTableRow(List<String> row, float[] widths, boolean alternate) throws IOException {
        if (alternate) {
            stream.setNonStrokingColor(COLOR_ROW_ALT);
            stream.addRect(MARGIN, cursorY - TABLE_ROW_HEIGHT, CONTENT_WIDTH, TABLE_ROW_HEIGHT);
            stream.fill();
        }

        float x = MARGIN;
        float baseline = cursorY - TABLE_ROW_HEIGHT + (TABLE_ROW_HEIGHT - 8f) / 2f + 1f;
        for (int i = 0; i < widths.length; i++) {
            String value = i < row.size() ? row.get(i) : "";
            String truncated = truncateToWidth(regularFont, value, 8f, widths[i] - 2 * CELL_PADDING);
            drawText(regularFont, 8f, COLOR_TEXT, x + CELL_PADDING, baseline, truncated);
            x += widths[i];
        }

        stream.setStrokingColor(COLOR_BORDER);
        stream.setLineWidth(0.5f);
        stream.moveTo(MARGIN, cursorY - TABLE_ROW_HEIGHT);
        stream.lineTo(MARGIN + CONTENT_WIDTH, cursorY - TABLE_ROW_HEIGHT);
        stream.stroke();

        cursorY -= TABLE_ROW_HEIGHT;
    }

    private float[] computeColumnWidths(List<String> headers, List<List<String>> rows) throws IOException {
        float[] natural = new float[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            natural[i] = textWidth(boldFont, headers.get(i), 9f) + 2 * CELL_PADDING;
        }
        for (List<String> row : rows) {
            for (int i = 0; i < headers.size() && i < row.size(); i++) {
                float width = textWidth(regularFont, row.get(i), 8f) + 2 * CELL_PADDING;
                if (width > natural[i]) {
                    natural[i] = width;
                }
            }
        }

        float total = 0f;
        for (float value : natural) {
            total += value;
        }
        if (total <= 0f) {
            return natural;
        }

        float scale = CONTENT_WIDTH / total;
        float[] scaled = new float[natural.length];
        for (int i = 0; i < natural.length; i++) {
            scaled[i] = natural[i] * scale;
        }
        return scaled;
    }

    private void ensureSpace(float needed) throws IOException {
        if (cursorY - needed < MIN_CONTENT_Y) {
            startNewPage();
        }
    }

    private void drawText(PDFont font, float size, Color color, float x, float y, String text) throws IOException {
        stream.beginText();
        stream.setFont(font, size);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, y);
        try {
            stream.showText(text);
        } catch (IllegalArgumentException unmappableCharacter) {
            stream.showText(toAscii(text));
        }
        stream.endText();
    }

    private String truncateToWidth(PDFont font, String text, float size, float maxWidth) throws IOException {
        if (maxWidth <= 0) {
            return "";
        }
        String value = text == null ? "" : text;
        if (textWidth(font, value, size) <= maxWidth) {
            return value;
        }

        String ellipsis = "...";
        float ellipsisWidth = textWidth(font, ellipsis, size);
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            String candidate = truncated.toString() + value.charAt(i);
            if (textWidth(font, candidate, size) + ellipsisWidth > maxWidth) {
                break;
            }
            truncated.append(value.charAt(i));
        }
        return truncated + ellipsis;
    }

    private float textWidth(PDFont font, String text, float size) throws IOException {
        try {
            return (font.getStringWidth(text) / 1000f) * size;
        } catch (IllegalArgumentException unmappableCharacter) {
            return (font.getStringWidth(toAscii(text)) / 1000f) * size;
        }
    }

    private static String toAscii(String input) {
        String normalized = Normalizer.normalize(input == null ? "" : input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^\\x20-\\x7E]", "?");
    }
}
