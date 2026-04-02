package com.studentpdf.service;

import com.studentpdf.model.Question;
import com.studentpdf.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles the actual PDF layout and drawing using Apache PDFBox.
 *
 * <p>Layout:
 * <ul>
 *   <li>Page 1 — Header box (name + enrollment), then questions</li>
 *   <li>Additional pages auto-added as needed</li>
 *   <li>Footer on every page: page number + generated timestamp</li>
 * </ul>
 */
@Service
@Slf4j
public class PdfLayoutService {

    // ── Page geometry ─────────────────────────────────────────────────────────
    private static final PDRectangle PAGE_SIZE   = PDRectangle.A4;
    private static final float PAGE_W            = PAGE_SIZE.getWidth();   // 595
    private static final float PAGE_H            = PAGE_SIZE.getHeight();  // 842
    private static final float MARGIN            = 50f;
    private static final float CONTENT_W         = PAGE_W - 2 * MARGIN;

    // ── Typography ────────────────────────────────────────────────────────────
    private static final float FONT_TITLE        = 18f;
    private static final float FONT_SUBTITLE     = 11f;
    private static final float FONT_SECTION      = 12f;
    private static final float FONT_QUESTION     = 11f;
    private static final float FONT_OPTION       = 10.5f;
    private static final float FONT_FOOTER       = 8f;
    private static final float LINE_HEIGHT_BASE  = 14f;

    // ── Colours (R, G, B in 0-1 range) ───────────────────────────────────────
    private static final Color COL_HEADER_BG  = new Color(0x1F, 0x3A, 0x8F);  // deep blue
    private static final Color COL_HEADER_FG  = Color.WHITE;
    private static final Color COL_ACCENT     = new Color(0x25, 0x63, 0xEB);  // blue
    private static final Color COL_Q_NUM_BG   = new Color(0xEF, 0xF6, 0xFF);  // light blue
    private static final Color COL_TEXT       = new Color(0x1E, 0x29, 0x3B);  // near-black
    private static final Color COL_OPTION     = new Color(0x37, 0x41, 0x51);  // dark grey
    private static final Color COL_DIVIDER    = new Color(0xCB, 0xD5, 0xE1);
    private static final Color COL_FOOTER     = new Color(0x94, 0xA3, 0xB8);

    // ── Fonts (lazily resolved per document) ─────────────────────────────────
    private PDType1Font fontBold;
    private PDType1Font fontRegular;
    private PDType1Font fontItalic;

    // ── State per-document ────────────────────────────────────────────────────
    private PDDocument  doc;
    private PDPage      currentPage;
    private PDPageContentStream cs;
    private float       cursorY;
    private int         pageNumber;
    private String      generatedAt;

    /**
     * Entry point: given a Student, populate the provided PDDocument with all pages.
     */
    public void buildStudentPdf(PDDocument document, Student student) throws IOException {
        this.doc         = document;
        this.pageNumber  = 0;
        this.generatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss"));

        fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        fontItalic  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        newPage();
        drawHeader(student);
        drawQuestions(student);
        closeCurrentStream();
    }

    // ── Page management ───────────────────────────────────────────────────────
    private void newPage() throws IOException {
        if (cs != null) {
            drawFooter();
            cs.close();
        }
        pageNumber++;
        currentPage = new PDPage(PAGE_SIZE);
        doc.addPage(currentPage);
        cs = new PDPageContentStream(doc, currentPage);
        cursorY = PAGE_H - MARGIN;

        // Subtle background tint for even pages
        if (pageNumber % 2 == 0) {
            setFillColor(new Color(0xFA, 0xFC, 0xFF));
            cs.addRect(0, 0, PAGE_W, PAGE_H);
            cs.fill();
        }
    }

    private void closeCurrentStream() throws IOException {
        if (cs != null) {
            drawFooter();
            cs.close();
            cs = null;
        }
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private void drawHeader(Student student) throws IOException {
        float headerH = 90f;

        // Blue background rect
        setFillColor(COL_HEADER_BG);
        cs.addRect(MARGIN, cursorY - headerH, CONTENT_W, headerH);
        cs.fill();

        // Accent stripe (left edge)
        setFillColor(new Color(0x60, 0xA5, 0xFA));
        cs.addRect(MARGIN, cursorY - headerH, 5f, headerH);
        cs.fill();

        // Title text
        float textX   = MARGIN + 18f;
        float textTopY = cursorY - 22f;

        drawText("STUDENT EXAMINATION PAPER", textX, textTopY,
                fontBold, FONT_TITLE, COL_HEADER_FG);

        drawText("Name  :  " + student.getName(),
                textX, textTopY - 22f, fontRegular, FONT_SUBTITLE, COL_HEADER_FG);
        drawText("Enroll:  " + student.getEnrollmentNo(),
                textX, textTopY - 37f, fontRegular, FONT_SUBTITLE, COL_HEADER_FG);
        drawText("Total Questions:  " + student.getQuestions().size(),
                textX, textTopY - 52f, fontRegular, FONT_SUBTITLE, COL_HEADER_FG);

        cursorY -= (headerH + 20f);
    }

    // ── Questions ─────────────────────────────────────────────────────────────
    private void drawQuestions(Student student) throws IOException {
        List<Question> questions = student.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            drawQuestion(i + 1, questions.get(i));
        }
    }

    private void drawQuestion(int displayNum, Question q) throws IOException {
        // Estimate height needed: question text + options + padding
        float estimatedH = estimateQuestionHeight(q);
        if (cursorY - estimatedH < MARGIN + 40f) {
            newPage();
        }

        // Question number bubble
        float bubbleSize = 20f;
        float bubbleX    = MARGIN;
        float bubbleTopY = cursorY - bubbleSize;

        setFillColor(COL_ACCENT);
        cs.addRect(bubbleX, bubbleTopY, bubbleSize, bubbleSize);
        cs.fill();

        String numStr = String.valueOf(displayNum);
        float numW    = fontBold.getStringWidth(numStr) / 1000f * FONT_QUESTION;
        drawText(numStr,
                bubbleX + (bubbleSize - numW) / 2f,
                bubbleTopY + 6f,
                fontBold, FONT_QUESTION, Color.WHITE);

        // Question text (wrapped)
        float qTextX = MARGIN + bubbleSize + 8f;
        float qTextW = CONTENT_W - bubbleSize - 8f;
        List<String> qLines = wrapText(q.getText(), fontBold, FONT_QUESTION, qTextW);

        float lineY = cursorY - 14f;
        for (String line : qLines) {
            drawText(line, qTextX, lineY, fontBold, FONT_QUESTION, COL_TEXT);
            lineY -= LINE_HEIGHT_BASE;
        }

        cursorY = lineY - 4f;

        // Options
        Map<String, String> opts = q.getOptions();
        for (Map.Entry<String, String> entry : opts.entrySet()) {
            String key  = entry.getKey();
            String text = entry.getValue();
            float optX  = MARGIN + 28f;
            float optW  = CONTENT_W - 28f;

            // Option label + text
            String label = "(" + key + ")  " + text;
            List<String> optLines = wrapText(label, fontRegular, FONT_OPTION, optW);

            for (String ol : optLines) {
                if (cursorY < MARGIN + 50f) newPage();
                drawText(ol, optX, cursorY - 11f, fontRegular, FONT_OPTION, COL_OPTION);
                cursorY -= LINE_HEIGHT_BASE - 1f;
            }
        }

        cursorY -= 6f;

        // Thin divider
        if (cursorY > MARGIN + 60f) {
            setStrokeColor(COL_DIVIDER);
            cs.setLineWidth(0.5f);
            cs.moveTo(MARGIN, cursorY);
            cs.lineTo(MARGIN + CONTENT_W, cursorY);
            cs.stroke();
            cursorY -= 10f;
        }
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private void drawFooter() throws IOException {
        float fy = MARGIN - 18f;
        // Left: generated timestamp
        drawText("Generated: " + generatedAt, MARGIN, fy, fontItalic, FONT_FOOTER, COL_FOOTER);
        // Right: page number
        String pg = "Page " + pageNumber;
        float pgW = fontRegular.getStringWidth(pg) / 1000f * FONT_FOOTER;
        drawText(pg, PAGE_W - MARGIN - pgW, fy, fontRegular, FONT_FOOTER, COL_FOOTER);

        // Top line above footer
        setStrokeColor(COL_DIVIDER);
        cs.setLineWidth(0.5f);
        cs.moveTo(MARGIN, MARGIN - 6f);
        cs.lineTo(PAGE_W - MARGIN, MARGIN - 6f);
        cs.stroke();
    }

    // ── Text helpers ──────────────────────────────────────────────────────────
    private void drawText(String text, float x, float y,
                          PDType1Font font, float size, Color color) throws IOException {
        setFillColor(color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(text));
        cs.endText();
    }

    /** Simple greedy word-wrap. */
    private List<String> wrapText(String text, PDType1Font font,
                                   float size, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            float w = font.getStringWidth(test) / 1000f * size;
            if (w > maxWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(test);
            }
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines.isEmpty() ? List.of("") : lines;
    }

    private float estimateQuestionHeight(Question q) throws IOException {
        float h = LINE_HEIGHT_BASE * 3f;  // question lines estimate
        for (String optText : q.getOptions().values()) {
            List<String> lines = wrapText(optText, fontRegular, FONT_OPTION, CONTENT_W - 30f);
            h += lines.size() * (LINE_HEIGHT_BASE - 1f);
        }
        return h + 24f;
    }

    private void setFillColor(Color c) throws IOException {
        cs.setNonStrokingColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    private void setStrokeColor(Color c) throws IOException {
        cs.setStrokingColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    /** Remove characters PDFBox Type1 fonts cannot encode. */
    private String sanitize(String s) {
        if (s == null) return "";
        return s.replaceAll("[^\\x20-\\x7E]", "?");
    }
}
