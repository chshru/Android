package com.chshru.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by abc on 18-5-3.
 */

public class DynamicView extends View implements Visualizer.OnDataCaptureListener {

    private Paint mPaint;
    protected final static int CYLINDER_NUM = 30;
    protected byte[] mData = new byte[CYLINDER_NUM];
    protected final static int MAX_LEVEL = 50;
    private Visualizer mVisualizer;

    public DynamicView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(getWidth() / (CYLINDER_NUM + 4));
        for (int i = 0; i < CYLINDER_NUM; i++) {
            canvas.drawLine(
                    getWidth() / CYLINDER_NUM * (i + 0.5f),
                    getBottom() * 1.0f,
                    getWidth() / CYLINDER_NUM * (i + 0.5f),
                    getBottom() - getHeight() / (MAX_LEVEL*1.5f) * mData[i],
                    mPaint
            );
        }
    }

    public void setVisualizer(Visualizer visualizer) {
        if (visualizer != null) {
            if (!visualizer.getEnabled()) {
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
            }
            visualizer.setDataCaptureListener(this,
                    Visualizer.getMaxCaptureRate() / 2,
                    false, true);
        } else {
            if (mVisualizer != null) {
                mVisualizer.setEnabled(false);
                mVisualizer.release();
            }
        }
        mVisualizer = visualizer;
    }


    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        byte[] model = new byte[fft.length / 2 + 1];
        model[0] = (byte) Math.abs(fft[1]);
        int j = 1;
        for (int i = 2; i < fft.length; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }

        for (int i = 0; i < CYLINDER_NUM; i++) {
            final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / (128 / MAX_LEVEL));

            final byte b = mData[i];
            if (a > b) {
                mData[i] = a;
            } else {
                if (b > 0) {
                    mData[i]--;
                }
            }
        }
        postInvalidate();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }
}
