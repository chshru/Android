package com.chshru.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.view.View;

/**
 * Created by abc on 18-5-3.
 */

public class DynamicView extends View implements Visualizer.OnDataCaptureListener {

    private final int MAX = 128;

    private Paint paint;
    private int line;
    private float[] data;
    private float[] cache;
    private AudioManager am;

    public DynamicView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        line = 20;
        data = new float[line];
        cache = new float[line];
        am = (AudioManager) getContext().
                getSystemService(Context.AUDIO_SERVICE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        float pw = 1.0f * getWidth() / (1.0f * line + 4);
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
                    getBottom() - getHeight() / (MAX * 1.5f) * (cache[i]) - (pw / 3),
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
        float[] model = new float[fft.length / 2 + 1];
        model[0] = Math.abs(fft[1]);
        for (int i = 2, j = 1; i < fft.length; i += 2, j++) {
            model[j] = (float) Math.hypot(fft[i], fft[i + 1]);
        }

//        int cur = 0, max = 0;
//        float mod = 0f;
//        if (am != null) {
//            max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            cur = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        }
//        if (cur != 0 && max != 0) {
//            mod = 1.0f * cur / max;
//        }

        for (int i = 0; i < line; i++) {
            data[i] = Math.abs(model[line - i]) /*/ mod*/;

            if (data[i] > cache[i]) {
                cache[i] = data[i];
            } else {
                cache[i] -= cache[i] <= 0 ? 0 : 2;
            }
        }
        postInvalidate();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }
}
