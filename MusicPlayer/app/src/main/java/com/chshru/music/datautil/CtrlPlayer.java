package com.chshru.music.datautil;

import android.content.Context;
import com.chshru.music.service.PlayService;

/**
 * Created by chshru on 2017/5/16.
 */

public class CtrlPlayer {

    private Context context;

    private CtrlPlayer(Context context) {
        this.context = context;
    }

    public static CtrlPlayer getInstance(Context context) {
        return new CtrlPlayer(context);
    }

    public int getMaxTime() {
        return PlayService.getDuration();
    }

    public int getCurTime() {
        return PlayService.getCurDuration();
    }

    public int getSessionId() {
        return PlayService.getAudioSessionId();
    }

    public void quickRun(int t) {
        PlayService.quickRun(t);
    }

    public void choose(int p) {
        if (p != Config.curPosition) {
            if (Config.musicPlaying)
                pause();
            Config.curPosition = p;
            prepare(MusicList.getInstance(context).getList().get(p).getPath());
            start();
        }
    }

    public void pause() {
        Config.musicPlaying = false;
        PlayService.pause();
    }

    public void start() {
        Config.musicPlaying = true;
        PlayService.start();
    }

    public void next() {
        change(Config.NEXT);
    }

    public void pre() {
        change(Config.PRE);
    }

    public void completion() {
        next();
    }

    public void prepare(String path) {
        PlayService.prepare(path);
        Config.musicPlaying = false;
    }

    private void change(int ride) {
        boolean preIsMusicPlay = Config.musicPlaying;
        if (Config.musicPlaying) pause();
        Config.curPosition = (Config.curPosition + ride) % MusicList.getInstance(context).getList().size();
        prepare(MusicList.getInstance(context).getList().get(Config.curPosition).getPath());
        if (preIsMusicPlay) start();
    }
}
