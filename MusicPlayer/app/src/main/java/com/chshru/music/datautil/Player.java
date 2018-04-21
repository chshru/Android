package com.chshru.music.datautil;

import android.media.MediaPlayer;

import com.chshru.music.service.Controller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chshru on 2017/5/16.
 */

public class Player implements MediaPlayer.OnPreparedListener {

    private Controller mPlayer;
    private MusicList mList;
    private List<MusicListener> listener;
    private int index;

    public Player(Controller service, MusicList list) {
        mPlayer = service;
        mPlayer.setPreparedListener(this);
        listener = new LinkedList<>();
        mList = list;
    }

    public void setController(Controller controller) {
        mPlayer = controller;
    }


    public int getPosition() {
        return index;
    }

    public String getCurName() {
        return mList.getList().get(index).getName();
    }

    public String getCurArtist() {
        return mList.getList().get(index).getArtist();
    }

    public void addMusicListener(MusicListener listener) {
        this.listener.add(listener);
    }

    public void removeMusicListener(MusicListener listener) {
        if (this.listener.contains(listener)) {
            this.listener.remove(listener);
        }

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
        index = p;
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
        index = (index + 1) % mList.getList().size();
        choose(index);
    }

    public void pre() {
        index = (index - 1) % mList.getList().size();
        choose(index);
    }


    private void notifyChange() {
        for (MusicListener ml : listener) {
            if (ml != null) {
                ml.onPlayerStatusChange();
            }
        }
    }


    public interface MusicListener {

        void onPlayerStatusChange();
    }

}
