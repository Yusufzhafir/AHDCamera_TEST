package com.quectel.multicamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class DefineLinearLayout extends LinearLayout {
    private Paint paint0, paint1;
    private boolean isLine = false;
    private boolean isADASEnable = false;
    private boolean isFacePoint = false;
    private boolean isDMSEnable = false;
    private Point point0Start, point0End, point1Start, point1End;
    private int[] rectangleArray = new int[25];
    private int[] dmsFacePointArray = new int[62];
    private int[] lane1 = new int[315];
    private int[] lane2 = new int[315];

    public DefineLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        paint0 = new Paint();
        paint0.setAntiAlias(true);
        paint0.setColor(Color.GREEN);
        paint0.setStyle(Paint.Style.FILL);
        paint0.setStrokeWidth(6);

        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(Color.RED);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setStrokeWidth(1);

        point0Start = new Point();
        point0End = new Point();
        point0Start.x = 0;
        point0Start.y = 0;
        point0End.x = 0;
        point0End.y = 0;

        point1Start = new Point();
        point1End = new Point();
        point1Start.x = 0;
        point1Start.y = 0;
        point1End.x = 0;
        point1End.y = 0;

        for (int i = 0; i < 25; i++) {
            rectangleArray[i] = 0;
        }

        dmsFacePointArray[0] = 0;//人脸特征点的个数
        dmsFacePointArray[37] = 0;//特征目标的个数

        lane1[0] = 0;
        lane2[0] = 0;
    }

    public void setADASEnable(boolean enable) {
        isADASEnable = enable;
    }

    public void setLineEnable(boolean enable) {
        isLine = enable;
    }

    public void setPointLane2(int[] lane) {
        this.lane2 = lane;
    }

    public void setPointLane1(int[] lane) {
        this.lane1 = lane;
    }

    public void setRectanglePara0(int[] rectArray) {
        rectangleArray = rectArray;
    }

    public void setDMSEnable(boolean enable) {
        isDMSEnable = enable;
    }

    public void setFacePointEnable(boolean enable) {
        isFacePoint = enable;
    }

    public void setDmsFacePointArray(int[] dmsArray) {
        dmsFacePointArray = dmsArray;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isADASEnable) {
            if (isLine) {
                for (int i = 0; i < lane1[0]; i++) {
                    canvas.drawPoint(lane1[i * 2 + 1], lane1[i * 2 + 2], paint0);
                }

                for (int i = 0; i < lane2[0]; i++) {
                    canvas.drawPoint(lane2[i * 2 + 1], lane2[i * 2 + 2], paint0);
                }

                for (int i = 0; i < rectangleArray[0]; i++) {
                    canvas.drawLine(rectangleArray[i * 4 + 1], rectangleArray[i * 4 + 4], rectangleArray[i * 4 + 1], rectangleArray[i * 4 + 3], paint1);//left
                    canvas.drawLine(rectangleArray[i * 4 + 2], rectangleArray[i * 4 + 3], rectangleArray[i * 4 + 2], rectangleArray[i * 4 + 4], paint1);//right
                    canvas.drawLine(rectangleArray[i * 4 + 1], rectangleArray[i * 4 + 3], rectangleArray[i * 4 + 2], rectangleArray[i * 4 + 3], paint1);//top
                    canvas.drawLine(rectangleArray[i * 4 + 1], rectangleArray[i * 4 + 4], rectangleArray[i * 4 + 2], rectangleArray[i * 4 + 4], paint1);//bottom
                }
            }
        }

        if (isDMSEnable) {
            if (isFacePoint) {
                for (int i = 0; i < dmsFacePointArray[0]; i++)
                    canvas.drawPoint(dmsFacePointArray[i * 2 + 1], dmsFacePointArray[i * 2 + 2], paint0);
                for (int i = 0; i < dmsFacePointArray[37]; i++) {
                    canvas.drawLine(dmsFacePointArray[i * 4 + 38], dmsFacePointArray[i * 4 + 41], dmsFacePointArray[i * 4 + 38], dmsFacePointArray[i * 4 + 40], paint1);//left
                    canvas.drawLine(dmsFacePointArray[i * 4 + 39], dmsFacePointArray[i * 4 + 40], dmsFacePointArray[i * 4 + 39], dmsFacePointArray[i * 4 + 41], paint1);//right
                    canvas.drawLine(dmsFacePointArray[i * 4 + 38], dmsFacePointArray[i * 4 + 40], dmsFacePointArray[i * 4 + 39], dmsFacePointArray[i * 4 + 40], paint1);//top
                    canvas.drawLine(dmsFacePointArray[i * 4 + 38], dmsFacePointArray[i * 4 + 41], dmsFacePointArray[i * 4 + 39], dmsFacePointArray[i * 4 + 41], paint1);//bottom
                }
            }
        }

        postInvalidate();
    }
}
