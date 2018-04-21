package com.chshru.music;

import android.app.Application;

import com.chshru.music.datautil.MusicList;
import com.chshru.music.datautil.Player;

/**
 * Created by abc on 18-4-21.
 */

public class AppContext extends Application {

    private MusicList mList;
    private int pos;

    public MusicList getList() {
        return mList;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setList(MusicList list) {
        mList = list;
    }

}
