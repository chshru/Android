package com.chshru.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.view.View;

/**
 * Created by abc on 18-5-3.
 */

public class DynamicView extends View implements Visualizer.OnDataCaptureListener {

    private final int MAX = 128;

    private Paint paint;
    private int line;
    private byte[] data;
    private byte[] cache;


    public DynamicView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        line = 20;
        data = new byte[line];
        cache = new byte[line];
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int pw = getWidth() / (line + 4);
        paint.setStrokeWidth(pw);
        for (int i = 0; i < line; i++) {
            paint.setColor(0x55FFFFFF);
            canvas.drawLine(
                    getWidth() / line * (i + 0.5f),
                    getBottom() * 1.0f,
                    getWidth() / line * (i + 0.5f),
                    getBottom() - getHeight() / (MAX * 1.5f) * data[i],
                    paint
            );
            paint.setColor(0xFFFFFFFF);
            canvas.drawLine(
                    getWidth() / line * (i + 0.5f),
                    getBottom() - getHeight() / (MAX * 1.5f) * (cache[i]),
                    getWidth() / line * (i + 0.5f),
                    getBottom() - getHeight() / (MAX * 1.5f) * (cache[i] + pw / 6),
                    paint
            );
        }
    }

    public void setVisualizer(Visualizer visualizer) {
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
            visualizer.setDataCaptureListener(this,
                    Visualizer.getMaxCaptureRate() / 2,
                    false, true);
            visualizer.setEnabled(true);
        }
    }


    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int rate) {
        byte[] model = new byte[fft.length / 2 + 1];
        model[0] = (byte) Math.abs(fft[0]);
        for (int j = 1, i = 2; i < fft.length; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        fft[model.length - 1] = (byte) Math.abs(fft[1]);

        for (int i = 0; i < line; i++) {
            data[i] = (byte) (Math.abs(model[line - i - 1]));
            if (data[i] > cache[i]) {
                cache[i] = data[i];
            } else {
                cache[i] -= cache[i] == 0 ? 0 : 2;
            }
        }
        postInvalidate();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }
}
