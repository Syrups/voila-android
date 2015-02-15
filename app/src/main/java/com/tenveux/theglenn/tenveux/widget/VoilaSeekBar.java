package com.tenveux.theglenn.tenveux.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.tenveux.theglenn.tenveux.R;

/**
 * Created by theGlenn on 15/02/15.
 */
public class VoilaSeekBar extends SeekBar {
    private Paint selected, unselected, green, red;
    private int halfSize = 9;
    float margin = 45.f;
    private RectF position;

    public VoilaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public VoilaSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);

    }

    public VoilaSeekBar(Context context) {
        super(context);
        this.init(context);
    }

    public void init(Context context) {
        selected = new Paint(Paint.ANTI_ALIAS_FLAG);
        //selected.setColor(getContext().getResources().getColor(R.color.TextColor));
        selected.setStyle(Paint.Style.FILL);
        unselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselected.setColor(getContext().getResources().getColor(android.R.color.darker_gray));
        selected.setStyle(Paint.Style.FILL);
        position = new RectF();

        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        green.setStyle(Paint.Style.FILL);
        green.setColor(Color.GREEN);

        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        red.setStyle(Paint.Style.FILL);
        red.setColor(Color.RED);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        float halfHeight = (canvas.getHeight() + paddingTop) * .5f;

        //float margin = 35.f = (canvas.getWidth() - (paddingLeft + getPaddingRight())) / 16;
        //int numberOfDots = (canvas.getWidth() - (paddingLeft + getPaddingRight())) / ((halfSize * 2) + (int) margin);
        //int numberOfDots = canvas.getWidth() / ((halfSize * 2) + (int) margin);
        //Log.d("Margin", canvas.getWidth() + ": " + margin + " for " + numberOfDots + "  dots");

        int numberOfDots = 16;
        float halfWay = numberOfDots / 2;
        int raster = getMax() / numberOfDots;

        int progress = getProgress() / raster;

        Log.d("Progress", raster + ": " + getProgress() + "-> " + progress);

        for (int i = 0; i < numberOfDots; i++) {

            boolean want = i > halfWay;
            boolean colored = false;
            Paint p = null;

            if (want) {
                colored = (i < progress);
                p = colored ? green : unselected;
            } else {
                colored = (i > progress);
                p = colored ? red : unselected;
            }


            position.set(
                    paddingLeft + (i * margin) - halfSize,
                    halfHeight - halfSize,
                    paddingLeft + (i * margin) + halfSize,
                    halfHeight + halfSize);

            canvas.drawOval(position, p);
            //canvas.drawOval(position, (i < getProgress()) ? selected : unselected);

        }
        super.onDraw(canvas);
    }

    public int getDotsSize() {
        return halfSize * 2;
    }

    public void setDotsSize(int dotsSize) {
        this.halfSize = dotsSize / 2;
    }
}
