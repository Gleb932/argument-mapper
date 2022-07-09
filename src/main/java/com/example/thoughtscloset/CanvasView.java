package com.example.thoughtscloset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {

    private Paint fillPaint = new Paint();

    private void init()
    {
        fillPaint.setStyle(Paint.Style.FILL);
    }

    public CanvasView(Context context) {
        super(context);
        init();
    }
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void fill(int color)
    {
        fillPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawPaint(fillPaint);
    }
}
