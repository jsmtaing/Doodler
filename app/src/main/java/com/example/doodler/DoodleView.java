package com.example.doodler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DoodleView extends View {
    private final Paint paint;
    private final Path path;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f); // default stroke width (initial setup)

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the path with the current paint (stroke width, color, etc.)
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate(); // Redraw the view with the updated path
        return true;
    }

    // Set the stroke width
    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);  // Set the new stroke width
        invalidate(); // Ensure the view is redrawn with the new stroke width
    }

    public float getStrokeWidth() {
        return paint.getStrokeWidth(); // Get the current stroke width
    }

    // Set the stroke color
    public void setStrokeColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    // Set the stroke alpha (opacity)
    public void setStrokeAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidate();
    }

    public int getStrokeAlpha() {
        return paint.getAlpha();
    }

    public void clearCanvas() {
        path.reset(); // Reset the path to clear the canvas
        invalidate(); // Redraw after clearing
    }
}
