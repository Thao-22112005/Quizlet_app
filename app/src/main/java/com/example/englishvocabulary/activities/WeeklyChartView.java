package com.example.englishvocabulary.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WeeklyChartView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint gridPaint;

    private int[] values = {
            0, 0, 0, 0, 0, 0, 0
    };

    private String[] days = {
            "T2", "T3", "T4", "T5", "T6", "T7", "CN"
    };

    public WeeklyChartView(Context context) {
        super(context);
        init();
    }
    public void setDays(String[] days) {
        if (days != null && days.length == 7) {
            this.days = days;
            invalidate();
        }
    }

    public WeeklyChartView(
            Context context,
            AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeeklyChartView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFF3155E7);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(0xFF3155E7);
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF6B7280);
        textPaint.setTextSize(25);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0xFFE5E7EB);
        gridPaint.setStrokeWidth(1);
    }

    public void setValues(int[] values) {

        if (values != null && values.length == 7) {
            this.values = values;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float left = 40;
        float right = width - 15;
        float top = 15;
        float bottom = height - 35;

        float chartWidth = right - left;
        float chartHeight = bottom - top;


        for (int i = 0; i <= 4; i++) {

            float y =
                    top +
                            chartHeight * i / 4;

            canvas.drawLine(
                    left,
                    y,
                    right,
                    y,
                    gridPaint
            );
        }


        int max = 10;

        for (int value : values) {

            if (value > max) {
                max = value;
            }
        }


        Path path = new Path();

        for (int i = 0; i < 7; i++) {

            float x =
                    left +
                            (chartWidth * i / 6);

            float y =
                    bottom -
                            ((float) values[i] / max)
                                    * chartHeight;

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        canvas.drawPath(path, linePaint);

        for (int i = 0; i < 7; i++) {

            float x =
                    left +
                            (chartWidth * i / 6);

            float y =
                    bottom -
                            ((float) values[i] / max)
                                    * chartHeight;

            canvas.drawCircle(
                    x,
                    y,
                    5,
                    pointPaint
            );

            canvas.drawText(
                    days[i],
                    x - 10,
                    height - 10,
                    textPaint
            );
        }
    }
}