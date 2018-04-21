package com.chshru.music.view;

import android.media.MediaPlayer;

/**
 * Created by abc on 18-4-20.
 */

public interface Controller {

    int getAudioSessionId();

    void seekTo(int t);

    boolean isPlaying();

    int getDuration();

    int getCurDuration();

    void prepare(String path);

    void start();

    void pause();

    void setPreparedListener(MediaPlayer.OnPreparedListener listener);

    void setCompletionListener(MediaPlayer.OnCompletionListener listener);
}
