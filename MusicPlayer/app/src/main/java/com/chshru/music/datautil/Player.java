package com.chshru.music.datautil;

import android.app.Activity;
import android.media.MediaPlayer;

import com.chshru.music.AppContext;
import com.chshru.music.service.Controller;

import java.util.ArrayList;

/**
 * Created by chshru on 2017/5/16.
 */

public class Player implements MediaPlayer.OnPreparedListener {

    private Controller mPlayer;
    private MusicList mList;
    private ArrayList<MusicListener> listener;
    private Activity mActivity;

    public Player(Controller service, MusicList list) {
        mPlayer = service;
        mPlayer.setPreparedListener(this);
        mList = list;
        listener = new ArrayList<>();

    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public int getPosition() {
        return ((AppContext) mActivity.getApplication()).getPos();
    }

    public void addMusicListener(MusicListener listener) {
        this.listener.add(listener);
    }


    public int getDuration() {
        return mPlayer.getDuration();
    }

    public int getCurDuration() {
        return mPlayer.getCurDuration();
    }

    public int getSessionId() {
        return mPlayer.getAudioSessionId();
    }

    public void seekTo(int t) {
        mPlayer.seekTo(t);
    }

    public void choose(int p) {
        ((AppContext) mActivity.getApplication()).setPos(p);
        mPlayer.pause();
        String path = mList.getList().get(p).getPath();
        prepare(path);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
        notifyChange();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void pause() {
        mPlayer.pause();
        notifyChange();
    }

    public void start() {
        mPlayer.start();
        notifyChange();
    }


    private void prepare(String path) {
        mPlayer.prepare(path);
    }


    public void next() {
        notifyChange();
    }

    public void pre() {
        notifyChange();
    }


    private void notifyChange() {
        for (MusicListener ml : listener) {
            if (ml != null) {
                ml.onPlayerPositionChange();
                ml.onPlayerStatusChange();
            }
        }
    }



    public interface MusicListener {

        void onPlayerStatusChange();

        void onPlayerPositionChange();
    }

}
