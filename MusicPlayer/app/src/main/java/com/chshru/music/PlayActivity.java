package com.chshru.music;

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


import com.chshru.music.datautil.Player;
import com.chshru.music.datautil.MusicList;
import com.chshru.music.service.AudioView;
import com.chshru.music.service.Controller;
import com.chshru.music.service.PlayService;

import java.util.Timer;
import java.util.TimerTask;

import static com.chshru.music.datautil.Config.*;

/**
 * Created by chshru on 2017/2/25.
 */


public class PlayActivity extends Activity implements View.OnClickListener, Player.MusicListener {


    private SeekBar seekBar;
    private TextView curTime;
    private TextView time;
    private TextView name;
    private TextView artist;
    private ImageView pauseImg;
    private ImageView preImg;
    private ImageView nextImg;
    private Visualizer visualizer;
    private Equalizer equalizer;


    private Player mPlayer;
    private Controller mController;
    private AppContext app;
    private TimerTask task;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        initializeView();
        initializeListener();

    }

    private void initializeListener() {
        nextImg.setOnClickListener(this);
        preImg.setOnClickListener(this);
        pauseImg.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean flag;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                flag = mPlayer.isPlaying();
                if (flag) {
                    mPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (flag) {
                    mPlayer.start();
                }
            }
        });

        task = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_FRESH);
            }
        };
    }

    private final int MSG_FRESH = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FRESH:
                    time.setText(timeToString(mPlayer.getDuration()));
                    curTime.setText(timeToString(mPlayer.getCurDuration()));
                    seekBar.setMax(mPlayer.getDuration());
                    seekBar.setProgress(mPlayer.getCurDuration());
                    break;
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mController = (Controller) binder;
            if (app.getPlayer() != null) {
                mPlayer = app.getPlayer();
                mPlayer.setController(mController);
                mPlayer.addMusicListener(PlayActivity.this);
            } else {
                MusicList mList = MusicList.getInstance(getApplicationContext());
                mPlayer = new Player(mController, mList);
                mPlayer.addMusicListener(PlayActivity.this);
            }
            Timer timer = new Timer(true);
            timer.schedule(task, 0, 100);
            onPlayerStatusChange();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.removeMusicListener(PlayActivity.this);
            if (task != null) {
                task.cancel();
                task = null;
            }
            mController = null;
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playing_next:
                mPlayer.next();
                break;
            case R.id.playing_pause:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
                break;
            case R.id.playing_pre:
                mPlayer.pre();
                break;
        }
    }


    private void initializeView() {
        nextImg = (ImageView) findViewById(R.id.playing_next);
        nextImg.setImageResource(R.drawable.playing_next);
        pauseImg = (ImageView) findViewById(R.id.playing_pause);
        preImg = (ImageView) findViewById(R.id.playing_pre);
        preImg.setImageResource(R.drawable.playing_pre);
        name = (TextView) findViewById(R.id.playing_name);
        artist = (TextView) findViewById(R.id.playing_artist);
        curTime = (TextView) findViewById(R.id.playing_current);
        time = (TextView) findViewById(R.id.playing_sum);
        seekBar = (SeekBar) findViewById(R.id.playing_seek_bar);
        try {
            initAudioFxUi();
        } catch (Exception e) {
            e.printStackTrace();
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
    }


    private String timeToString(int t) {
        String sMin, sSec;
        sMin = String.valueOf((t / 1000 / 60));
        sSec = String.valueOf((t / 1000 % 60));
        sMin = sMin.length() < 2 ? "0" + sMin : sMin;
        sSec = sSec.length() < 2 ? "0" + sSec : sSec;
        return sMin + ":" + sSec;
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
        super.onResume();
        app = (AppContext) getApplication();
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    @Override
    public void onPlayerStatusChange() {
        int res = mPlayer.isPlaying() ?
                R.drawable.playing_run :
                R.drawable.playing_pause;
        pauseImg.setImageResource(res);
        name.setText(mPlayer.getCurName());
        artist.setText(mPlayer.getCurArtist());
    }
}

