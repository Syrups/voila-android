package com.tenveux.app.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by theGlenn on 21/02/15.
 */
public class VoilaLoaderImageVIew extends ImageView {


    private static final String BASE = "loader/loader__";
    private AnimationDrawable anim;

    public VoilaLoaderImageVIew(Context context) {
        super(context);

        if (!isInEditMode())
            init(context);
    }

    public VoilaLoaderImageVIew(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode())
            init(context);
    }

    public VoilaLoaderImageVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode())
            init(context);
    }

    private void init(Context context) {
        anim = new AnimationDrawable();
        for (int i = 0; i < 48; i++) {
            try {
                String path = BASE + i + ".png";
                InputStream inputStream = context.getAssets().open(path);
                Drawable d = Drawable.createFromStream(inputStream, null);

                anim.addFrame(d, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //if you want the animation to loop, set false
        anim.setOneShot(false);
        this.setImageDrawable(anim);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final float scale = getResources().getDisplayMetrics().density;

        int width = (int) (300 / scale);
        int height = (int) (200 / scale);

        setMeasuredDimension(width, height);
    }

    private void updateAnimationsState() {
        boolean running = getVisibility() == View.VISIBLE && hasWindowFocus();
        updateAnimationState(getDrawable(), running);
        updateAnimationState(getBackground(), running);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updateAnimationsState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        updateAnimationsState();
    }

    private void updateAnimationState(Drawable drawable, boolean running) {
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
            if (running) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
        }
    }
}