package com.chshru.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;


import com.chshru.music.activity.ListActivity;
import com.chshru.music.activity.PlayActivity;
import com.chshru.music.datautil.CtrlPlayer;
import com.chshru.music.datautil.FileCtrl;


/**
 * Created by chshru on 2017/2/25.
 */


public class PlayService extends Service {


    private MediaPlayer mPlayer;

    private IBinder binder = new PlayBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public int getAudioSessionId() {
        try {
            return mPlayer.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }

    public void seekTo(int t) {
        mPlayer.seekTo(t);
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    public int getCurDuration() {
        return mPlayer.getCurrentPosition();
    }

    public void prepare(String path) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
        mPlayer.start();
    }

    public void pause() {
        mPlayer.pause();
    }


    private void freePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        freePlayer();
    }

    class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

}

