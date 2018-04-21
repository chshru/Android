package com.chshru.music.datautil;

import com.chshru.music.service.Controller;

import java.util.ArrayList;

/**
 * Created by chshru on 2017/5/16.
 */

public class Player {

    private Controller mPlayer;
    private MusicList mList;
    private ArrayList<MusicListener> listener;

    public Player(Controller service, MusicList list) {
        mPlayer = service;
        mList = list;
        listener = new ArrayList<>();
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
        pause();
        String path = mList.getList().get(p).getPath();
        prepare(path);
        start();
        notifyChange(p);
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


    public void prepare(String path) {
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
                ml.onPlayerStatusChange();
            }
        }
    }

    private void notifyChange(int pos) {
        for (MusicListener ml : listener) {
            if (ml != null) {
                ml.onPlayerPositionChange(pos);
            }
        }
    }

    public interface MusicListener {

        void onPlayerStatusChange();

        void onPlayerPositionChange(int pos);
    }

}
