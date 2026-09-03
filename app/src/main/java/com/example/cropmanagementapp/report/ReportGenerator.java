package com.example.cropmanagementapp.report;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.ActivityLog;
import com.example.cropmanagementapp.model.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds a printable/shareable PDF summarising the whole farm: active
 * crops, harvest history with yields by plot, and total logged expenses.
 * Uses Android's built-in PdfDocument (no external library needed).
 */
public class ReportGenerator {

    private static final int PAGE_WIDTH = 595;  // A4 at 72dpi
    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN = 40;

    private static final Pattern LEADING_NUMBER = Pattern.compile("([0-9]+(\\.[0-9]+)?)");

    private final Context context;
    private final DatabaseHelper dbHelper;

    private PdfDocument pdfDocument;
    private PdfDocument.Page currentPage;
    private Canvas canvas;
    private int pageNumber;
    private float y;

    private Paint titlePaint, headingPaint, subheadingPaint, bodyPaint, mutedPaint, linePaint;

    public ReportGenerator(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(this.context);
        setupPaints();
    }

    private void setupPaints() {
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(0xFF1B5E20);
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        headingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headingPaint.setColor(0xFF1B5E20);
        headingPaint.setTextSize(14);
        headingPaint.setFakeBoldText(true);

        subheadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subheadingPaint.setColor(0xFF1A1A1A);
        subheadingPaint.setTextSize(12);
        subheadingPaint.setFakeBoldText(true);

        bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bodyPaint.setColor(0xFF1A1A1A);
        bodyPaint.setTextSize(11);

        mutedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mutedPaint.setColor(0xFF616161);
        mutedPaint.setTextSize(10);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFFDDDDDD);
        linePaint.setStrokeWidth(1);
    }

    /** Builds the full farm report and saves it under the app's cache/reports folder. Returns the saved File. */
    public File generateFarmReport() throws IOException {
        pdfDocument = new PdfDocument();
        pageNumber = 0;
        currentPage = null;
        startNewPage();

        drawLine("CropManagementApp — Farm Report", titlePaint, 14);
        drawLine("Generated on " + DateUtils.toDisplayFormat(DateUtils.todayIso()), mutedPaint, 16);

        drawSectionDivider();
        drawLine("Active Crops Summary", headingPaint, 10);
        List<Crop> activeCrops = dbHelper.getAllCrops(null);
        if (activeCrops.isEmpty()) {
            drawBody("No active crops currently recorded.");
        } else {
            for (Crop crop : activeCrops) {
                drawCropBlock(crop, false);
            }
        }

        drawSectionDivider();
        drawLine("Harvest Archive & Yield Summary", headingPaint, 10);
        List<Crop> harvestedCrops = dbHelper.getHarvestedCrops(null);
        if (harvestedCrops.isEmpty()) {
            drawBody("No harvested crops recorded yet.");
        } else {
            Map<String, Double> totalsByPlot = new LinkedHashMap<>();
            for (Crop crop : harvestedCrops) {
                double amount = parseLeadingNumber(crop.getYieldAmount());
                totalsByPlot.put(crop.getPlotName(), totalsByPlot.getOrDefault(crop.getPlotName(), 0.0) + amount);
            }
            drawLine("Total Yield by Plot", subheadingPaint, 6);
            for (Map.Entry<String, Double> entry : totalsByPlot.entrySet()) {
                drawBody("  " + entry.getKey() + ":  " + formatNumber(entry.getValue()));
            }
            drawLine("Harvest History", subheadingPaint, 6);
            for (Crop crop : harvestedCrops) {
                drawCropBlock(crop, true);
            }
        }

        drawSectionDivider();
        drawLine("Farm Expenses", headingPaint, 10);
        double totalExpenses = 0;
        for (Crop crop : activeCrops) totalExpenses += dbHelper.getTotalExpensesForCrop(crop.getId());
        for (Crop crop : harvestedCrops) totalExpenses += dbHelper.getTotalExpensesForCrop(crop.getId());
        drawBody("Total logged expenses across all crops: KES " + String.format("%.2f", totalExpenses));

        pdfDocument.finishPage(currentPage);

        File reportsDir = new File(context.getCacheDir(), "reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();
        File file = new File(reportsDir, "Farm_Report_" + DateUtils.todayIso() + ".pdf");
        try (FileOutputStream out = new FileOutputStream(file)) {
            pdfDocument.writeTo(out);
        } finally {
            pdfDocument.close();
        }
        return file;
    }

    private void drawCropBlock(Crop crop, boolean isHarvested) {
        ensureSpace(70);
        String name = crop.getCropName() + (isEmpty(crop.getVariety()) ? "" : " (" + crop.getVariety() + ")");
        drawLine(name, subheadingPaint, 4);
        drawBody("Plot: " + crop.getPlotName());
        if (isHarvested) {
            drawBody("Harvested: " + DateUtils.toDisplayFormat(crop.getHarvestedDate()) +
                    "   Yield: " + (isEmpty(crop.getYieldAmount()) ? "Not recorded" : crop.getYieldAmount()));
        } else {
            drawBody("Planted: " + DateUtils.toDisplayFormat(crop.getPlantingDate()) +
                    "   Expected harvest: " + DateUtils.toDisplayFormat(crop.getExpectedHarvestDate()));
        }
        drawBody("Area planted: " + safe(crop.getAreaPlanted()));

        List<ActivityLog> activities = dbHelper.getActivitiesForCrop(crop.getId());
        if (!activities.isEmpty()) {
            drawLine(activities.size() + " activity log entr" + (activities.size() == 1 ? "y" : "ies") + " recorded.",
                    mutedPaint, 10);
        } else {
            y += 8;
        }
    }

    // ---------------- drawing helpers ----------------

    private void startNewPage() {
        if (currentPage != null) {
            pdfDocument.finishPage(currentPage);
        }
        pageNumber++;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
        currentPage = pdfDocument.startPage(pageInfo);
        canvas = currentPage.getCanvas();
        y = MARGIN;
    }

    private void ensureSpace(float needed) {
        if (y + needed > PAGE_HEIGHT - MARGIN) {
            startNewPage();
        }
    }

    private void drawLine(String text, Paint paint, float extraSpacing) {
        ensureSpace(paint.getTextSize() + extraSpacing);
        canvas.drawText(text, MARGIN, y + paint.getTextSize(), paint);
        y += paint.getTextSize() + extraSpacing;
    }

    private void drawBody(String text) {
        for (String line : wrapText(text, bodyPaint, PAGE_WIDTH - 2f * MARGIN)) {
            drawLine(line, bodyPaint, 4);
        }
    }

    private void drawSectionDivider() {
        ensureSpace(20);
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
        y += 16;
    }

    private List<String> wrapText(String text, Paint paint, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = (current.length() == 0) ? word : current + " " + word;
            if (paint.measureText(candidate) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safe(String s) {
        return isEmpty(s) ? "Not specified" : s;
    }

    private String formatNumber(double v) {
        return (v == Math.floor(v)) ? String.valueOf((long) v) : String.format("%.1f", v);
    }

    private double parseLeadingNumber(String text) {
        if (text == null) return 0;
        Matcher matcher = LEADING_NUMBER.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException ignored) { }
        }
        return 0;
    }
}