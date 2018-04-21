package com.chshru.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;


/**
 * Created by chshru on 2017/2/25.
 */


public class PlayService extends Service {


    private MediaPlayer mPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        mPlayer = new MediaPlayer();
        return new PlayBinder();
    }


    public int getAudioSessionIdInService() {
        try {
            return mPlayer.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }

    public void seekToInService(int t) {
        mPlayer.seekTo(t);
    }

    public boolean isPlayingInService() {
        return mPlayer.isPlaying();
    }

    public int getDurationInService() {
        return mPlayer.getDuration();
    }

    public int getCurDurationInService() {
        return mPlayer.getCurrentPosition();
    }

    public void prepareInService(String path) {
        try {
            mPlayer.stop();
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startInService() {
        mPlayer.start();
    }

    public void pauseInService() {
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


    public class PlayBinder extends Binder implements Controller {


        @Override
        public int getAudioSessionId() {
            return getAudioSessionIdInService();
        }

        @Override
        public void seekTo(int t) {
            seekToInService(t);
        }

        @Override
        public boolean isPlaying() {
            return isPlayingInService();
        }

        @Override
        public int getDuration() {
            return getDurationInService();
        }

        @Override
        public int getCurDuration() {
            return getCurDurationInService();
        }

        @Override
        public void prepare(String path) {
            prepareInService(path);
        }

        @Override
        public void start() {
            startInService();
        }

        @Override
        public void pause() {
            pauseInService();
        }
    }

}

