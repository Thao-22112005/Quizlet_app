package com.example.englishvocabulary.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint gridPaint;

    private int[] values = new int[7];

    private String[] days = {
            "T2", "T3", "T4", "T5", "T6", "T7", "CN"
    };

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(
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

    public void setDays(String[] days) {

        if (days != null && days.length > 0) {
            this.days = days;
            invalidate();
        }
    }

    public void setValues(int[] values) {

        if (values != null && values.length > 0) {
            this.values = values;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (values == null || values.length == 0) {
            return;
        }

        float width = getWidth();
        float height = getHeight();

        float left = 40;
        float right = width - 15;
        float top = 15;
        float bottom = height - 35;

        float chartWidth = right - left;
        float chartHeight = bottom - top;

        // =========================
        // GRID
        // =========================

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

        // =========================
        // TÌM GIÁ TRỊ LỚN NHẤT
        // =========================

        int max = 10;

        for (int value : values) {

            if (value > max) {
                max = value;
            }
        }

        // =========================
        // VẼ ĐƯỜNG BIỂU ĐỒ
        // =========================

        Path path = new Path();

        int count = values.length;

        for (int i = 0; i < count; i++) {

            float x;

            if (count == 1) {
                x = left + chartWidth / 2;
            } else {
                x = left +
                        (chartWidth * i / (count - 1));
            }

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

        // =========================
// VẼ ĐIỂM + SỐ LƯỢNG
// =========================

        for (int i = 0; i < count; i++) {

            float x;

            if (count == 1) {
                x = left + chartWidth / 2;
            } else {
                x =
                        left +
                                (chartWidth * i / (count - 1));
            }

            float y =
                    bottom -
                            ((float) values[i] / max)
                                    * chartHeight;

            // Vẽ điểm
            canvas.drawCircle(
                    x,
                    y,
                    5,
                    pointPaint
            );

            // =========================
            // VẼ SỐ LƯỢNG TỪ
            // =========================

            String valueText =
                    String.valueOf(values[i]);

            float valueTextWidth =
                    textPaint.measureText(valueText);

            // Chiều cao của chữ
            Paint.FontMetrics fontMetrics =
                    textPaint.getFontMetrics();

// Mặc định: số nằm phía trên điểm
            float textY = y - 10;

// Mặc định căn giữa theo điểm
            float textX =
                    x - valueTextWidth / 2;

// Nếu số bị vượt phía trên
// thì dịch sang phải một chút
            if (textY + fontMetrics.top < top) {

                textX =
                        x + 8;

                textY =
                        y + 5 - fontMetrics.top;
            }

// Không cho số vượt mép phải
            if (textX + valueTextWidth > right) {

                textX =
                        x - valueTextWidth - 8;
            }

            canvas.drawText(
                    valueText,
                    textX,
                    textY,
                    textPaint
            );

        }

        // =========================
        // VẼ NHÃN
        // =========================

        if (days != null && days.length == count) {

            for (int i = 0; i < count; i++) {

                // Với biểu đồ tháng: chỉ hiện một số nhãn
//                if (count > 7) {
//
//                    boolean showLabel =
//                            i == 0 ||
//                                    i % 5 == 0 ||
//                                    i == count - 1;
//
//                    if (!showLabel) {
//                        continue;
//                    }
//                }

                float x;

                if (count == 1) {
                    x = left + chartWidth / 2;
                } else {
                    x =
                            left +
                                    (chartWidth * i / (count - 1));
                }

                String label = days[i];

                // Độ rộng của chữ
                float textWidth =
                        textPaint.measureText(label);

                // Căn giữa nhãn theo điểm
                float textX =
                        x - textWidth / 2;

                // Không cho chữ vượt mép trái
                if (textX < left) {
                    textX = left;
                }

                // Không cho chữ vượt mép phải
                if (textX + textWidth > right) {
                    textX = right - textWidth;
                }

                canvas.drawText(
                        label,
                        textX,
                        height - 10,
                        textPaint
                );
            }
        }
    }
}