package com.chshru.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


import com.chshru.music.activity.ListActivity;
import com.chshru.music.activity.PlayActivity;
import com.chshru.music.datautil.CtrlPlayer;
import com.chshru.music.datautil.FileCtrl;


/**
 * Created by chshru on 2017/2/25.
 */


public class PlayService extends Service {


    private static PlayService playService;
    private static MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        playService = this;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                CtrlPlayer.getInstance(PlayService.this).completion();
                freshAllView();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    public static int getAudioSessionId() {
        try {
            return mediaPlayer.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }

    public static void quickRun(int t) {
        mediaPlayer.seekTo(t);
    }

    public static int getDuration() {
        return mediaPlayer.getDuration();
    }

    public static int getCurDuration() {
        return mediaPlayer.getCurrentPosition();
    }

    public static void prepare(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        freshAllView();
    }

    public static void freshAllView() {
        try {
            FileCtrl.getInstance(playService).saveUserInfo();
            ListActivity.getListAty().reFreshView();
            PlayActivity.getPlatAty().freshTextView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        mediaPlayer.start();
        freshAllView();
    }

    public static void pause() {
        mediaPlayer.pause();
        freshAllView();
    }


    private void freePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        freePlayer();
        super.onDestroy();
    }
}

