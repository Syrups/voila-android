package com.tenveux.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tenveux.app.R;

/**
 * Created by theGlenn on 15/02/15.
 */
public class VoilaSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final float MARGIN = 45.f;
    final float scale = getResources().getDisplayMetrics().density;

    private Paint unselected, green, red;
    private RectF position;
    private float radius = 4.5f;
    int raster;

    private OnVoilaSeekBarChangeListener mListner;
    ObjectAnimator animation;

    private DisplayMetrics displayMetrics;

    /**
     * Converts a given dip (density independent pixel) value to its corresponding pixel value.
     *
     * @param dips The dip value to convert, as float.
     * @return The pixel value, as int.
     */
    private int dipToPix(float dips) {
        if (displayMetrics == null)
            displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, displayMetrics);
    }

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

        this.initAnimation();
        this.setOnSeekBarChangeListener(this);
        this.setWillNotDraw(false);

        position = new RectF();

        unselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselected.setColor(getContext().getResources().getColor(android.R.color.darker_gray));
        unselected.setStyle(Paint.Style.FILL);

        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        green.setColor(getContext().getResources().getColor(R.color.cyan));
        green.setStyle(Paint.Style.FILL);

        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        red.setColor(getContext().getResources().getColor(R.color.red));
        green.setStyle(Paint.Style.FILL);
    }


    void initAnimation() {

        this.setProgress(100);
        this.incrementProgressBy(0);
        this.setMax(200);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {


        Log.d("scale", scale + "s");
        /*int width = canvas.getWidth();
        int height = canvas.getHeight();

        Paint mPaint = new Paint();

        // prepare a paint
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);

        // draw a rectangle
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL); //fill the background with blue color
        canvas.drawRect(0, 0, width, height, mPaint);*/


        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        float halfHeight = (canvas.getHeight() + paddingTop) * .5f;
        float dotHeight = halfHeight / 3.5f;
        radius = dipToPix(dotHeight * .5f);
        //* scale;

        int numberOfDots = 15;
        raster = getMax() / numberOfDots;

        float halfWay = numberOfDots / 2;
        float margin = (canvas.getWidth() - (paddingLeft + getPaddingRight())) / (numberOfDots - 1);

        int progress = getProgress() / raster;

        //Log.d("ginger", radius + "radiius ->" + this.getWidth() + " w " + halfHeight + " half ");

        for (int i = 0; i < numberOfDots; i++) {

            boolean want = i > halfWay;
            boolean colored;
            Paint p = unselected;

            if (want) {
                colored = (i < progress);
                p = colored ? green : unselected;
            } else {
                colored = (i > progress);
                p = colored ? red : unselected;
            }

            position.set(
                    (i * margin) - radius,
                    halfHeight - radius,
                    (i * margin) + radius,
                    halfHeight + radius);

            //if (i != 0 && i != numberOfDots)
            canvas.drawCircle(position.centerX() + paddingLeft, this.getHeight() / 2, 10, p);

        }


        super.onDraw(canvas);
    }

    public float getDotsSize() {
        return radius * 2;
    }

    public void setDotsSize(int dotsSize) {
        this.radius = dotsSize / 2;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress, boolean fromUser) {


        progress = progress / 10;
        int value = progress * 10;

        if (mListner != null) {
            mListner.onValueChanged(seekBar, value);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if (animation != null && animation.isRunning()) {
            animation.cancel();
        }
        mListner.onStartTrackingTouch();
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (mListner != null) {
            if (getProgress() == getMax()) {
                mListner.onOptionSelected(true);

            } else if (getProgress() == 0) {
                mListner.onOptionSelected(false);

            } else {

                int mid = getMax() / 2;
                animation = ObjectAnimator.ofInt(seekBar, "progress", mid);
                animation.setDuration(500);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mListner.onNeutralSelected();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mListner.onNeutralSelected();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }
    }

    public void setOnVoilaSeekBarChangeListener(OnVoilaSeekBarChangeListener onVoilaSeekBarChangeListener) {
        mListner = onVoilaSeekBarChangeListener;
    }

    public interface OnVoilaSeekBarChangeListener {
        public void onOptionSelected(boolean isPositive);

        public void onNeutralSelected();

        public void onStartTrackingTouch();

        public void onValueChanged(SeekBar seekBar, int value);
    }
}
