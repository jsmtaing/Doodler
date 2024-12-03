package com.example.doodler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.compose.ui.graphics.drawscope.Stroke;

public class DoodleView extends View {
    private Paint paint;
    private Path currentPath;
    private Bitmap bitmap;
    private Canvas canvas;

    //Strokes storage.
    private final List<Stroke> strokes = new ArrayList<>();
    private int currentColor = Color.BLACK;
    private int currentStrokeWidth = 10;
    private int currentAlpha = 255;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        resetPaint();

        currentPath = new Path();
    }

    private void resetPaint() {
        paint.setColor(currentColor);
        paint.setStrokeWidth(currentStrokeWidth);
        paint.setAlpha(currentAlpha);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //Redraw previous (all) strokes.
        for (Stroke stroke : strokes) {
            paint.setColor(stroke.color);
            paint.setStrokeWidth(stroke.strokeWidth);
            paint.setAlpha(stroke.alpha);
            canvas.drawPath(stroke.path, paint);
        }

        //Draw the current path
        paint.setColor(currentColor);
        paint.setStrokeWidth(currentStrokeWidth);
        paint.setAlpha(currentAlpha);
        canvas.drawPath(currentPath, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                strokes.add(new Stroke(new Path(currentPath), currentColor, currentStrokeWidth, currentAlpha));
                currentPath.reset();
                break;
        }
        invalidate();
        return true;
    }

    // Set the stroke width
    public void setStrokeWidth(int width) {
        currentStrokeWidth = width;
        resetPaint();
        invalidate();
    }
    // Set the stroke color
    public void setStrokeColor(int color) {
        currentColor = color;
        resetPaint();
        invalidate();
    }
    // Set the stroke alpha (opacity)
    public void setStrokeAlpha(int alpha) {
        currentAlpha = alpha;
        resetPaint();
        invalidate();
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void clearCanvas() {
        strokes.clear(); // Reset the path to clear the canvas
        invalidate(); // Redraw after clearing
    }

    //Inner class to represent a single stroke.
    private static class Stroke {
        Path path;
        int color;
        int strokeWidth;
        int alpha;

        Stroke(Path path, int color, int strokeWidth, int alpha) {
            this.path = path;
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.alpha = alpha;
        }
    }
}
