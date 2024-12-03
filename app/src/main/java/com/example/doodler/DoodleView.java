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
        super (context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);

        path = new Path();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
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
        invalidate(); //Redraws the view.
        return true;
    }
}
