package com.chshru.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.media.audiofx.Visualizer;
import android.view.View;

public class AudioView extends View implements Visualizer.OnDataCaptureListener {

    private static final int DN_W = 480;
    private static final int DN_H = 160;
    private static final int DN_SL = 8;
    private static final int DN_SW = 3;
    private static final int COLOR = Color.parseColor("#ffffff");

    private int hGap = 0;
    private int vGap = 0;
    private int levelStep = 0;
    private float strokeWidth = 0;
    private float strokeLength = 0;
    /**
     * It is the max level.
     */
    protected final static int MAX_LEVEL = 40;
    /**
     * It is the cylinder number.
     */
    protected final static int CYLINDER_NUM = 30;
    /**
     * It is the visualizer.
     */
    protected Visualizer mVisualizer = null;
    /**
     * It is the paint which is used to draw to visual effect.
     */
    protected Paint mPaint = null;
    /**
     * It is the buffer of fft.
     */
    protected byte[] mData = new byte[CYLINDER_NUM];

    boolean mDataEn = true;

    /**
     * It constructs the base visualizer view.
     * It is the context of the view owner.
     */
    public AudioView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        float w, h, xr, yr;
        w = right - left;
        h = bottom - top;
        xr = w / (float) DN_W;
        yr = h / (float) DN_H;

        strokeWidth = DN_SW * yr;
        strokeLength = DN_SL * xr;
        hGap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1));
        vGap = (int) (h / (MAX_LEVEL + 2));

        mPaint.setStrokeWidth(strokeWidth);
    }

    protected void drawCylinder(Canvas canvas, float x, byte value) {
        if (value < 0) value = 0;
        for (int i = 0; i < value; i++) {
            float y = getHeight() - i * vGap - vGap;
            canvas.drawLine(x, y, x + strokeLength, y, mPaint);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //for (int i = 0; i < CYLINDER_NUM; i++) {
        //    drawCylinder(canvas, strokeWidth / 2 + hGap + i * (hGap + strokeLength), mData[i]);
        //}
        for (int i = 0; i < mData.length ; i++) {
            System.out.print(mData[i]+" ");
        }
        System.out.println();
    }

    /**
     * It sets the visualizer of the view. DO set the viaulizer to null when exit the program.
     * visualizer It is the visualizer to set.
     */
    public void setVisualizer(Visualizer visualizer) {
        if (visualizer != null) {
            if (!visualizer.getEnabled()) {
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
            }
            levelStep = 128 / MAX_LEVEL;
            visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate() / 2, false, true);
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
        if (mDataEn) {
            model[0] = (byte) Math.abs(fft[1]);
            int j = 1;
            for (int i = 2; i < fft.length; ) {
                model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                i += 2;
                j++;
            }
        } else {
            for (int i = 0; i < CYLINDER_NUM; i++) {
                model[i] = 0;
            }
        }
        for (int i = 0; i < CYLINDER_NUM; i++) {
            final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / levelStep);

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
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                      int samplingRate) {
        // Do nothing.
    }
}
