package com.chshru.music;

import android.app.Application;

import com.chshru.music.util.Player;

/**
 * Created by abc on 18-4-21.
 */

public class AppContext extends Application {

    private Player mPlayer;

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }
}
