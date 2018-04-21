package com.chshru.music.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.chshru.music.view.Controller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chshru on 2017/5/16.
 */

public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    private final String indexKey = "index";


    private Controller mPlayer;
    private MusicList mList;
    private List<MusicListener> listener;
    private int index;
    private SharedPreferences sp;
    private boolean init;


    public Player(Controller service, MusicList list) {
        mPlayer = service;
        listener = new LinkedList<>();
        mPlayer.setPreparedListener(this);
        init = true;
        mList = list;
        sp = mList.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        index = sp.getInt(indexKey, 0);
    }

    public MusicList getList() {
        return mList;
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
        sp.edit().putInt(indexKey, index).apply();
        prepare(path);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!init) {
            mPlayer.start();
        } else {
            init = false;
        }
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
        int length = mList.getList().size();
        index = (index + 1) % length;
        choose(index);
    }

    public void pre() {
        int length = mList.getList().size();
        index = (index + length - 1) % length;
        choose(index);
    }


    private void notifyChange() {
        for (MusicListener ml : listener) {
            if (ml != null) {
                ml.onPlayerStatusChange();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }


    public interface MusicListener {

        void onPlayerStatusChange();
    }

}
