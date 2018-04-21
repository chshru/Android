package com.chshru.music.activity;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.chshru.music.R;
import com.chshru.music.datautil.Player;
import com.chshru.music.datautil.MusicList;
import com.chshru.music.service.AudioView;
import com.chshru.music.service.Controller;
import com.chshru.music.service.PlayService;

import static com.chshru.music.datautil.Config.*;

/**
 * Created by chshru on 2017/2/25.
 */


public class PlayActivity extends Activity implements View.OnClickListener {


    private SeekBar seekBar;
    private TextView curDuration;
    private TextView duration;
    private TextView name;
    private TextView artist;
    private ImageView pauseImg;
    private ImageView preImg;
    private ImageView nextImg;
    private Thread thread;
    private int freshTime = TIME_LONG;
    private Visualizer visualizer;
    private Equalizer equalizer;
    private Handler handler;

    private Player mPlayer;
    private Controller mController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        initViewAndResource();
        initializeService();
        mPlayer = new Player(mController, MusicList.getInstance(this));
        try {
            initAudioFxUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextImg.setOnClickListener(this);
        preImg.setOnClickListener(this);
        pauseImg.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean preIsMusicPlay;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                preIsMusicPlay = musicPlaying;
                freshTime = TIME_SHORT;
                if (preIsMusicPlay) {
                    mPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                freshTime = TIME_LONG;
                if (preIsMusicPlay) {
                    mPlayer.start();
                }
            }
        });
    }


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mController = (Controller) binder;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mController = null;
        }
    };


    private void initializeService() {
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playing_next:
                mPlayer.next();
                threadSleep(TIME_SHORT);
                break;
            case R.id.playing_pause:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
                threadSleep(TIME_SHORT);
                break;
            case R.id.playing_pre:
                mPlayer.pre();
                threadSleep(TIME_SHORT);
                break;
        }
    }

    private void threadSleep(int t) {
        try {
            thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initViewAndResource() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FRESH)
                    freshSeekBar();
            }
        };
        nextImg = (ImageView) findViewById(R.id.playing_next);
        nextImg.setImageResource(R.drawable.playing_next);
        pauseImg = (ImageView) findViewById(R.id.playing_pause);
        preImg = (ImageView) findViewById(R.id.playing_pre);
        preImg.setImageResource(R.drawable.playing_pre);
        name = (TextView) findViewById(R.id.playing_name);
        artist = (TextView) findViewById(R.id.playing_artist);
        curDuration = (TextView) findViewById(R.id.playing_current);
        duration = (TextView) findViewById(R.id.playing_sum);
        seekBar = (SeekBar) findViewById(R.id.playing_seek_bar);
        freshTextView();
        startThread();
    }

    private void startThread() {
        if (thread != null) {
            if (thread.isInterrupted()) {
                thread.start();
            }
        } else {
            thread = new Thread(new SeekBarThread());
            thread.start();
        }

    }

    private void freeResource() {

        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
    }

    public void freshTextView() {
        name.setText(MusicList.getInstance(this).getList().get(curPosition).getName());
        seekBar.setMax(mPlayer.getDuration());
        artist.setText(MusicList.getInstance(this).getList().get(curPosition).getArtist());
        if (musicPlaying) pauseImg.setImageResource(R.drawable.playing_run);
        else pauseImg.setImageResource(R.drawable.playing_pause);
    }

    private void freshSeekBar() {
        seekBar.setProgress(mPlayer.getCurDuration());
        duration.setText(calcTime(mPlayer.getDuration()));
        curDuration.setText(calcTime(mPlayer.getCurDuration()));
    }

    private String calcTime(int t) {
        String sMin, sSec;
        sMin = String.valueOf((t / 1000 / 60));
        sSec = String.valueOf((t / 1000 % 60));
        sMin = sMin.length() < 2 ? "0" + sMin : sMin;
        sSec = sSec.length() < 2 ? "0" + sSec : sSec;
        return sMin + ":" + sSec;
    }

    private class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    handler.sendEmptyMessage(FRESH);
                    Thread.sleep(freshTime);
                } catch (InterruptedException e) {
                    startThread();
                }
            }
        }
    }

    private void initAudioFxUi() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.audio);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        AudioView mAudio = new AudioView(this);
        float VISUALIZER_HEIGHT_DIP = 200f;
        mAudio.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLayout.addView(mAudio);
        visualizer = new Visualizer(mPlayer.getSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mAudio.setVisualizer(visualizer);
        equalizer = new Equalizer(0, mPlayer.getSessionId());
        equalizer.setEnabled(true);
        visualizer.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        freeResource();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        startThread();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        startThread();
        super.onRestart();
    }

}

