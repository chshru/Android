package com.chshru.game2048;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.chshru.game2048.view.Game;

/**
 * Created by chshru on 2017/11/10.
 */

public class Listener {


    private Game mGame;


    public static final int SWIPE_UP = 1;
    public static final int SWIPE_RIGHT = 2;
    public static final int SWIPE_DOWN = 3;
    public static final int SWIPE_LEFT = 4;

    public Listener(Game game) {
        mGame = game;
    }

    public View.OnTouchListener getIntorListener() {
        return mIntorListener;
    }

    public View.OnTouchListener getGameListemer() {
        return mGameListemer;
    }

    private View.OnTouchListener mIntorListener = new View.OnTouchListener() {
        private long lastTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    long time = System.currentTimeMillis();
                    if ((time - lastTime) >= 500) {
                        lastTime = time;
                        return false;
                    }
                    mGame.getAnimLayer().titleAnimation(v);
                    return true;
            }
            return true;
        }
    };

    private View.OnTouchListener mGameListemer = new View.OnTouchListener() {
        private float sx;
        private float sy;
        private long lastTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sx = event.getX();
                    sy = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    float dis = 20f;
                    float dx = event.getX() - sx;
                    float dy = event.getY() - sy;
                    if (Math.abs(dx) < dis && Math.abs(dy) < dis) {
                        long time = System.currentTimeMillis();
                        if ((time - lastTime) >= 500) {
                            lastTime = time;
                            return true;
                        }
                        mGame.doubleClick();
                        return true;
                    }
                    if (Math.abs(dx) > Math.abs(dy)) {
                        mGame.swipe(dx < 0 ? SWIPE_LEFT : SWIPE_RIGHT);
                    } else {
                        mGame.swipe(dy < 0 ? SWIPE_UP : SWIPE_DOWN);
                    }
                    break;
            }
            return true;
        }
    };


}
