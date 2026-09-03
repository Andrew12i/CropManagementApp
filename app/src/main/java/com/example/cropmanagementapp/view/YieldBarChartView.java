package com.example.cropmanagementapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A small, self-drawn bar chart (no external library) comparing a set of
 * labelled numeric values — used here to compare harvest yields by plot.
 * Draws inside a HorizontalScrollView so it can scroll if there are many
 * bars. Every bar shows its exact value as text, so the comparison never
 * depends on color alone.
 */
public class YieldBarChartView extends View {

    private final List<String> labels = new ArrayList<>();
    private final List<Float> values = new ArrayList<>();

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int BAR_COLOR = 0xFF1B5E20;   // green_primary
    private static final int LABEL_COLOR = 0xFF1A1A1A; // text_primary
    private static final int AXIS_COLOR = 0xFFBDBDBD;

    private final int barWidthPx;
    private final int barSpacingPx;
    private final int chartHeightPx;
    private final int labelTextSizePx;
    private final int valueTextSizePx;

    public YieldBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;
        barWidthPx = (int) (60 * density);
        barSpacingPx = (int) (24 * density);
        chartHeightPx = (int) (200 * density);
        labelTextSizePx = (int) (13 * density);
        valueTextSizePx = (int) (13 * density);

        barPaint.setColor(BAR_COLOR);
        barPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(LABEL_COLOR);
        labelPaint.setTextSize(labelTextSizePx);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(LABEL_COLOR);
        valuePaint.setTextSize(valueTextSizePx);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setFakeBoldText(true);

        axisPaint.setColor(AXIS_COLOR);
        axisPaint.setStrokeWidth(2 * density);

        emptyPaint.setColor(LABEL_COLOR);
        emptyPaint.setTextSize(labelTextSizePx);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** Replaces the chart's data and triggers a re-measure/redraw. */
    public void setData(List<String> newLabels, List<Float> newValues) {
        labels.clear();
        values.clear();
        if (newLabels != null && newValues != null) {
            int count = Math.min(newLabels.size(), newValues.size());
            for (int i = 0; i < count; i++) {
                labels.add(newLabels.get(i));
                values.add(newValues.get(i));
            }
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int barCount = Math.max(labels.size(), 1);
        int desiredWidth = barCount * (barWidthPx + barSpacingPx) + barSpacingPx;
        int minWidth = MeasureSpec.getSize(widthMeasureSpec);
        int width = Math.max(desiredWidth, minWidth);

        int desiredHeight = chartHeightPx + labelTextSizePx * 3; // room for axis + labels + value text
        setMeasuredDimension(width, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float axisY = chartHeightPx + valueTextSizePx * 1.5f;

        // Baseline axis
        canvas.drawLine(0, axisY, width, axisY, axisPaint);

        if (labels.isEmpty()) {
            canvas.drawText("No harvested crops yet", width / 2f, chartHeightPx / 2f, emptyPaint);
            return;
        }

        float maxValue = 0f;
        for (float v : values) {
            if (v > maxValue) maxValue = v;
        }
        if (maxValue <= 0f) maxValue = 1f; // avoid divide-by-zero when all yields are 0/unrecorded

        float x = barSpacingPx;
        for (int i = 0; i < labels.size(); i++) {
            float value = values.get(i);
            float barHeight = (value / maxValue) * (chartHeightPx - valueTextSizePx * 2);
            if (barHeight < 4) barHeight = 4; // keep zero/near-zero bars visible as a thin line

            float top = axisY - barHeight;
            RectF barRect = new RectF(x, top, x + barWidthPx, axisY);
            canvas.drawRoundRect(barRect, 8, 8, barPaint);

            String valueText = formatValue(value);
            canvas.drawText(valueText, x + barWidthPx / 2f, top - 10, valuePaint);

            String label = labels.get(i);
            canvas.drawText(truncateLabel(label), x + barWidthPx / 2f, axisY + labelTextSizePx + 12, labelPaint);

            x += barWidthPx + barSpacingPx;
        }
    }

    private String formatValue(float value) {
        if (value == Math.floor(value)) {
            return String.format(Locale.getDefault(), "%.0f", value);
        }
        return String.format(Locale.getDefault(), "%.1f", value);
    }

    private String truncateLabel(String label) {
        if (label.length() <= 10) return label;
        return label.substring(0, 9) + "…";
    }
}