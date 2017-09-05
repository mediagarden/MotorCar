package com.samsung.baymax;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by STMC-LH on 2017/8/25.
 */

public class DrawView extends View {
    final float scale = getContext().getResources().getDisplayMetrics().density;
    public float X;
    public float Y;
    public float Z;
    public float currentX;
    public float currentY;
    private float radius;

    public DrawView(Context context, int X, int Y, int Z) {
        super(context);
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        currentX =  X * scale;
        currentY =  Y * scale;
        radius = Z * scale;
    }

    public float getInitX() {
        return X * scale;
    }

    public float getInitY() {
        return Y * scale;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public float getRadius() {
        return radius;
    }

    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(currentX, currentY, radius, paint);
    }
}