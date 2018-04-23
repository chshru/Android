package com.chshru.music.view;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.SoftReference;


/**
 * Created by chshru on 2017/2/25.
 */


public class PlayService extends Service {


    private MediaPlayer mPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        mPlayer = new MediaPlayer();
        return new PlayBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private int getAudioSessionId() {
        try {
            return mPlayer.getAudioSessionId();
        } catch (Exception e) {
            return 0;
        }
    }

    private void seekTo(int t) {
        mPlayer.seekTo(t);
    }

    private boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    private int getDuration() {
        return mPlayer.getDuration();
    }

    private int getCurDuration() {
        return mPlayer.getCurrentPosition();
    }

    private void prepare(String path) {
        try {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mPlayer.setOnCompletionListener(listener);
    }

    private void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPlayer.setOnPreparedListener(listener);
    }


    private void start() {
        mPlayer.start();
    }

    private void pause() {
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

        SoftReference<PlayService> mService;

        PlayBinder(PlayService service) {
            mService = new SoftReference<>(service);
        }

        @Override
        public int getAudioSessionId() {
            return mService.get().getAudioSessionId();
        }

        @Override
        public void seekTo(int t) {
            mService.get().seekTo(t);
        }

        @Override
        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        @Override
        public int getDuration() {
            return mService.get().getDuration();
        }

        @Override
        public int getCurDuration() {
            return mService.get().getCurDuration();
        }

        @Override
        public void prepare(String path) {
            mService.get().prepare(path);
        }

        @Override
        public void start() {
            mService.get().start();
        }

        @Override
        public void pause() {
            mService.get().pause();
        }

        @Override
        public void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
            mService.get().setPreparedListener(listener);
        }

        @Override
        public void setCompletionListener(MediaPlayer.OnCompletionListener listener) {
            mService.get().setCompletionListener(listener);
        }

    }
}

