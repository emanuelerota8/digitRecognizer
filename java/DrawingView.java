package com.emanuelerota.drawingtest;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Context context;
    private Paint paint = new Paint();
    private Path path = new Path();
    int drawAction;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context=context;
        drawAction=0;

        paint.setAntiAlias(true);
        paint.setStrokeWidth(100f);
        paint.setColor(Color.rgb(128,128,128));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        switch (drawAction){
            case 0:
                //normal mode (writing mode)
                canvas.drawPath(path, paint);
                break;
            case 1:
                //clean screen
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                path.reset();
                drawAction=0;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the coordinates of the touch event.
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Set a new starting point
                path.moveTo(eventX, eventY);
                return true;
            case MotionEvent.ACTION_MOVE:
                // Connect the points
                path.lineTo(eventX, eventY);
                break;
            default:
                return false;
        }

        // Makes our view repaint and call onDraw
        invalidate();
        return true;
    }

    public void cleanScreen(){
        drawAction=1;
        invalidate();
    }

}
