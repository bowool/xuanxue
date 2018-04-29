package com.bowool.xuanxue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DraggingPoint extends View {

    private Paint paint;
    private float X = 200;
    private float Y = 150;
    Rect rect = new Rect();
    private boolean M=false;


    public DraggingPoint(Context context) {
        super(context);
    }

    public DraggingPoint(Context context, @Nullable AttributeSet att) {
        super(context, att);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    public float returnX(){
        return X;
    }

    public float returnY(){
        return Y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(X, Y, 50, paint);
        rect.set((int) (X - 50), (int) (Y - 50), (int) (X + 50), (int) (Y + 50));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x1 = event.getX();
                float y1 = event.getY();
                if(x1 >rect.left && x1 <rect.right && y1>rect.top &&y1<rect.bottom){
                    M = true;
                }else {
                    M = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(M){
                    X = event.getX();
                    Y = event.getY();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                // 点击事件
                Toast.makeText(getContext(),"抢玄学中，退出请点右上角",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

}