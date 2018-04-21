package com.chshru.music.view;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private int getAudioSessionIdInService() {
        try {
            return mPlayer.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }

    private void seekToInService(int t) {
        mPlayer.seekTo(t);
    }

    private boolean isPlayingInService() {
        return mPlayer.isPlaying();
    }

    private int getDurationInService() {
        return mPlayer.getDuration();
    }

    private int getCurDurationInService() {
        return mPlayer.getCurrentPosition();
    }

    private void prepareInService(String path) {
        try {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCompletionListenerInService(MediaPlayer.OnCompletionListener listener) {
        mPlayer.setOnCompletionListener(listener);
    }

    private void setPreparedListenerInService(MediaPlayer.OnPreparedListener listener) {
        mPlayer.setOnPreparedListener(listener);
    }


    private void startInService() {
        mPlayer.start();
    }

    private void pauseInService() {
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

        @Override
        public void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
            setPreparedListenerInService(listener);
        }

        @Override
        public void setCompletionListener(MediaPlayer.OnCompletionListener listener) {
            setCompletionListenerInService(listener);
        }

    }
}

