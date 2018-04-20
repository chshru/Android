package com.chshru.music.datautil;

import com.chshru.music.service.PlayService;

/**
 * Created by chshru on 2017/5/16.
 */

public class Player {

    private PlayService mPlayer;
    private MusicList mList;

    public Player(PlayService service, MusicList list) {
        mPlayer = service;
        mList = list;
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
    }

    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }

    }

    public void start() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }

    }


    public void prepare(String path) {
        mPlayer.prepare(path);
    }


}
